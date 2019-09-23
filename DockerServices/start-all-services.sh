#!/bin/sh

my_dir="$(dirname "$0")"

sh "$my_dir/RabbitMQ-Server-service.sh"  start
sh "$my_dir/ELKServers-service.sh"  start
sh "$my_dir/ZipkinServer-service.sh"  start
sh "$my_dir/PostgresServer-service.sh"  start
