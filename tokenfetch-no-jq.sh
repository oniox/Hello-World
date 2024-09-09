#!/bin/bash

# Set the necessary variables
TOKEN_ENDPOINT="https://your-oauth-server.com/token"
CLIENT_ID="your_client_id"
CLIENT_SECRET="your_client_secret"
SCOPE="your_requested_scope"

# Fetch the access token
response=$(curl -X POST   -H "Content-Type: application/x-www-form-urlencoded"   -d "grant_type=client_credentials"   -d "client_id=$CLIENT_ID"   -d "client_secret=$CLIENT_SECRET"   -d "scope=$SCOPE"   "$TOKEN_ENDPOINT")

# Extract the access token from the response
access_token=$(echo $response | grep -o -E '"access_token":"[^"]*"' | cut -d':' -f2 | tr -d '"')

# Check if the token was successfully fetched
if [ -n "$access_token" ]; then
  echo "Access token: $access_token"
else
  echo "Failed to fetch access token."
  exit 1
fi