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

printf "${GREEN}Extracting bandwidth and latency values for WordCount application\n${NORMAL}"

NTHREADS=32
NSOURCE_MAX=4
for nsource in $(seq 1 $NSOURCE_MAX);
do
    for nsplit in {0..13..2};
    do
        if [ $nsplit -eq 0 ];
        then
            printf "${BLUE}extract from tests/output_60s/main_$nsource-1-1-1_10000.log\n\n${NORMAL}"

            # bandwidth
            grep "Sink" tests/output_60s/main_$nsource-1-1-1_10000.log | awk  -F'[, ]' 'FNR == 1 { print $9 }' >> tests/output_60s/bandwidth_words.txt
            grep "Sink" tests/output_60s/main_$nsource-1-1-1_10000.log | awk  -F'[, ]' 'FNR == 1 { print $11 }' >> tests/output_60s/bandwidth_MB.txt

            # latency
            grep "Sink" tests/output_60s/main_$nsource-1-1-1_10000.log | awk  -F'[, ]' 'FNR == 2 { print $4 " " $6 " " $8 " " $10 " " $12 " " $14 " " $16 " " $18 }' >> tests/output_60s/latency.txt
            grep "Sink" tests/output_60s/main_$nsource-1-1-1_10000.log | awk  -F'[, ]' 'FNR == 2 { print $4 }' >> tests/output_60s/latency_mean.txt

        elif [ $nsplit -ge $nsource ];
        then
            for ncount in {2..13..2};
            do
                if [ $ncount -ge $nsplit ];
                then
                    printf "${BLUE}extract from tests/output_60s/main_$nsource-$nsplit-$ncount-1_10000.log\n\n${NORMAL}"

                    # bandwidth
                    grep "Sink" tests/output_60s/main_$nsource-$nsplit-$ncount-1_10000.log | awk  -F'[, ]' 'FNR == 1 { print $9 }' >> tests/output_60s/bandwidth_words.txt
                    grep "Sink" tests/output_60s/main_$nsource-$nsplit-$ncount-1_10000.log | awk  -F'[, ]' 'FNR == 1 { print $11 }' >> tests/output_60s/bandwidth_MB.txt

                    # latency
                    grep "Sink" tests/output_60s/main_$nsource-$nsplit-$ncount-1_10000.log | awk  -F'[, ]' 'FNR == 2 { print $4 " " $6 " " $8 " " $10 " " $12 " " $14 " " $16 " " $18 }' >> tests/output_60s/latency.txt
                    grep "Sink" tests/output_60s/main_$nsource-$nsplit-$ncount-1_10000.log | awk  -F'[, ]' 'FNR == 2 { print $4 }' >> tests/output_60s/latency_mean.txt
	            fi
            done
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