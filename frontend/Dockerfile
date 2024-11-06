FROM node:14-bullseye-slim AS builder
WORKDIR /app
RUN chown node:node /app
COPY --chown=node:node . .
USER node
RUN npm install --only=production && yarn build
FROM node:14-bullseye-slim
RUN apt-get update && apt-get install -y --no-install-recommends dumb-init && npm install --global serve
WORKDIR /app
RUN chown node:node /app
COPY --from=builder --chown=node:node /app/build ./build
USER node
ENV NODE_ENV=production
ENTRYPOINT ["/usr/bin/dumb-init", "--"]
CMD ["serve", "-s", "build"]
