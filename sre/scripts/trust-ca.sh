#!/usr/bin/env bash
# Extract the demo CA cert from the cluster and trust it in the macOS
# keychain. Optional convenience: avoids "not secure" browser warnings
# when hitting the *.sre-demo.local ingresses.
set -euo pipefail

TMP=$(mktemp -t sre-demo-ca).crt

kubectl -n cert-manager get secret sre-demo-ca-key-pair \
  -o jsonpath='{.data.ca\.crt}' | base64 -d > "$TMP"

echo "adding CA to macOS login keychain (requires sudo)..."
sudo security add-trusted-cert -d -r trustRoot \
  -k /Library/Keychains/System.keychain "$TMP"

rm -f "$TMP"
echo "done. Restart your browser to pick up the new trust."
