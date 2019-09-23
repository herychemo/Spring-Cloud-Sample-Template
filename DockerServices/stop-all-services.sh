#!/bin/sh

my_dir="$(dirname "$0")"

sh "$my_dir/RabbitMQ-Server-service.sh"  stop
sh "$my_dir/ZipkinServer-service.sh" stop
sh "$my_dir/ELKServers-service.sh"  stop
sh "$my_dir/PostgresServer-service.sh" stop
