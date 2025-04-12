#!/bin/sh

echo "Checking if uv is installed..."
if ! command -v uv >/dev/null 2>&1; then
		echo "uv is not installed. Installing."
		curl -LsSf https://astral.sh/uv/install.sh | sh
fi

echo "Done"

exit 0
