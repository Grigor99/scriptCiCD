#!/bin/bash

# Define variables
JAR_NAME="deploy-0.0.1-SNAPSHOT.jar"
EC2_INSTANCE_IP="54.165.139.173"
EC2_INSTANCE_USER="495642364690"
EC2_INSTANCE_KEY_FILE_PATH="/Users/gmartirosyan/Downloads/deploy/myCiCdkeyPair.pem"

PID=$(ssh -i "$EC2_INSTANCE_KEY_FILE_PATH" "ubuntu@ec2-54-90-141-30.compute-1.amazonaws.com" "sudo lsof -t -i:8080")

# If the PID is not empty, stop the process
if [ -n "$PID" ]; then
    echo "Stopping previously running Java process (PID: $PID) on port 8080..."
    ssh -i "$EC2_INSTANCE_KEY_FILE_PATH" "ubuntu@ec2-54-90-141-30.compute-1.amazonaws.com" "sudo kill $PID"
    sleep 5
fi

# Build the project with Maven
mvn clean install

echo "copying jar into remote host"
# Copy the JAR file to the EC2 instance
scp -i "$EC2_INSTANCE_KEY_FILE_PATH" target/"$JAR_NAME" "ubuntu@ec2-54-90-141-30.compute-1.amazonaws.com":"/home/ubuntu/"
echo "starting app..."

# Connect to the EC2 instance and start the application
ssh -i "$EC2_INSTANCE_KEY_FILE_PATH" "ubuntu@ec2-54-90-141-30.compute-1.amazonaws.com" "java -jar /home/ubuntu/$JAR_NAME"

