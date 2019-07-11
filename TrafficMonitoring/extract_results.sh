#!/usr/bin/env bash

# @author   Alessandra Fais
# @date     July 2019

############################################### extract results ########################################################

printf "Extracting bandwidth and latency values for TrafficMonitoring application\n"

NTHREADS=32
NSOURCE_MAX=1
for nsource in $(seq 1 $NSOURCE_MAX);
do
    NMATCH_MAX=$((NTHREADS-3))
    RATE=$((nsource*1000))
    for nmatch in $(seq 1 $NMATCH_MAX);
    do
        printf "extract from tests/output_60s/main_1-$nmatch-1-1_$RATE.log\n\n"

        # bandwidth
	    grep "MapMatch" tests/output_60s/main_1-$nmatch-1-1_$RATE.log | awk  -F'[, ]' '{ print $11 }' >> tests/output_60s/bandwidth_$RATE-$nmatch.txt

        # latency
	    grep "Sink" tests/output_60s/main_1-$nmatch-1-1_$RATE.log | awk  -F'[, ]' 'FNR == 2 { print $10 " " $12 " " $14 " " $16 " " $18 " " $20 " " $22 " " $24 }' >> tests/output_60s/latency.txt
        grep "Sink" tests/output_60s/main_1-$nmatch-1-1_$RATE.log | awk  -F'[, ]' 'FNR == 2 { print $10 }' >> tests/output_60s/latency_mean.txt
    done
done

for nsource in $(seq 1 $NSOURCE_MAX);
do
    NMATCH_MAX=$((NTHREADS-3))
    RATE=$((nsource*1000))
    for nmatch in $(seq 1 $NMATCH_MAX);
    do
        cat tests/output_60s/bandwidth_$RATE-$nmatch.txt | awk '{ sum += $1 } END { print sum }' >> tests/output_60s/bandwidth.txt
    done
done