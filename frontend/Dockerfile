# pull the official base image
FROM node:alpine
# set working direction
WORKDIR ./
# add `/app/node_modules/.bin` to $PATH
ENV PATH ./node_modules/.bin:$PATH
# install application dependencies
COPY package.json ./
COPY yarn.lock ./
RUN npm i
# add app
COPY . ./
# start app
CMD ["npm", "start"]
