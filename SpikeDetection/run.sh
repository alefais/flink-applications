#!/usr/bin/env bash

# @author   Alessandra Fais
# @date     July 2019

############################################## create test directories #################################################

if [ ! -d tests ]; then
    mkdir tests
fi
if [ ! -d tests/output_60s ]; then
    mkdir tests/output_60s
fi

#################################################### run tests #########################################################

printf "Running Flink tests for SpikeDetection application\n"

NTHREADS=32
NSOURCE_MAX=4
for nsource in $(seq 1 $NSOURCE_MAX);
do
    NAVG_MAX=$((NTHREADS-nsource-2))
    for navg in {0..$NAVG_MAX..2};
    do
        if [ $navg -eq 0 ];
        then
            printf "flink_spikedetection --nsource $nsource --naverage 1 --ndetector 1 --nsink 1 --rate -1\n\n"

            ./run_params.sh $nsource 1 1 1
        else
            printf "flink_spikedetection --nsource $nsource --naverage $navg --ndetector 1 --nsink 1 --rate -1\n\n"

            ./run_params.sh $nsource $navg 1 1
        fi
    done
done
