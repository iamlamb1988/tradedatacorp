#!bin/bash

# $1: Destination directory
# $2: Source first directory

function create_soft_links() {
	# $1 destination merge dir
	# $2 source dir

	pathnames=$(find "$2" -type f -name '*')
	while IFS= read -r pathname; do
		ln -s $pathname $1
	done <<< "$pathnames"
}

args=("$@")
dest=$(realpath ${args[0]})

dir_arr=()
i=1
while [ $i -lt $# ]; do
	dir_arr+=($(realpath ${args[i]}))
	((i++))
done

for d in "${dir_arr[@]}"; do
	create_soft_links $dest $d
done

exit
