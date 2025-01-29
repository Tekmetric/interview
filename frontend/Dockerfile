# Use Node.js 18 with Alpine for a lightweight build
FROM node:18-alpine AS builder
WORKDIR /app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm install

# Copy the rest of the application code and build it
COPY . .
RUN npm run build

# Create a new stage for running the application
FROM node:18-alpine AS runner
WORKDIR /app

# Copy built application from the builder stage
COPY --from=builder /app ./
# Install concurrently to run multiple commands
RUN npm install -g concurrently
# Create a symlink to avoid issues with xdg-open
RUN ln -sf /bin/true /usr/bin/xdg-open

# Set environment variables for the application
ENV HOST=0.0.0.0
ENV STORYBOOK_HOST=0.0.0.0
ENV BROWSER=none
ENV CI=true

# Expose ports for the application and Storybook
EXPOSE 3000
EXPOSE 6006

# Start the application and Storybook concurrently
CMD ["sh", "-c", "concurrently 'npm run start' 'npm run storybook -- --ci'"]