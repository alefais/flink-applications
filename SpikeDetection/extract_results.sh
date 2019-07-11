#!/usr/bin/env bash

# @author   Alessandra Fais
# @date     July 2019

############################################### extract results ########################################################

printf "Extracting bandwidth and latency values for SpikeDetection application\n"

NTHREADS=32
NSOURCE_MAX=4
for nsource in $(seq 1 $NSOURCE_MAX);
do
    NAVG_MAX=$((NTHREADS-nsource-2))
    for navg in {0..$NAVG_MAX..2};
    do
        if [ $navg -eq 0 ];
        then
            printf "extract from tests/output_60s/main_$nsource-1-1_-1.log\n\n"

            # bandwidth
            grep "Average" tests/output_60s/main_$nsource-1-1_-1.log | awk  -F'[, ]' '{ print $20 }' >> tests/output_60s/bandwidth_$nsource-1.txt

            # latency
            grep "Sink" tests/output_60s/main_$nsource-1-1_-1.log | awk  -F'[, ]' 'FNR == 3 { print $10 " " $12 " " $14 " " $16 " " $18 " " $20 " " $22 " " $24 }' >> tests/output_60s/latency.txt
            grep "Sink" tests/output_60s/main_$nsource-1-1_-1.log | awk  -F'[, ]' 'FNR == 3 { print $10 }' >> tests/output_60s/latency_mean.txt

        else
            printf "extract from tests/output_60s/main_$nsource-$navg-1_-1.log\n\n"

            # bandwidth
            grep "Average" tests/output_60s/main_$nsource-$navg-1_-1.log | awk  -F'[, ]' '{ print $20 }' >> tests/output_60s/bandwidth_$nsource-$navg.txt

            # latency
            grep "Sink" tests/output_60s/main_$nsource-$navg-1_-1.log | awk  -F'[, ]' 'FNR == 3 { print $10 " " $12 " " $14 " " $16 " " $18 " " $20 " " $22 " " $24 }' >> tests/output_60s/latency.txt
            grep "Sink" tests/output_60s/main_$nsource-$navg-1_-1.log | awk  -F'[, ]' 'FNR == 3 { print $10 }' >> tests/output_60s/latency_mean.txt
        fi
    done
done

for nsource in $(seq 1 $NSOURCE_MAX);
do
    NAVG_MAX=$((NTHREADS-nsource-2))
    for navg in {0..$NAVG_MAX..2};
    do
        if [ $navg -eq 0 ];
        then
            cat tests/output_60s/bandwidth_$nsource-1.txt | awk '{ sum += $1 } END { print sum }' >> tests/output_60s/bandwidth.txt
        else
            cat tests/output_60s/bandwidth_$nsource-$navg.txt | awk '{ sum += $1 } END { print sum }' >> tests/output_60s/bandwidth.txt
        fi
    done
done