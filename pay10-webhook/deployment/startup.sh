#!/bin/sh


nohup java -XX:+PrintFlagsFinal -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -Dspring.profiles.active=$1 -jar webhook.jar --server.port=9000  >> log.out>&1 &
echo $! > webhook.pid
