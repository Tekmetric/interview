#!/bin/bash
# Replace the {id} with the ID of the book you want to delete
curl -X DELETE http://localhost:8080/api/books/1
