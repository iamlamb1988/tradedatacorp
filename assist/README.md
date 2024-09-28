# Assist with development

This is an assist development directory to ease troubleshooting.

## Soft Merge
This feature has been mildly tested for this project only. The script, softmerge.sh creates softlinks from several specified locations into a specified top level directory.

There are 2 implementation types:
- Weak: Places on soft links in a single directory.
- Structured: Creates folder structure relative to original specified path.

### Flags
- `d | --destination`: This flag is required and must point to an existing directory prior to command execution. This is where all soft links will be placed.
- `s | --source`: This flag specifies the source directory containing the original files. At least one valid source is required, and multiple source directories are allowed.
- `--weak`: Indicates that all soft links will be placed in the same directory (this can be problematic if files contain the same name).

### Weak method
Usage: ```bash softmerge.sh -d <--destination> -s ..<--source1> -s <--source2> --weak```
This method will place all soft link files in the directory specified by the -d | --destination flag. The --weak flag indicates this implementation.
Note: No two files can have the same name across all specified sources; otherwise, they will be overridden without warning.

Example: ```bash softmerge.sh -d weak_merge/ -s ../tradedatacorp/src/ -s ../supercompressor/src/ --weak```

### Structured Method
Usage: ```bash softmerge.sh -d <--destination> -s ..<--source1> -s <--source2>```
This method will create subdirectories in the --destination directory for each file.
Note: No two files can have the same subdirectory path and name (even if they are from completely different projects). This is an edge case that will be handled

EX: ```bash softmerge.sh -d structured_merge/ -s ../tradedatacorp/src/ -s ../supercompressor/src/```

### Additional method
In development, will handle the case where any duplicate structure is handled.

## Build

Simple XML file. This is a hardcoded file specific to it's directory. It should be placed in the same directory to compile all at once.
id argument is a second directory with a project that depends on tradedatacorp definitions.

```ant -buildfile softbuild.xml```
Will build a bin directory with the correct file structure of both directories.