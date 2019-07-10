#!/usr/bin/env bash

# @author   Alessandra Fais
# @date     July 2019

###################################################### single test ###########################################################################

# clean environment
~/flink-release/flink-1.7.2/bin/stop-cluster.sh
rm -f ~/flink-release/flink-1.7.2/log/*

sleep 5

# start a new TaskManager
~/flink-release/flink-1.7.2/bin/start-cluster.sh

sleep 10

printf "executing TrafficMonitoring --nsource $1 --nmatcher $2 --ncalculator $3 --nsink $4 --rate $5 for 60s...\n"

flink run -c TrafficMonitoring.TrafficMonitoring target/TrafficMonitoring-1.0-SNAPSHOT-jar-with-dependencies.jar --nsource $1 --nmatcher $2 --ncalculator $3 --nsink $4 --rate $5 &

sleep 65

printf "stopping...\n"

flink stop $(flink list | grep TrafficMonitoring | awk '{ print $4 }')

sleep 20

printf "saving logs...\n"

cp ~/flink-release/flink-1.7.2/log/flink-fais-taskexecutor-?-pianosau.out tests/output_60s/
mv tests/output_60s/flink-fais-taskexecutor-?-pianosau.out tests/output_60s/main_$1-$2-$3-$4_$5.log