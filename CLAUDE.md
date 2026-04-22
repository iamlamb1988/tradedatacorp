# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Build
mvn compile

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=BinaryToolsTest

# Run a single test method
mvn test -Dtest=BinaryToolsTest#methodName

# Package
mvn package
```

Java 21, JUnit Jupiter 5.13.0. No linter is configured.

## Architecture Overview

This is a core library for ingesting, compressing, and storing OHLCV candlestick trading data. It models a "corporation" metaphor: **Miners** collect data, **Smelters** encode/decode it, **Warehouses** store it.

### Core Data Type

`StickDouble` — the fundamental unit. An interface with 6 fields: `getUTC()` (ms since epoch), `getO/H/L/C()` (prices as doubles), `getV()` (volume as double). Concrete implementations: `CandleStickDouble`, `CandleStickFixedDouble`.

### Binary File Format (`OHLCV_BinaryLexical`)

The custom compressed binary format is the heart of the library. Parsing follows a strict dependency chain:

- **H1** — fully self-describing. All H1 field widths are constants, readable with zero prior knowledge. H1 values encode the bit lengths of everything that follows.
- **H2** — each field's bit length is unknown until the corresponding H1 value has been read. H1 tells you how wide to read each H2 field before you can extract its value.
- **Data** — field bit lengths are derived from H1 and/or H2 values. Every data point in the file has **identical structure** — the header defines the schema once and it applies uniformly to all records.

Each file is self-contained and schema-independent: bit lengths for price (whole/fractional), volume (whole/fractional), UTC, symbol, and data count are all encoded in the header and may differ between files.

**Bit length determination (OPTIMAL mode):** Two-pass operation — Pass 1 reads all data points to find the max whole-number part and max fractional part (treated as an integer, e.g. `.99` → `99`); Pass 2 encodes and writes. Cannot stream-write in OPTIMAL mode. Fractional values are right-padded with zeros to match the decimal-place count of the max fractional value (e.g. `.5` → `500` if max has 3 decimal places). **FAT mode:** user manually sets bit lengths, enabling single-pass streaming writes. Setting `h1_pf_len = 0` handles whole-number-only assets like Satoshi.

The `OHLCV_BinaryLexical` class is both the schema definition and the encoder/decoder. It must be cloned when passed to a smelter. Cached headers skip re-parsing H1+H2 for repeated reads of same-schema files.

`h2_h_gap` (`H[13]`) is a pure padding field — its bit values are meaningless and discarded on read. The user sets the bit length via `h1_h_gap_len` (`H[4]`) for optional alignment. Value is irrelevant, only the length matters.

See `src/test/resources/smelter/filesmelter/README.md` for a complete bit-level breakdown of example `.brclmb` files including hex, bit index, and field color coding.

### Smelter Layer (`tradedatacorp.smelter`)

- `OHLCV_BinaryLexicalFileSmelter` — **threaded** writer. Encodes sticks to boolean arrays, then runs a 4-stage producer-consumer pipeline (crucible → hotCrucible → bitAligner → moltenData → file/string). Prefer this for bulk writes.
- `OHLCV_BinaryLexicalSmallFileSmelter` — **single-threaded** writer, prototype/reference implementation.
- `OHLCV_BinaryLexicalFileUnsmelter` — reader. Implements multiple interfaces (`FileUnsmelter`, `FileUnsmelterCachedHeader`, `FileUnsmelterPartial`, `FileUnsmelterPartialCachedHeader`) to support full reads, partial reads, and cached-header reads where the caller supplies a previously parsed header to skip re-reading it.

### Warehouse Layer (`tradedatacorp.warehouse`)

`OHLCV_BinaryWarehouse` — in-progress filesystem warehouse. Uses `OHLCV_BinaryLexical` files as its storage format. Implements `WarehouseInitializer`, `WarehousePowerable`, `WarehouseDataStorage`, `WarehouseStorer`, `WarehousePicker`. Designed to run continuously but survive restarts without corruption.

### Tools (`tradedatacorp.tools`)

- `BinaryTools` — static utilities for boolean-array ↔ numeric conversions (used throughout the binary pipeline).
- `BitByteTrack` — tracks bit/byte position during streaming reads.
- `JSON_Parser` — custom lightweight JSON parser (no exponent or unicode escape support by design).
- `tools/interval/` — generic integer-based interval utilities with no project-specific dependencies: `FixedLongInterval` (immutable interval with open/closed endpoints), `AlignedLongSet` (set of intervals snapped to a micro-interval grid, supports `addInterval` / `subtractInterval` / lazy `merge`), `LongSetRegistry` (ordered non-overlapping slots, each holding an `AlignedLongSet` coverage; used by the warehouse for tier/bucket coverage tracking).
- `tools/stick/info/` — `StickHeader`, `StickTimeFrame` metadata attached to collections of sticks.

### Miner Layer (`tradedatacorp.miner`)

`PolygonIO_CryptoMiner` implements `Miner` and ingests real-time OHLC data from polygon.io. API interfaces in `miner/web/api/` define fetch strategies by UTC range or date+size.

## Critical Invariant: Flat vs Non-Flat Arrays

Throughout the entire project, methods follow a `getXxxFlat()` / `getXxx()` naming convention:

- `getXxx()` → `boolean[][]` — one sub-array per field, in order, for easy per-field inspection
- `getXxxFlat()` → `boolean[]` — all fields concatenated left-to-right into a single array for I/O

**The invariant:** `flat.length == sum of nonflat[i].length for all i`. Flattening is a strict left-to-right drain with no reordering, no padding, and no omissions. Concretely: `flat[0] = nonflat[0][0]`, `flat[1] = nonflat[0][1]`, `flat[2] = nonflat[1][0]`, etc.

Violating this — any reordering, missing bits, or extra bits between the two forms — corrupts every file written and makes every file read produce garbage. **This breaks the entire project.**

## Key Design Patterns

- **Stateful vs stateless smelters:** `FileSmelterStateful` adds a preset `targetFile` path and a `crucible` buffer so data can be staged via `addData()` then flushed with `smeltToFile()`. Non-stateful variants take the destination as a parameter on each call.
- **CachedHeader reading:** After reading a file once, the caller can retain the parsed `boolean[][]` header and pass it back to avoid re-parsing H1/H2 on subsequent reads of same-schema files.
- **Data loss risk:** If `OHLCV_BinaryLexical` bitfield sizes are too small to represent a value, truncation occurs silently. Tests in `OHLCV_ExpectedResourceValues` define canonical byte sequences for regression testing.
