#!/bin/bash
# Script to run the test
# Find all java files
find src/main/java -name "*.java" > sources.txt
find src/test/java -name "*.java" >> sources.txt

# Create bin directory if it doesn't exist
mkdir -p bin

# Compile the project
javac -d bin -cp "lib/*:resources" @sources.txt

# Run the test
java -cp "bin:lib/*:resources" ReportTest
