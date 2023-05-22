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

## Test with curl and jq
 - to send your png image to the server run `curl -X POST "http://localhost:8080/images" -F file=@<path-to-your-file>.png`. Server will return your image id, save it for later to retrieve metadata of your image. (Your image will be saved for 10 minutes)
 - Get all available metadata: `curl http://localhost:8080/images/<your-image-id>/metadata | jq`
 - Save image from server: `curl http://localhost:8080/images/<your-image-id> | convert - <new-file-name>.png | open <new-file-name>.png`
 - Get chunks found in your image: `curl http://localhost:8080/images/<your-image-id>/metadata | jq '[.chunks[].type]'`
 - Get metadata of specific chunk: `curl http://localhost:8080/images/<your-image-id>/metadata | jq '.chunks[] | select (.type=="<chunk-name>")'`

## Architecture
<p align="center">
  <img src="https://user-images.githubusercontent.com/77827442/230593213-9ecfca96-193d-4e26-a2e0-07cd2c5cb228.png" alt='concept diagram' width="620">
</p>
