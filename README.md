# Trade Data Corp Core Library

This repository contains the core interfaces and foundational classes for the Trade Data application. These interfaces are designed for storing, analyzing, and generating trade patterns and summaries. Each interface is intended to have at least one example implementation, but you are encouraged to create your own implementations in separate component repositories to suit your specific needs.

Product release and install options aim for both system install and containerized releases. Containerized releases will be tailored for easy docker, Kubernetes, and helm release.

Note: Tutorials and additional documentation will be provided soon.

## Release Notes

Current Version: 0-M
This version is incomplete. Many parts are stable and tested. Most of this code is modular and easy to test.
Upon completion of first Warehouse implementation, will move forward with a component release.

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
    - Binary Files
    - CSV
	- JSON
	- XML/HTML
  - GUI images
- Tools
  - Binary Conversion Tools
  - JSON Parser

## Component Status
The structure and names may change to ensure a clean base design

| Component | Description | Status | JUnit Tests | Notes |
|-|-|-|-|-|
| **Miner** | Receives OHLC sticks from polygon.io | Working | No | Handles real-time stick data ingestion |
| **FileSmelter** | Reads/writes super-compressed binary files | Working | No | 2 File smelting implementations. Slow [non-threaded](https://github.com/iamlamb1988/tradedatacorp/blob/master/src/smelter/filesmelter/OriginalSmallFileSmelter.java), faster [threaded](https://github.com/iamlamb1988/tradedatacorp/blob/master/src/smelter/filesmelter/OriginalFileSmelter.java). |
| **Warehouses** | Database/file storage | Not Started | No | Will be started next after a few cleanup tasks (SQL, NoSQL, File System). |
| **Crafters** | Data processing and pattern generation | Not Started | No | Planned |
| **StoreFronts** | Web/App interfaces | Not Started | No | Planned (Website, App) |
| **Products** | Summaries, files (CSV, JSON, XML/HTML), GUI images | Not Started | No | Planned |
| **Binary Conversion Tool** | [Binary Tools](https://github.com/iamlamb1988/tradedatacorp/tree/master/src/tools/binarytools/BinaryTools.java) converts binary formats to/from various numeric and string formats. | Completed | Yes | Will add more features only if necessary. |
| **JSON Parser** | Custom lightweight [JSON parser](https://github.com/iamlamb1988/tradedatacorp/blob/master/src/tools/jsonparser/JSON_Parser.java) for fast parsing JSON strings. Does not handle exponents nor unicode escapes | Completed | Yes | Will add exponent handling and unicode escapes only if necessary. |

A corporation may operate multiple branches (nodes or containers), each with its own set of tangibles.
