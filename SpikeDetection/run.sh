#!/usr/bin/env bash

# @author   Alessandra Fais
# @date     August 2019

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

printf "${GREEN}Running Flink tests for SpikeDetection application\n${NORMAL}"

NTHREADS=32
NSOURCE_MAX=4
for nsource in $(seq 1 $NSOURCE_MAX);
do
    NAVG_MAX=$((NTHREADS-nsource-2))
    for navg in {0..29..4};
    do
        if [ $navg -eq 0 ];
        then
            printf "${BLUE}flink_spikedetection --nsource $nsource --naverage 1 --ndetector 1 --nsink 1 --rate -1\n\n${NORMAL}"

            ./run_params.sh $nsource 1 1 1
            ./run_params.sh $nsource 2 1 1
            ./run_params.sh $nsource 2 2 1
            ./run_params.sh $nsource 2 4 1
        
        elif [ $navg -le $NAVG_MAX ];
        then
            printf "${BLUE}flink_spikedetection --nsource $nsource --naverage $navg --ndetector 1 --nsink 1 --rate -1\n\n${NORMAL}"

            ./run_params.sh $nsource $navg 1 1

            for ndet in {2..4..2};
            do
                if [ $navg -le $((NTHREADS-nsource-ndet-1)) ];
                then
                    printf "${BLUE}flink_spikedetection --nsource $nsource --naverage $navg --ndetector $ndet --nsink 1 --rate -1\n\n${NORMAL}"

                    ./run_params.sh $nsource $navg $ndet 1
                fi
            done
        fi
    done
done
