# Build for React frontend
# Multi Stages

# Stage 1: Build stage
FROM node:16-alpine AS build
WORKDIR /app
COPY package.json yarn.lock ./
RUN yarn install --frozen-lockfile
COPY . .
RUN yarn build

# Stage 2: Production stage (no react dev server in k8s, will use nginx to server the application)
FROM nginx:alpine AS production
# Copy built assets from build stage
COPY --from=build /app/build /usr/share/nginx/html

# Expose port 80//container port
EXPOSE 80

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
