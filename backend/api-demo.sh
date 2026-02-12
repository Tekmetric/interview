#/bin/bash

# put space between curl output and whatever comes next since it lacks a newline
function spacer {
  printf '\n\n'
}

xtrace() (
  set -x
  "${@}"
)

api_base=http://localhost:8080/api
content_header='Content-Type: application/json'

echo "Get a list of users at api/users:"
xtrace curl -X GET ${api_base}/users; spacer

echo "Get a single user at api/users/{id}:"
xtrace curl -X GET ${api_base}/users/1; spacer

echo "Add a user with a POST to api/users:"
xtrace curl -X POST ${api_base}/users -H "${content_header}" -d '{"name": "Another", "surname": "User", "email": "auser@email.com"}'; spacer

echo "All users:"
xtrace curl -X GET ${api_base}/users; spacer

echo "Edit a user:"
xtrace curl -X GET ${api_base}/users/1; spacer
echo "Updating email.."
xtrace curl -X PUT ${api_base}/users/1 -H "${content_header}" -d '{"name": "Bob", "surname": "Smith", "email": "bobsmith@email.com"}'; spacer

echo "Endpoints validate input:"
xtrace curl -X POST ${api_base}/users -H "${content_header}" -d '{"name": "", "email": "myemail.com"}'; spacer
