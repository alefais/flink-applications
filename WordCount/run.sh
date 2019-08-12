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

printf "${GREEN}Running Flink tests for WordCount application\n${NORMAL}"

NTHREADS=32
NSOURCE_MAX=6
for nsource in $(seq 1 $NSOURCE_MAX);
do
    NSPLIT_MAX=$((NTHREADS-nsource-2))
    for nsplit in {0..29..4};
    do
        if [ $nsplit -eq 0 ];
        then
            printf "${BLUE}flink_wordcount --nsource $nsource --nsplitter 1 --ncounter 1 --nsink 1 --rate -1\n\n${NORMAL}"

            ./run_params.sh $nsource 1 1 1
            ./run_params.sh $nsource 2 1 1
            ./run_params.sh $nsource 2 2 1
            ./run_params.sh $nsource 2 4 1

        elif [ $nsplit -le $NSPLIT_MAX ];
        then
            if [ $nsplit -gt "2" ];
            then
                ./run_params.sh $nsource $nsplit 1 1
            fi

            for ncount in {2..4..2};
            do
                if [ $nsplit -le $((NTHREADS-nsource-ncount-1)) ];
                then
                    printf "${BLUE}flink_wordcount --nsource $nsource --nsplitter $nsplit --ncounter $ncount --nsink 1 --rate -1\n\n${NORMAL}"

                    ./run_params.sh $nsource $nsplit $ncount 1
                fi
            done
        fi
    done
done