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

printf "executing SpikeDetection --nsource $1 --naverage $2 --ndetector $3 --nsink $4 --rate -1 for 60s...\n"

flink run -c SpikeDetection.SpikeDetection target/SpikeDetection-1.0-SNAPSHOT-jar-with-dependencies.jar --nsource $1 --naverage $2 --ndetector $3 --nsink $4 &

sleep 65

printf "stopping...\n"

flink stop $(flink list | grep SpikeDetection | awk '{ print $4 }')

sleep 10

printf "saving logs...\n"

cp ~/flink-release/flink-1.7.2/log/flink-fais-taskexecutor-?-pianosau.out tests/output_60s/
mv tests/output_60s/flink-fais-taskexecutor-?-pianosau.out tests/output_60s/main_$1-$2-$3-$4_-1.log