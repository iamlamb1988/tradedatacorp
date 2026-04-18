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

The custom compressed binary format is the heart of the library. Files have two sections:

- **Header (H1 + H2):** H1 is fixed-width fields encoding bitfield sizes (e.g. how many bits represent price whole-part, fractional-part, UTC, volume, symbol, data count). H2 is variable-width fields whose lengths are defined by H1 (symbol string, data count, alignment gap).
- **Data:** Packed bit-fields for each stick, with no padding between records. Each stick stores UTC + O/H/L/C/V split into whole and fractional integer parts.

The `OHLCV_BinaryLexical` class is both the schema definition and the encoder/decoder. It must be cloned when passed to a smelter.

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
- `tools/time/` — `TimeTier`, `FixedInterval`, `MonthInterval`, `QuarterInterval`, `TimeRange`, `TimeChunkTracker` for time-bucketing logic used by the warehouse.
- `tools/stick/info/` — `StickHeader`, `StickTimeFrame` metadata attached to collections of sticks.

### Miner Layer (`tradedatacorp.miner`)

`PolygonIO_CryptoMiner` implements `Miner` and ingests real-time OHLC data from polygon.io. API interfaces in `miner/web/api/` define fetch strategies by UTC range or date+size.

## Key Design Patterns

- **Stateful vs stateless smelters:** `FileSmelterStateful` adds a preset `targetFile` path and a `crucible` buffer so data can be staged via `addData()` then flushed with `smeltToFile()`. Non-stateful variants take the destination as a parameter on each call.
- **CachedHeader reading:** After reading a file once, the caller can retain the parsed `boolean[][]` header and pass it back to avoid re-parsing H1/H2 on subsequent reads of same-schema files.
- **Data loss risk:** If `OHLCV_BinaryLexical` bitfield sizes are too small to represent a value, truncation occurs silently. Tests in `OHLCV_ExpectedResourceValues` define canonical byte sequences for regression testing.
