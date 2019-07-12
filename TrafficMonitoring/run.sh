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

RED=$(tput setaf 1)
GREEN=$(tput setaf 2)
BLUE=$(tput setaf 4)
MAGENTA=$(tput setaf 5)
CYAN=$(tput setaf 6)
NORMAL=$(tput sgr0)

printf "${GREEN}Running Flink tests for TrafficMonitoring application\n${NORMAL}"

NTHREADS=32
NSOURCE_MAX=1
for nsource in $(seq 1 $NSOURCE_MAX);
do
    NMATCH_MAX=$((NTHREADS-3))
    RATE=$((nsource*1000))
    for nmatch in {0..29..2};
    do
        if [ $nmatch -eq 0 ];
        then
            printf "${BLUE}flink_trafficmonitoring --nsource 1 --nmatcher 1 --ncalculator 1 --nsink 1 --rate $RATE\n\n${NORMAL}"

            ./run_params.sh 1 1 1 1 $RATE

        elif [ $nmatch -le $NMATCH_MAX ];
        then
            printf "${BLUE}flink_trafficmonitoring --nsource 1 --nmatcher $nmatch --ncalculator 1 --nsink 1 --rate $RATE\n\n${NORMAL}"

            ./run_params.sh 1 $nmatch 1 1 $RATE
        else
            printf "${BLUE}storm_trafficmonitoring --nsource 1 --nmatcher 29 --ncalculator 1 --nsink 1 --rate $RATE\n\n${NORMAL}"

            ./run_params.sh 1 29 1 1 $RATE
        fi
    done
done
