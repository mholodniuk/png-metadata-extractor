

Running docker images (mongodb + rabbitmq)
```
docker-compose up -d
```

Running frontend (dev)
```
ng serve -o
```

Building frontend
```
cd image-analyser
npm run build --prod
```

Running frontend container
```
docker build -t fronetend .
docker run --rm -p 8081:80 frontend
```

Running python magnitude spectrum service
```
cd magnitude-spectrum-service
python receiver.py
```

Running backend:

```
mvn clean install
mvn spring-boot:run
```

### backend todos:
- optimize chunk sizes - if raw bytes are too large or there is too many of them
- create spring profiles for docker-compose (prod)
- don't send all `IDAT` chunks. too much useless info -> send only few (and inform if skipped)
- interpret raw bytes - chunks
## python service todos:
- refactor to module
### frontend todos:
- create UI for deleting chunks
- style chunks
- enable scaling image after loading it
- 