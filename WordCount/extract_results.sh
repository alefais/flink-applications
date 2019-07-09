#!/usr/bin/env bash

# @author   Alessandra Fais
# @date     July 2019

############################################### extract results ########################################################

printf "Extracting bandwidth and latency values for WordCount application\n"

NTHREADS=32
NSOURCE_MAX=4
for nsource in $(seq 1 $NSOURCE_MAX);
do
    NSPLIT_MAX=$((NTHREADS-nsource-nsource-1))
    for nsplit in $(seq 1 $NSPLIT_MAX);
    do
        printf "extract from tests/output_60s/main_$nsource-$nsplit-$nsource-1_10000.log\n\n"

	    # bandwidth
	    grep "Sink" tests/output_60s/main_$nsource-$nsplit-$nsource-1_10000.log | awk  -F'[, ]' 'FNR == 2 { print $15 }' >> tests/output_60s/bandwidth_words_$nsource.txt
	    grep "Sink" tests/output_60s/main_$nsource-$nsplit-$nsource-1_10000.log | awk  -F'[, ]' 'FNR == 2 { print $17 }' >> tests/output_60s/bandwidth_MB_$nsource.txt

        # latency
	    grep "Sink" tests/output_60s/main_$nsource-$nsplit-$nsource-1_10000.log | awk  -F'[, ]' 'FNR == 3 { print $10 " " $12 " " $14 " " $16 " " $18 " " $20 " " $22 " " $24 }' >> tests/output_60s/latency_$nsource.txt
    done
done