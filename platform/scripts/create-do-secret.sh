#!/bin/bash

set -euo pipefail


if [ -z "$DIGITALOCEAN_TOKEN" ]; then
    echo "Error: DIGITALOCEAN_TOKEN environment variable is not set"
    echo "Please source your .envrc or export DIGITALOCEAN_TOKEN"
    exit 1
fi

# Create the secret in kube-system namespace
kubectl create secret generic digitalocean-dns \
    --from-literal=token="$DIGITALOCEAN_TOKEN" \
    -n kube-system \
    --dry-run=client -o yaml | kubectl apply -f -

echo "Secret 'digitalocean-dns' created/updated in kube-system namespace"
