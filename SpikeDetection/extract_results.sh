#!/usr/bin/env bash

# @author   Alessandra Fais
# @date     July 2019

############################################### extract results ########################################################

RED=$(tput setaf 1)
GREEN=$(tput setaf 2)
BLUE=$(tput setaf 4)
MAGENTA=$(tput setaf 5)
CYAN=$(tput setaf 6)
NORMAL=$(tput sgr0)

printf "${GREEN}Extracting bandwidth and latency values for SpikeDetection application\n${NORMAL}"

NTHREADS=32
NSOURCE_MAX=4
for nsource in $(seq 1 $NSOURCE_MAX);
do
    NAVG_MAX=$((NTHREADS-nsource-2))
    for navg in {0..29..2};
    do
        if [ $navg -eq 0 ];
        then
            printf "${BLUE}extract from tests/output_60s/main_$nsource-1-1_-1.log\n\n${NORMAL}"

            # bandwidth
            grep "Average" tests/output_60s/main_$nsource-1-1_-1.log | awk  -F'[, ]' '{ print $20 }' >> tests/output_60s/bandwidth_$nsource-1.txt

            # latency
            grep "Sink" tests/output_60s/main_$nsource-1-1_-1.log | awk  -F'[, ]' 'FNR == 3 { print $10 " " $12 " " $14 " " $16 " " $18 " " $20 " " $22 " " $24 }' >> tests/output_60s/latency.txt
            grep "Sink" tests/output_60s/main_$nsource-1-1_-1.log | awk  -F'[, ]' 'FNR == 3 { print $10 }' >> tests/output_60s/latency_mean.txt

        elif [ $navg -le $NAVG_MAX ];
        then
            printf "${BLUE}extract from tests/output_60s/main_$nsource-$navg-1_-1.log\n\n${NORMAL}"

            # bandwidth
            grep "Average" tests/output_60s/main_$nsource-$navg-1_-1.log | awk  -F'[, ]' '{ print $20 }' >> tests/output_60s/bandwidth_$nsource-$navg.txt

            # latency
            grep "Sink" tests/output_60s/main_$nsource-$navg-1_-1.log | awk  -F'[, ]' 'FNR == 3 { print $10 " " $12 " " $14 " " $16 " " $18 " " $20 " " $22 " " $24 }' >> tests/output_60s/latency.txt
            grep "Sink" tests/output_60s/main_$nsource-$navg-1_-1.log | awk  -F'[, ]' 'FNR == 3 { print $10 }' >> tests/output_60s/latency_mean.txt

        else
            printf "${BLUE}extract from tests/output_60s/main_$nsource-29-1_-1.log\n\n${NORMAL}"

            # bandwidth
            grep "Average" tests/output_60s/main_$nsource-29-1_-1.log | awk  -F'[, ]' '{ print $20 }' >> tests/output_60s/bandwidth_$nsource-29.txt

            # latency
            grep "Sink" tests/output_60s/main_$nsource-29-1_-1.log | awk  -F'[, ]' 'FNR == 3 { print $10 " " $12 " " $14 " " $16 " " $18 " " $20 " " $22 " " $24 }' >> tests/output_60s/latency.txt
            grep "Sink" tests/output_60s/main_$nsource-29-1_-1.log | awk  -F'[, ]' 'FNR == 3 { print $10 }' >> tests/output_60s/latency_mean.txt

        fi
    done
done

for nsource in $(seq 1 $NSOURCE_MAX);
do
    NAVG_MAX=$((NTHREADS-nsource-2))
    for navg in {0..29..2};
    do
        if [ $navg -eq 0 ];
        then
            cat tests/output_60s/bandwidth_$nsource-1.txt | awk '{ sum += $1 } END { print sum }' >> tests/output_60s/bandwidth.txt
        elif [ $navg -lt $NAVG_MAX ];
        then
            cat tests/output_60s/bandwidth_$nsource-$navg.txt | awk '{ sum += $1 } END { print sum }' >> tests/output_60s/bandwidth.txt
        else
            cat tests/output_60s/bandwidth_$nsource-29.txt | awk '{ sum += $1 } END { print sum }' >> tests/output_60s/bandwidth.txt
        fi
    done
done

if [ ! -d tests/output_60s/logs ]; then
    mkdir tests/output_60s/logs
fi

mv tests/output_60s/*.log tests/output_60s/logs/

if [ ! -d tests/output_60s/statistics ]; then
    mkdir tests/output_60s/statistics
fi

mv tests/output_60s/*.txt tests/output_60s/statistics/