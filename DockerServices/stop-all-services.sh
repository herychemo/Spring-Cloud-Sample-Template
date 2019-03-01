#!/bin/sh

my_dir="$(dirname "$0")"

"$my_dir/ZipkinServer-service.sh" stop
"$my_dir/PostgresServer-service.sh" stop
