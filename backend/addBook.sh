#!/bin/bash
curl -X POST -H "Content-Type: application/json" \
     -d '{"title": "Sample Book", "author": "Sample Author"}' \
     http://localhost:8080/api/books

