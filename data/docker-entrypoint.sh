#!/bin/bash

# check to see if this file is being run or sourced from another script
_is_sourced() {
	# https://unix.stackexchange.com/a/215279
	[ "${#FUNCNAME[@]}" -ge 2 ] &&
		[ "${FUNCNAME[0]}" = '_is_sourced' ] &&
		[ "${FUNCNAME[1]}" = 'source' ]
}

_main() {
	# if first arg looks like a flag, assume we want to run pose matching server
	if [ "${1:0:1}" = '-' ]; then
		set -- run "$@"
	fi

	if [ "$1" = 'run' ]; then
	  shift
		cmdline="python3 -m tekmetric_data"
		exec $cmdline "$@"
	else
		exec "$@"
	fi
}

if ! _is_sourced; then
	_main "$@"
fi
