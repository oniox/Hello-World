#!/bin/bash

# Function to display usage information
usage() {
    echo "Usage: $0 --name <name> --age <age>"
    exit 1
}

# Parse named arguments
while [[ "$#" -gt 0 ]]; do
    case $1 in
        --name) NAME="$2"; shift ;;
        --age) AGE="$2"; shift ;;
        *) usage ;;
    esac
    shift
done

# Check if required arguments are provided
if [ -z "$NAME" ] || [ -z "$AGE" ]; then
    usage
fi

# Use the arguments
echo "Name: $NAME"
echo "Age: $AGE"
