#!bin/bash

# $1: Destination directory
# $2: Source first directory

#Init globals and define functions
SOURCE_DIRS=()

#Weak, will not work if any file will have the same name accross any source.
function create_soft_links() {
	# $1 destination merge dir
	# $2 source dir containing original files
	dest=$(realpath $1)
	src=$(realpath $2)

	local file_arr=()
	readarray -t file_arr < <(find $src -type f -name '*')

	filename_regex='[^\/]*$'

	for f in "${file_arr[@]}"; do
		# echo "ln -s $f $dest"
		if [[ "$f" =~ $filename_regex ]]; then
			filename="${BASH_REMATCH[0]}"
		fi
		ln -s $f $dest/$filename
	done
}

function create_structured_soft_links() {
 	# $1 destination merge dir
	# $2 source dir containing original files

	dest_abs_dir=$(realpath $1)
	source_abs_dir=$(realpath $2)

	filename_regex='[^\/]*$'

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
		-s|--source)
			if [[ -d $arg ]]; then
				SOURCE_DIRS+=($(realpath $arg))
			else
				echo "$arg not a directory... skipping."
			fi
			i=$next_i
		;;
		--weak)
			IS_WEAK=1
		;;
	esac
done

#0.2 Validate user input
if [[ ! -d $DESTINATION ]]; then
	echo "Destination: $DESTINATION not a directory"
	exit 1
fi

#1. Execute
if [[ "$IS_WEAK" -eq 1 ]]; then
	echo "Weak method creating soft links."
	for d in "${SOURCE_DIRS[@]}"; do
		echo "calling weak function..."
		create_soft_links $DESTINATION $d
	done
else
	echo "Creating structured soft links."
	for d in "${SOURCE_DIRS[@]}"; do
		create_structured_soft_links $DESTINATION $d
	done
fi

exit 0