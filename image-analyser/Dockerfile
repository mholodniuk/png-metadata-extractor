FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY dist/image-analyser /usr/share/nginx/html

# commands to run
# docker build -t project-name .
# docker run --rm -it -p 8080:80 project-name