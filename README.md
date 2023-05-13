# PNG file metadata extractor

Currently available chunks: bKGD, cHRM, gAMA, IHDR, pHYs, PLTE, tEXt, tIME, zTXt

See more about PNG format in [documentation](https://www.w3.org/TR/png/)



## Building instructions

Building backend
```
cd spring-boot-api
mvn clean install
```

Building frontend
```
cd image-analyser
npm run build --prod
```
* these steps are to be replaced with Jenkins build pipeline (?)

Running docker-compose
```
docker-compose build
docker-compose up -d
```

### Architecture
<p align="center">
  <img src="https://user-images.githubusercontent.com/77827442/230593213-9ecfca96-193d-4e26-a2e0-07cd2c5cb228.png" alt='concept diagram' width="620">
</p>


### todos
- gray images fail to generate fft (???)
