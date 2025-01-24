#!/bin/bash

# Check for correct number of arguments
if [ "$#" -ne 2 ]; then
  echo "Usage: $0 input_file.csv split_size"
  exit 1
fi

# Input parameters
input_file=$1
split_size=$2

# Validate that the input file exists
if [ ! -f "$input_file" ]; then
  echo "Error: File '$input_file' not found!"
  exit 1
fi

# Validate that split size is a positive integer
if ! [[ "$split_size" =~ ^[0-9]+$ ]] || [ "$split_size" -le 0 ]; then
  echo "Error: Split size must be a positive integer."
  exit 1
fi

# Extract the header from the input file
header=$(head -n 1 "$input_file")

# Remove the header from the file and process the remaining rows
tail -n +2 "$input_file" | split -l "$split_size" -d --additional-suffix=.csv - "$input_file"_

# Add the header to each generated file
for split_file in "$input_file"_*.csv; do
  {
    echo "$header"
    cat "$split_file"
  } > "${split_file}.tmp"
  mv "${split_file}.tmp" "$split_file"
done

# Rename the files to match the format `input_1.csv`, `input_2.csv`, etc.
count=1
for split_file in "$input_file"_*.csv; do
  mv "$split_file" "${input_file%.*}_$count.csv"
  ((count++))
done

echo "Files generated successfully!"