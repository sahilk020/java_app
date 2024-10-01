#!/bin/sh


PID=$(cat webhook.pid)
kill -9 $PID
