#!/bin/bash

set -euo pipefail
GIT_HOST=git.127-0-0-1.sslip.io

NODEIP=$(kubectl get node -ojson | jq -r .items[0].status.addresses[0].address)
git remote add gitea git@$GIT_HOST:chazu/interview.git
