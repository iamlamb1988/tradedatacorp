# Assist with development

This is an assist development directory to ease troubleshooting.
Because this package is a dependancy for add-on projects, the script should be used to compile all .class files into a single directory in order to get clean error messages and simplify debugging without having to move files.

## Soft Merge
UNTESTED but useful

This script is used create soft link of several files by specifiying:
- First Argument: Destination directory: Where all the soft links will go.
- Next Arguments: The directory for which all files will be have soft links recursively
At least 2 arguments are required. There can be as many directories as possible but duplicates are not recommended.

Likely to be problems if a Java files have the same name.

## Build

Simple XML file. It should be placed in the same directory to compile all at once.

## Examples
A directory called "merged_java" is created and the softmerge.sh, build.xml script are placed in.
The script is called as such:
```bash softmerge.sh . ../tradedatacorp/src /root/supercompressor/src```

The first argument is "." the current directory (That is the merged_java)
The second argument is the the "src" directory where all .java files are located
The thrid argument is a second directory with a project that depends on tradedatacorp definitions.

```ant compile```
Will build a bin directory with the correct file structure of both directories.