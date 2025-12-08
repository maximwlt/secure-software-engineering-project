FROM node:25.2.1-alpine AS build

COPY package*.json /app/
WORKDIR /app

RUN npm install

COPY . .

RUN npm run build

FROM nginx:1.29.3-alpine
COPY --from=build /app/dist /usr/share/nginx/html

# COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx","-g","daemon off;"]