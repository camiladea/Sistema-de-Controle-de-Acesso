#!/bin/bash
# Script to compile the project
# Find all java files
find src/main/java -name "*.java" > sources.txt

# Create bin directory if it doesn't exist
mkdir -p bin

# Compile the project
javac -d bin -cp "lib/*:resources" @sources.txt
