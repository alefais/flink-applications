#!/usr/bin/env bash

# @author   Alessandra Fais
# @date     July 2019

############################################### generate pardeg ########################################################

RED=$(tput setaf 1)
GREEN=$(tput setaf 2)
BLUE=$(tput setaf 4)
MAGENTA=$(tput setaf 5)
CYAN=$(tput setaf 6)
NORMAL=$(tput sgr0)

printf "${GREEN}Generate parallelism degrees for nodes in WordCount pipeline\n${NORMAL}"

for nsource in $(seq 1 4);
do
    for nsplit in {0..13..2};
    do
        if [ $nsplit -eq 0 ];
        then
            echo $nsource >> source.txt
            echo $nsource >> splitter.txt
            echo $nsource >> counter.txt
        elif [ $nsplit -ge $nsource ];
        then
            for ncount in {2..13..2};
            do
                if [ $ncount -ge $nsplit ];
                then
                    echo $nsource >> source.txt
                    echo $nsplit >> splitter.txt
                    echo $ncount >> counter.txt
                fi
            done
        fi
    done
done
