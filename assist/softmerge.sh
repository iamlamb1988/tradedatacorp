#!/bin/bash

# author Bruce Lamb
# since 21 OCT 2024

# Init globals and define functions
# Parallel arrays Soft Link locations to be stored.
DEST_DIRS=()
SOURCE_DIRS=()

# Parallel arrays for dirs that require Destination concatenation
PREFIX_DEST_DIRS=()
POSTFIX_DEST_DIRS=()

NO_PREFIX_DEST_DIRS=()

function create_structured_soft_links() {
 	# $1 destination merge dir
	# $2 source dir containing original files

	local dest_abs_dir=$(realpath $1)
	local source_abs_dir=$(realpath $2)

	local filename_regex='[^\/]*$'

	local file_arr=()
	readarray -t file_arr < <(find $2 -type f -name '*')

	for f in "${file_arr[@]}"; do
		pathname=$(realpath "$f") #Absolute file name
		subpath="${pathname##$source_abs_dir/}" #NOTE: contains Pathname

		#Should ALWAYS match a filename correctly..
		if [[ "$subpath" =~ $filename_regex ]]; then
			filename="${BASH_REMATCH[0]}"
		fi
		subpath="${subpath%%/$filename}"
		dest_path="$dest_abs_dir/$subpath"
		dest_pathname="$dest_path/$filename"

		if [[ ! -d "$dest_path" ]];then
			mkdir -p $dest_path
		fi

		ln -s $pathname $dest_path/$filename
	done
}

#0.1 Take all user inputs
for ((i=1; i <= $#; i++)); do
    flag="${!i}"
    next_i=$((i + 1))
    arg="${!next_i}"

	case $flag in
		-d|--destination)
			DESTINATION=$arg
			i=$next_i
		;;
		-pd|--prefix-destination)
			# Cannot process DESTINATION yet.. (may not exist)
			arg1=$arg
			arg2=$((next_i + 1)) # Prefix directory
			arg2=${!arg2}		 # Source directory

			if [[ -d $arg2 ]]; then
				prefix_dir=${arg1%/}

				PREFIX_DEST_DIRS+=($prefix_dir)
				POSTFIX_DEST_DIRS+=($(realpath $arg2))
			else
				echo "$arg2 not a directory... skipping prefix."
			fi

			next_i=$((i+2))
		;;
		-s|--source)
			if [[ -d $arg ]]; then
				NO_PREFIX_DEST_DIRS+=($(realpath $arg))
			else
				echo "$arg not a directory... skipping."
			fi
			i=$next_i
		;;
	esac
done

#0.2 Validate user input
if [[ ! -d $DESTINATION ]]; then
	echo "Destination: $DESTINATION not a directory"
	exit 1
fi

#0.3 Add non prefix directories
for src in "${NO_PREFIX_DEST_DIRS[@]}"; do
	DEST_DIRS+=("$DESTINATION")
	SOURCE_DIRS+=("$src")
done

#0.4 Add prefixed directories
for ((i=0; i < ${#PREFIX_DEST_DIRS[@]}; i++)); do
	destination="$DESTINATION/${PREFIX_DEST_DIRS[$i]}"
	mkdir -p "$destination"

	DEST_DIRS+=("$destination")
	SOURCE_DIRS+=("${POSTFIX_DEST_DIRS[$i]}")
done

#1. Execute
echo "Source dir count: ${#SOURCE_DIRS[@]}"
for ((i=0; i < ${#SOURCE_DIRS[@]}; i++)); do
	echo "Creating soft link(s) from ${SOURCE_DIRS[$i]} to ${DEST_DIRS[$i]}"
	create_structured_soft_links "${DEST_DIRS[$i]}" "${SOURCE_DIRS[$i]}" 
done

exit 0