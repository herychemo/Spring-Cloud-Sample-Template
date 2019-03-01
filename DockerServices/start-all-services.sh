#!/bin/sh

my_dir="$(dirname "$0")"

"$my_dir/ZipkinServer-start.sh"
"$my_dir/PostgresServer-start.sh"
