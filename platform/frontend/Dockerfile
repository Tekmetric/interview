FROM node:18-slim

LABEL maintainer="Tekmetric Site Reliability <sre@tekmetric.com>"

COPY frontend /app

RUN useradd nodeguy -d /app \
  && chown -R nodeguy:nodeguy /app \
  && cd /app \
  && npm install \
  && NODE_OPTIONS=--openssl-legacy-provider npm run build
  
USER nodeguy

WORKDIR /app

HEALTHCHECK --interval=15s --timeout=5s CMD curl http://localhost:3000/metrics || exit 1

CMD ["npm", "start"]