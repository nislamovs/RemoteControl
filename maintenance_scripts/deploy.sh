#!/usr/bin/env bash

echo -en "Deploying..."
rsync ../target/java.test-1.0-SNAPSHOT.jar /home/perkele/Desktop/
echo -e "done!";