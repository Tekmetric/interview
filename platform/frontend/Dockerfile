FROM node:20-alpine AS base

# Add metadata labels
LABEL maintainer="platform-team"
LABEL description="Frontend application with React and Express"

WORKDIR /usr/src/app

# Copy package files for better layer caching
COPY package*.json ./
RUN npm install 

# Copy source code
COPY . .

# Build the React application
RUN npm run build

# Production stage
FROM node:20-alpine

# Add metadata labels
LABEL maintainer="platform-team"
LABEL description="Frontend application production image"

WORKDIR /usr/src/app

# Copy package files
COPY package*.json ./

RUN npm install --omit=dev  && \
    npm cache clean --force && \
    rm -rf /tmp/*

# Copy built application from build stage
# Note: node user already exists in node:20-alpine image
COPY --from=base --chown=node:node /usr/src/app/build ./build
COPY --from=base --chown=node:node /usr/src/app/server.js ./server.js

# Set production environment
ENV NODE_ENV=production 

# Switch to non-root user (node user already exists in node:20-alpine)
USER node

# Use exec form for proper signal handling
CMD ["npm", "start"]