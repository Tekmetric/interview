FROM node:22-alpine

RUN npm install -g artillery@latest

WORKDIR /app

# Copy test files
COPY quick-test.yml load-test.yml docker-test.yml load-test-processor.js ./

# Create reports directory
RUN mkdir -p artillery-reports

# Default command
CMD ["artillery", "run", "quick-test.yml"]