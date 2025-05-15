#!/bin/bash

# Exit if any command fails
set -e

# Step 1: Clean and install without tests
echo "Running 'mvn clean -DskipTests install'..."
mvn clean -DskipTests install

# Step 2: Navigate to the docs-web directory
echo "Changing directory to docs-web..."
cd docs-web

# Step 3: Run the Jetty server
echo "Starting Jetty server with 'mvn jetty:run'..."
mvn jetty:run