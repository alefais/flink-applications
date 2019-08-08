#!/usr/bin/env bash

# @author   Alessandra Fais
# @date     August 2019

cd SD_Flink

for i in {1..5};
do
    printf "executing run $i\n"

    ./run.sh

    mkdir tests/run_$i
    mv tests/output_60s/* tests/run_$i/
    rm -r tests/output_60s
done

cd ..