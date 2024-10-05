#!/bin/bash

# Define the path to your Tomcat installation
TOMCAT_HOME="/opt/tomcat"

# Start Tomcat with sudo
sudo +x startup.sh
sudo bash "$TOMCAT_HOME/bin/startup.sh"
