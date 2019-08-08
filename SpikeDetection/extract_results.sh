#!/usr/bin/env bash

# @author   Alessandra Fais
# @date     August 2019

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
            printf "${BLUE}extract from tests/output_60s/main_$nsource-1-1-1_-1.log\n\n${NORMAL}"

            # bandwidth
            grep "Average" tests/output_60s/main_$nsource-1-1-1_-1.log | awk  -F'[, ]' '{ print $11 }' >> tests/output_60s/bandwidth_$nsource-1-1.txt

            # latency
            grep "Sink" tests/output_60s/main_$nsource-1-1-1_-1.log | awk  -F'[, ]' 'FNR == 2 { print $4 " " $6 " " $8 " " $10 " " $12 " " $14 " " $16 " " $18 }' >> tests/output_60s/latency.txt
            grep "Sink" tests/output_60s/main_$nsource-1-1-1_-1.log | awk  -F'[, ]' 'FNR == 2 { print $4 }' >> tests/output_60s/latency_mean.txt

        elif [ $navg -le $NAVG_MAX ];
        then
            printf "${BLUE}extract from tests/output_60s/main_$nsource-$navg-1-1_-1.log\n\n${NORMAL}"

            # bandwidth
            grep "Average" tests/output_60s/main_$nsource-$navg-1-1_-1.log | awk  -F'[, ]' '{ print $11 }' >> tests/output_60s/bandwidth_$nsource-$navg-1.txt

            # latency
            grep "Sink" tests/output_60s/main_$nsource-$navg-1-1_-1.log | awk  -F'[, ]' 'FNR == 2 { print $4 " " $6 " " $8 " " $10 " " $12 " " $14 " " $16 " " $18 }' >> tests/output_60s/latency.txt
            grep "Sink" tests/output_60s/main_$nsource-$navg-1-1_-1.log | awk  -F'[, ]' 'FNR == 2 { print $4 }' >> tests/output_60s/latency_mean.txt

            for ndet in {2..4..2};
            do
                if [ $navg -le $((NTHREADS-nsource-ndet-1)) ];
                then
                    printf "${BLUE}extract from tests/output_60s/main_$nsource-$navg-$ndet-1_-1.log\n\n${NORMAL}"

                    # bandwidth
                    grep "Average" tests/output_60s/main_$nsource-$navg-$ndet-1_-1.log | awk  -F'[, ]' '{ print $11 }' >> tests/output_60s/bandwidth_$nsource-$navg-$ndet.txt

                    # latency
                    grep "Sink" tests/output_60s/main_$nsource-$navg-$ndet-1_-1.log | awk  -F'[, ]' 'FNR == 2 { print $4 " " $6 " " $8 " " $10 " " $12 " " $14 " " $16 " " $18 }' >> tests/output_60s/latency.txt
                    grep "Sink" tests/output_60s/main_$nsource-$navg-$ndet-1_-1.log | awk  -F'[, ]' 'FNR == 2 { print $4 }' >> tests/output_60s/latency_mean.txt
                fi
            done
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
            cat tests/output_60s/bandwidth_$nsource-1-1.txt | awk '{ sum += $1 } END { print "1 1 " sum }' >> tests/output_60s/bandwidth_$nsource.txt

        elif [ $navg -le $NAVG_MAX ];
        then
            cat tests/output_60s/bandwidth_$nsource-$navg-1.txt | awk '{ sum += $1 } END { print $navg " 1 " sum }' >> tests/output_60s/bandwidth_$nsource.txt

            for ndet in {2..4..2};
            do
                if [ $navg -le $((NTHREADS-nsource-ndet-1)) ];
                then
                    cat tests/output_60s/bandwidth_$nsource-$navg-$ndet.txt | awk '{ sum += $1 } END { print $navg " " $ndet " " sum }' >> tests/output_60s/bandwidth_$nsource.txt
                fi
            done
        fi
    done
done

#if [ ! -d tests/output_60s/logs ]; then
#    mkdir tests/output_60s/logs
#fi

#mv tests/output_60s/*.log tests/output_60s/logs/

#if [ ! -d tests/output_60s/statistics ]; then
#    mkdir tests/output_60s/statistics
#fi

#mv tests/output_60s/*.txt tests/output_60s/statistics/