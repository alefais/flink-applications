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

printf "${GREEN}Running Flink tests for WordCount application\n${NORMAL}"

NTHREADS=32
NSOURCE_MAX=4
for nsource in $(seq 1 $NSOURCE_MAX);
do
    for nsplit in {0..13..2};
    do
        if [ $nsplit -eq 0 ];
        then
            printf "${BLUE}flink_wordcount --nsource $nsource --nsplitter 1 --ncounter 1 --nsink 1 --rate 10000\n\n${NORMAL}"

            ./run_params.sh $nsource 1 1 1 10000

        elif [ $nsplit -ge $nsource ];
        then
            for ncount in {2..13..2};
            do
                if [ $ncount -ge $nsplit ];
                then
                    printf "${BLUE}flink_wordcount --nsource $nsource --nsplitter $nsplit --ncounter $ncount --nsink 1 --rate 10000\n\n${NORMAL}"

                    ./run_params.sh $nsource $nsplit $ncount 1 10000
                fi
            done
        fi
    done
done