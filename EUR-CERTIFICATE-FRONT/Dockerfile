FROM node:18.14-alpine
WORKDIR /OnsimaWeb/onsimaWeb
COPY package*.json ./
RUN npm install
COPY . .
RUN  npm run build
EXPOSE 3220
CMD ["npm", "start"]
