#!/bin/bash
# Replace the {id} with the ID of the book you want to update
curl -X PUT -H "Content-Type: application/json" \
     -d '{"title": "Updated Book Title", "author": "Updated Author"}' \
     http://localhost:8080/api/books/1

