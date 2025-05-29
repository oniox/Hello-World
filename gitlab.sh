curl --request PUT \
  --header "PRIVATE-TOKEN: <your_private_token>" \
  --header "Content-Type: application/json" \
  --data '{ "labels": "in progress,frontend" }' \
  "<your_gitlab_instance_url>/api/v4/projects/<project_id>/issues/<issue_iid>"
