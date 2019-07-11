#!/usr/bin/env bash

# @author   Alessandra Fais
# @date     July 2019

############################################### extract results ########################################################

printf "Extracting bandwidth and latency values for WordCount application\n"

NTHREADS=32
NSOURCE_MAX=4
NSPLIT_MAX=8
NCOUNT_MAX=8
for nsource in $(seq 1 $NSOURCE_MAX);
do
    # NSPLIT_MAX=$((NTHREADS-nsource-nsource-1))
    for nsplit in $(seq $nsource $NSPLIT_MAX);
    do
        for ncount in $(seq $nsplit $NCOUNT_MAX);
        do
            printf "extract from tests/output_60s/main_$nsource-$nsplit-$ncount-1_10000.log\n\n"

            # bandwidth
            grep "Sink" tests/output_60s/main_$nsource-$nsplit-$ncount-1_10000.log | awk  -F'[, ]' 'FNR == 1 { print $9 }' >> tests/output_60s/bandwidth_words.txt
            grep "Sink" tests/output_60s/main_$nsource-$nsplit-$ncount-1_10000.log | awk  -F'[, ]' 'FNR == 1 { print $11 }' >> tests/output_60s/bandwidth_MB.txt

            # latency
            grep "Sink" tests/output_60s/main_$nsource-$nsplit-$ncount-1_10000.log | awk  -F'[, ]' 'FNR == 2 { print $10 " " $12 " " $14 " " $16 " " $18 " " $20 " " $22 " " $24 }' >> tests/output_60s/latency.txt
            grep "Sink" tests/output_60s/main_$nsource-$nsplit-$ncount-1_10000.log | awk  -F'[, ]' 'FNR == 2 { print $10 }' >> tests/output_60s/latency_mean.txt
	    done
	done
done
