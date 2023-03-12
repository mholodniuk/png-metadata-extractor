## Building everything from `docker-compose.yml`

Building backend
```
cd spring-boot-api
mvn clean install -Dmaven.test.skip=true
```


Building frontend
```
cd image-analyser
npm run build --prod
```


Running docker-compose
```
docker-compose up -d
```

### backend todos:
- optimize chunk sizes - if raw bytes are too large or there is too many of them
- create spring profiles for docker-compose (prod)
- don't send all `IDAT` chunks. too much useless info -> send only few (and inform if skipped)
- interpret raw bytes - chunks
### python service todos:
- refactor to module
### frontend todos:
- update UI for deleting chunks
- style chunks
- prevent deleting critical chunks