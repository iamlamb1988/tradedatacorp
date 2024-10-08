# Assist with development

This is an assist development directory to ease troubleshooting.

## Soft Merge
The script, softmerge.sh creates softlinks from several specified locations into a specified top level directory.

### Flags
- `d | --destination`: This flag is required and must point to an existing directory prior to command execution. This is where all soft links will be placed.
- `pd | --prefix-destination`: Two paremeter flag, the first parameter will be the prefix directory of --destination (will be created at destination location if it does not exist). The second parameter is the source directory.
- `s | --source`: The source directory will be have shortcuts at destination with no prefix.

### Usage
Usage: ```bash softmerge.sh -d <--destination> -pd <prefix-dir1> <source1> -pd <prefix-dir2> <source2>```
Will create directories and softlinks from all sources as such:
"<--destination>/<prefix-dir1>/<source1>"
"<--destination>/<prefix-dir2>/<source2>"

Example: ```bash softmerge.sh -d merged -pd tradesrc tradedatacorp/src -pd compsrc supercompressor/src```
Will create directories and softlinks from all sources as such:
NOTE: "<sourceN>" is ont part of the path, only it's recursive contents.
"merged/tradesrc/<dir structure from tradedatacorp/src>"
"merged/compsrc/<dir structure from supercompressor/src>"

You may use the same prefix for each source, be cautios if a separate source happens to have same file name with same directory structure, one of those links will be overriden with no warning or indication. Giving each source a separate prefix will guarantee each link is created and preserved.

Also use -s to place a source directly at destination with no prefix.
Usage: ```bash softmerge.sh -d <--destination> -s <source1> -pd <prefix-dir2> <source2>```
Similar result, using -s instead of -dp for source1 and prefix1. Prefix1 is empty.
"merged/<dir structure from tradedatacorp/src>"
"merged/compsrc/<dir structure from supercompressor/src>"

Example: ```bash softmerge.sh -d merged -s tradedatacorp/src -pd compsrc supercompressor/src```

## Build

Simple XML file. This is a hardcoded file specific to it's directory. It should be placed in the same directory to compile all at once.
id argument is a second directory with a project that depends on tradedatacorp definitions.

```ant -buildfile softbuild.xml```
Will build a bin directory with the correct file structure of both directories.
Will be modified to allow user to speficy destination of compiled class files.