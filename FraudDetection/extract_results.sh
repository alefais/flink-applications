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

printf "${GREEN}Extracting bandwidth and latency values for FraudDetection application\n${NORMAL}"

NTHREADS=32
NSOURCE_MAX=4
for nsource in $(seq 1 $NSOURCE_MAX);
do
    NPRED_MAX=$((NTHREADS-nsource-1))
    for npred in {0..30..2};
    do
        if [ $npred -eq 0 ];
        then
            printf "${BLUE}extract from tests/output_60s/main_$nsource-1-1_-1.log\n\n${NORMAL}"

            # bandwidth
            grep "Predictor" tests/output_60s/main_$nsource-1-1_-1.log | awk  -F'[, ]' '{ print $14 }' >> tests/output_60s/bandwidth_$nsource-1.txt

            # latency
            grep "Sink" tests/output_60s/main_$nsource-1-1_-1.log | awk  -F'[, ]' 'FNR == 2 { print $10 " " $12 " " $14 " " $16 " " $18 " " $20 " " $22 " " $24 }' >> tests/output_60s/latency.txt
            grep "Sink" tests/output_60s/main_$nsource-1-1_-1.log | awk  -F'[, ]' 'FNR == 2 { print $10 }' >> tests/output_60s/latency_mean.txt

        elif [ $npred -le $NPRED_MAX ];
        then
            printf "${BLUE}extract from tests/output_60s/main_$nsource-$npred-1_-1.log\n\n${NORMAL}"

            # bandwidth
            grep "Predictor" tests/output_60s/main_$nsource-$npred-1_-1.log | awk  -F'[, ]' '{ print $14 }' >> tests/output_60s/bandwidth_$nsource-$npred.txt

            # latency
            grep "Sink" tests/output_60s/main_$nsource-$npred-1_-1.log | awk  -F'[, ]' 'FNR == 2 { print $10 " " $12 " " $14 " " $16 " " $18 " " $20 " " $22 " " $24 }' >> tests/output_60s/latency.txt
            grep "Sink" tests/output_60s/main_$nsource-$npred-1_-1.log | awk  -F'[, ]' 'FNR == 2 { print $10 }' >> tests/output_60s/latency_mean.txt
        fi
    done
done

for nsource in $(seq 1 $NSOURCE_MAX);
do
    NPRED_MAX=$((NTHREADS-nsource-1))
    for npred in {0..30..2};
    do
        if [ $npred -eq 0 ];
        then
            cat tests/output_60s/bandwidth_$nsource-1.txt | awk '{ sum += $1 } END { print sum }' >> tests/output_60s/bandwidth.txt
        elif [ $npred -le $NPRED_MAX ];
        then
            cat tests/output_60s/bandwidth_$nsource-$npred.txt | awk '{ sum += $1 } END { print sum }' >> tests/output_60s/bandwidth.txt
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