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

printf "executing FraudDetection --nsource $1 --npredictor $2 --nsink $3 --rate -1 for 60s...\n"

flink run -c FraudDetection.FraudDetection target/FraudDetection-1.0-SNAPSHOT-jar-with-dependencies.jar --nsource $1 --npredictor $2 --nsink $3 &

sleep 65

printf "stopping...\n"

flink stop $(flink list | grep FraudDetection | awk '{ print $4 }')

sleep 10

printf "saving logs...\n"

cp ~/flink-release/flink-1.7.2/log/flink-fais-taskexecutor-?-pianosau.out tests/output_60s/
mv tests/output_60s/flink-fais-taskexecutor-?-pianosau.out tests/output_60s/main_$1-$2-$3-$4_-1.log