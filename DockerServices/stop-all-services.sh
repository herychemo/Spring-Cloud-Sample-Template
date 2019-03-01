#!/bin/sh

my_dir="$(dirname "$0")"

"$my_dir/ZipkinServer-stop.sh"
"$my_dir/PostgresServer-stop.sh"
