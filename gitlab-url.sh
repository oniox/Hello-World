#!/bin/bash

# GitLab Issues Fetcher Script
# Fetches URLs of GitLab issues with specific labels and assignee

# Configuration
GITLAB_URL="https://gitlab.com"  # Change to your GitLab instance URL
PROJECT_ID=""                    # Set your project ID
GITLAB_TOKEN=""                  # Set your GitLab personal access token

# Check if required variables are set
if [[ -z "$PROJECT_ID" ]]; then
    echo "Error: PROJECT_ID is not set. Please set your GitLab project ID."
    echo "You can find it in your project's Settings > General page."
    exit 1
fi

if [[ -z "$GITLAB_TOKEN" ]]; then
    echo "Error: GITLAB_TOKEN is not set. Please set your GitLab personal access token."
    echo "Create one at: User Settings > Access Tokens"
    exit 1
fi

# API endpoint
API_ENDPOINT="${GITLAB_URL}/api/v4/projects/${PROJECT_ID}/issues"

# Parameters for the API call
LABELS="status=ready for work"
ASSIGNEE="Franklin"

echo "Fetching GitLab issues..."
echo "Project ID: $PROJECT_ID"
echo "Labels: $LABELS"
echo "Assignee: $ASSIGNEE"
echo "----------------------------------------"

# Make the API call
response=$(curl -s \
    --header "PRIVATE-TOKEN: $GITLAB_TOKEN" \
    --get \
    --data-urlencode "labels=$LABELS" \
    --data-urlencode "assignee_username=$ASSIGNEE" \
    --data-urlencode "state=opened" \
    "$API_ENDPOINT")

# Check if curl was successful
if [[ $? -ne 0 ]]; then
    echo "Error: Failed to make API request to GitLab"
    exit 1
fi

# Check if response is valid JSON and contains issues
if ! echo "$response" | jq empty 2>/dev/null; then
    echo "Error: Invalid JSON response from GitLab API"
    echo "Response: $response"
    exit 1
fi

# Parse and display the issue URLs
issue_count=$(echo "$response" | jq length)

if [[ $issue_count -eq 0 ]]; then
    echo "No issues found matching the criteria."
else
    echo "Found $issue_count issue(s):"
    echo ""
    
    # Extract and display issue URLs and titles
    echo "$response" | jq -r '.[] | "• \(.title)\n  URL: \(.web_url)\n"'
    
    echo "----------------------------------------"
    echo "Issue URLs only:"
    echo "$response" | jq -r '.[].web_url'
fi