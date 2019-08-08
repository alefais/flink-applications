#!/usr/bin/env bash

# @author   Alessandra Fais
# @date     July 2019

NTHREADS=32
NSOURCE_MAX=4
for nsource in $(seq 1 $NSOURCE_MAX);
do
    NAVG_MAX=$((NTHREADS-nsource-2))
    for navg in {0..29..2};
    do
        if [ $navg -eq 0 ];
        then
            echo $nsource >> source.txt
            echo "1" >> average.txt
            echo "1" >> detector.txt
        elif [ $navg -le $NAVG_MAX ];
        then
            echo $nsource >> source.txt
            echo $navg >> average.txt
            echo "1" >> detector.txt

            for ndet in {2..8..2};
            do
                if [ $navg -le $((NTHREADS-nsource-ndet-1)) ];
                then
                    echo $nsource >> source.txt
                    echo $navg >> average.txt
                    echo $ndet >> detector.txt
                fi
            done
        fi
    done
done
