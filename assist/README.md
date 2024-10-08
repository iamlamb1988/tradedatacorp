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

### Generic Build
-Dsrc is the single source dir containing all soft links.
-Dbin is the destination where .class files will be generated.

Ant tool executes ./ current directory relative to the actual location of file instead of pwd. When specifiying location of file and destination of file.
```ant -buildfile tradedatacorp/assist/softbuild.xml -Dsrc=$(pwd)/merged -Dbin=$(pwd)/bin compile```

To clean, must also specify bin
```ant -buildfile tradedatacorp/assist/softbuild.xml -Dbin=$(pwd)/bin clean```

### Simple Build.
Will need to actually copy and move the assist directory to location of merged links. Default files will be ./src and ./bin
```ant -buildfile softbuild.xml```
