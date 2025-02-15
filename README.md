# Trade Data Corp

This is the core of Trade Data application that contains the interfaces required for storing, analyzing, and generating trade patterns and summaries.
Generally, there will be at least one implementation of each interface that can be used as an example. It is expected to use implementations in different component repositories.
This will allow you to make your own implementation of certain components.

Tutorials will come soon

## Release Notes

Current Version: 0-M
This version is incomplete and unstable. Will Upgrade to 0-RC upon reaching minimum viable product status.
Component repositories may or may not work due to not in sync and not in stable state.

Completed class BinaryTools and documentation. No exception checking for this class (This is intentional).

Will focus Original Lexical documentation and remaining test cases.

More information on components will be released in future.

## Introduction

This abstract library behaves as a single "business corporation". A corporation has fundamental information such as a name and Headquarter address.
A corporation has tangibles such as:
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

A corporation may have more than branch (i.e Node (VM or Physical Server) or Container) that contain their own tangibles.
