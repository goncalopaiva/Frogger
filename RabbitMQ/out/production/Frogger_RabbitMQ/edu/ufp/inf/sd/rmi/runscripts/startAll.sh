#!/usr/bin/env bash
trap cleanup 1 2 3 6

cleanup (){
  pkill -f 'python'
  pkill -f 'rmiregistry'
  pkill -f 'java'
}

#compile
compile(){
  javac -d . ../server/*.java  ../client/*.java ../../util/rmisetup/*.java
}

#runpython
run_python(){
    sh _1_runpython.sh &
}

#rmiregisty
start_registry(){
  pkill -f 'rmiregistry'
  sh _2_runregistry.sh &
}

#server
start_server(){
  sh _3_runserver.sh &
}

#client
start_client(){
  sh _4_runclient.sh &
}

cleanup
compile
sleep 1
run_python
sleep 2
start_registry
sleep 2
start_server
sleep 2
start_client
#sleep 1
#start_client

