# create output directory
if [ ! -d output_fd60s ]; then 
	mkdir output_fd60s
fi

##################################################################### test $1 $2 $3 -1 #########################################################################################

# HOW TO USE: pass to the tests the following positional arguments: nsource, npredictor, nsink, rate

# clean environment
~/flink-release/flink-1.7.2/bin/stop-cluster.sh
rm -f ~/flink-release/flink-1.7.2/log/*

sleep 5

# start a new TaskManager
~/flink-release/flink-1.7.2/bin/start-cluster.sh

sleep 5

printf "executing FraudDetection $1 $2 $3 -1 for 60s...\n"

flink run -c FraudDetection.FraudDetection target/FraudDetection-1.0-SNAPSHOT-jar-with-dependencies.jar --file ~/data/app/fd/credit-card.dat --nsource $1 --npredictor $2 --nsink $3 --rate -1 &

sleep 65

printf "stopping...\n"

flink stop $(flink list | grep FraudDetection | awk '{ print $4 }')

sleep 5

printf "saving logs...\n"

cp ~/flink-release/flink-1.7.2/log/flink-fais-taskexecutor-0-pianosau.out output_fd60s/
mv output_fd60s/flink-fais-taskexecutor-0-pianosau.out output_fd60s/main_$1$2$3-1.log