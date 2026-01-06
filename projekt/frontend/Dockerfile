FROM node:25.2.1-alpine AS build


WORKDIR /app

COPY package*.json ./

RUN npm install

RUN apk update
RUN apk add openssl

# Create self-signed ssl certificate
RUN mkdir ssl
RUN openssl genrsa -out ssl/note-app.key 2048
RUN openssl req -new -key ssl/note-app.key -out ssl/note-app.csr -batch

RUN openssl genrsa -out ssl/ca.key 4096
RUN openssl req -new -x509 -key ssl/ca.key -out ssl/ca.crt -batch
RUN openssl x509 -req -in ssl/note-app.csr -CA ssl/ca.crt -CAkey ssl/ca.key -CAcreateserial -out ssl/note-app.crt

COPY . .

RUN npm run build

FROM nginx:1.29.3-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=build /app/dist /usr/share/nginx/html
COPY --from=build /app/ssl /app/ssl

CMD ["nginx","-g","daemon off;"]