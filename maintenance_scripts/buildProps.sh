#!/usr/bin/env bash

echo "application.environment=Production" > be/src/main/resources/build.properties
echo "application.version=v 1.0.0" >> be/src/main/resources/build.properties
echo "application.deploymentdate=$(date '+%Y-%m-%d %H:%M:%S')" >> be/src/main/resources/build.properties