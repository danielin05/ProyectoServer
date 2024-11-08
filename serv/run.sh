#!/bin/bash

# run.sh

# Change the working directory to where the script is located
cd "$(dirname "$0")"

# Set MAVEN_OPTS environment variable
export MAVEN_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED"

# Check for the first argument and set it as the main class, or exit if not provided
if [ -z "$1" ]; then
  echo "Error: No main class specified."
  exit 1
fi

mainClass="$1"
shift # Remove the first argument (mainClass) so the rest can be passed to Maven

echo "Setting MAVEN_OPTS to: $MAVEN_OPTS"
echo "Main Class: $mainClass"

# Construct Maven argument for the main class
mavenMainClassArg="-Dexec.mainClass=$mainClass"

# Check if there are any remaining arguments
if [ "$#" -gt 0 ]; then
  # Join the arguments into a single string, properly escaped
  javaArgsStr="$*"
  execArgsForJava="-Dexec.args=\"$javaArgsStr\""
else
  execArgsForJava=""
fi

echo "Maven Main Class Argument: $mavenMainClassArg"
echo "Java Program Arguments: $javaArgsStr"

# Execute Maven commands
mvn clean compile test

# Run the Maven exec command with conditional arguments
if [ -n "$execArgsForJava" ]; then
  mvn exec:java -PrunMain $mavenMainClassArg $execArgsForJava
else
  mvn exec:java -PrunMain $mavenMainClassArg
fi
