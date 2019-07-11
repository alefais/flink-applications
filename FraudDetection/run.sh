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

printf "Running Flink tests for FraudDetection application\n"

NTHREADS=32
NSOURCE_MAX=4
for nsource in $(seq 1 $NSOURCE_MAX);
do
    NPRED_MAX=$((NTHREADS-nsource-1))
    for npred in {0..$NPRED_MAX..2};
    do
        if [ $npred -eq 0 ];
        then
            printf "flink_frauddetection --nsource $nsource --npred 1 --nsink 1 --rate -1\n\n"

            ./run_params.sh $nsource 1 1
        else
            printf "flink_frauddetection --nsource $nsource --npred $npred --nsink 1 --rate -1\n\n"

            ./run_params.sh $nsource $npred 1
        fi
    done
done
