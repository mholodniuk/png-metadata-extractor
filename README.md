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
docker-compose build
docker-compose up -d
```


<p align="center">
  <img src="https://user-images.githubusercontent.com/77827442/230593213-9ecfca96-193d-4e26-a2e0-07cd2c5cb228.png" alt='concept diagram' width="620">
</p>
