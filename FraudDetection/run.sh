#!/usr/bin/env bash

# Flink tests bandwidth

./run_params.sh 1 1 1
./run_params.sh 1 2 1
./run_params.sh 1 4 1
./run_params.sh 1 8 1
./run_params.sh 1 14 1

./run_params.sh 2 4 1
./run_params.sh 2 8 1
./run_params.sh 2 13 1

./run_params.sh 4 8 1
./run_params.sh 4 11 1
./run_params.sh 5 10 1
