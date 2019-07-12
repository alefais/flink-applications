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

printf "${GREEN}Extracting bandwidth and latency values for TrafficMonitoring application\n${NORMAL}"

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
            printf "${BLUE}extract from tests/output_60s/main_1-1-1-1_$RATE.log\n\n${NORMAL}"

            # bandwidth
            grep "MapMatch" tests/output_60s/main_1-1-1-1_$RATE.log | awk  -F'[, ]' '{ print $11 }' >> tests/output_60s/bandwidth_$RATE-1.txt

            # latency
            grep "Sink" tests/output_60s/main_1-1-1-1_$RATE.log | awk  -F'[, ]' 'FNR == 2 { print $4 " " $6 " " $8 " " $10 " " $11 " " $12 " " $14 " " $16 " " $18 }' >> tests/output_60s/latency.txt
            grep "Sink" tests/output_60s/main_1-1-1-1_$RATE.log | awk  -F'[, ]' 'FNR == 2 { print $4 }' >> tests/output_60s/latency_mean.txt

        elif [ $nmatch -lt $NMATCH_MAX ];
        then
            printf "${BLUE}extract from tests/output_60s/main_1-$nmatch-1-1_$RATE.log\n\n${NORMAL}"

            # bandwidth
            grep "MapMatch" tests/output_60s/main_1-$nmatch-1-1_$RATE.log | awk  -F'[, ]' '{ print $11 }' >> tests/output_60s/bandwidth_$RATE-$nmatch.txt

            # latency
            grep "Sink" tests/output_60s/main_1-$nmatch-1-1_$RATE.log | awk  -F'[, ]' 'FNR == 2 { print $4 " " $6 " " $8 " " $10 " " $11 " " $12 " " $14 " " $16 " " $18 }' >> tests/output_60s/latency.txt
            grep "Sink" tests/output_60s/main_1-$nmatch-1-1_$RATE.log | awk  -F'[, ]' 'FNR == 2 { print $4 }' >> tests/output_60s/latency_mean.txt

        else
            printf "${BLUE}extract from tests/output_60s/main_1-29-1-1_$RATE.log\n\n${NORMAL}"

            # bandwidth
            grep "MapMatch" tests/output_60s/main_1-29-1-1_$RATE.log | awk  -F'[, ]' '{ print $11 }' >> tests/output_60s/bandwidth_$RATE-29.txt

            # latency
            grep "Sink" tests/output_60s/main_1-29-1-1_$RATE.log | awk  -F'[, ]' 'FNR == 2 { print $4 " " $6 " " $8 " " $10 " " $11 " " $12 " " $14 " " $16 " " $18 }' >> tests/output_60s/latency.txt
            grep "Sink" tests/output_60s/main_1-29-1-1_$RATE.log | awk  -F'[, ]' 'FNR == 3 { print $4 }' >> tests/output_60s/latency_mean.txt
        fi
    done
done

for nsource in $(seq 1 $NSOURCE_MAX);
do
    NMATCH_MAX=$((NTHREADS-3))
    RATE=$((nsource*1000))
    for nmatch in {0..29..2};
    do
        if [ $nmatch -eq 0 ];
        then
            cat tests/output_60s/bandwidth_$RATE-1.txt | awk '{ sum += $1 } END { print sum }' >> tests/output_60s/bandwidth.txt

        elif [ $nmatch -lt $NMATCH_MAX ];
        then
            cat tests/output_60s/bandwidth_$RATE-$nmatch.txt | awk '{ sum += $1 } END { print sum }' >> tests/output_60s/bandwidth.txt

        else
            cat tests/output_60s/bandwidth_$RATE-29.txt | awk '{ sum += $1 } END { print sum }' >> tests/output_60s/bandwidth.txt
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