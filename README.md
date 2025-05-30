# Trade Data Corp Core Library

This repository contains the core interfaces and foundational classes for the Trade Data application. These interfaces are designed for storing, analyzing, and generating trade patterns and summaries. Each interface is intended to have at least one example implementation, but you are encouraged to create your own implementations in separate component repositories to suit your specific needs.

Note: Tutorials and additional documentation will be provided soon.

## Release Notes

Current Version: 0-M
This version is incomplete and unstable. Will Upgrade to 0-RC upon reaching minimum viable product status.
Component repositories may or may not work due to not in sync and not in stable state.

Completed class BinaryTools and documentation. No exception checking for this class (This is intentional).
Completed StickDouble Interface, CandleStickDouble class, and CandleStickFixedDouble class documentation.

Will focus Original Lexical documentation and remaining test cases.

More information on components will be released in future.

## Introduction

This abstract library models a business corporation, encapsulating fundamental information such as the company name and headquarters address. The corporation consists of various tangible components, including:
- Workers
  - Miners
  - Smelters
  - Crafters
- Warehouses
  - Database
	- SQL
	- NoSQL
	- File System
- StoreFronts
  - Website
  - App
- Products
  - Summaries
  - Files
    - CSV
	- JSON
	- XML/HTML
  - GUI images

## Component Status
The structure and names may change to ensure a clean base design

| Component         | Description                                              | Status         | Notes                                             |
|-------------------|---------------------------------------------------------|----------------|---------------------------------------------------|
| **Miner**         | Receives OHLC sticks from polygon.io                    | Working     | Handles real-time stick data ingestion            |
| **FileSmelter**   | Reads/writes super-compressed binary files              | Working     | Supports both read and write operations           |
| **Smelter**       | Data transformation/aggregation                         | In Progress | Basic file smelting implemented                   |
| **Crafters**      | Data processing and pattern generation                  | Not Started | Planned                                          |
| **Warehouses**    | Database/file storage                                   | Not Started | Planned (SQL, NoSQL, File System)                 |
| **StoreFronts**   | Web/App interfaces                                      | Not Started | Planned (Website, App)                            |
| **Products**      | Summaries, files (CSV, JSON, XML/HTML), GUI images      | Not Started | Planned                                          |

A corporation may operate multiple branches (nodes or containers), each with its own set of tangibles.
