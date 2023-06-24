# PNG file metadata extractor

Currently supported chunks: bKGD, cHRM, gAMA, IHDR, pHYs, PLTE, tEXt, tIME, zTXt

See more about PNG format in [documentation](https://www.w3.org/TR/png/)

## Demo

### Main menu
<img width="1438" alt="Main menu" src="https://github.com/mholodniuk/png-metadata-extractor/assets/77827442/a419a625-31d7-411c-9368-4bdc8f6453a2">

### Displaying chunk information
<img width="1439" alt="Displaying information about chunk" src="https://github.com/mholodniuk/png-metadata-extractor/assets/77827442/9db38cea-90bb-443c-8154-39bbfe7f9a9b">

### PLTE chunk
<img width="1187" alt="PLTE chunk" src="https://github.com/mholodniuk/png-metadata-extractor/assets/77827442/fe91b170-4eb2-4861-97e6-0154d19e24ca">

### tIME & tEXt chunks
<img width="1440" alt="tIME & tEXt chunks" src="https://github.com/mholodniuk/png-metadata-extractor/assets/77827442/628d50e2-b724-41f1-bdc8-146d749e296d">

### Generating magnitude spectrum
<img width="1436" alt="Generating magnitude spectrum" src="https://github.com/mholodniuk/png-metadata-extractor/assets/77827442/d224d1db-d54d-49f7-bcb4-7f4d57c8c20e">

## Building instructions

Build backend
```
cd api
mvn clean install
```

Build frontend
```
cd frontend
npm run build --prod
```

Run in docker-compose
```
docker-compose build
docker-compose up -d
```

## Test with curl and jq
 - to upload your png image to the server run `curl -X POST "http://localhost:8080/images" -F file=@<path-to-your-file>.png`. Server will return your image id, save it for later to retrieve metadata of your image. (Your image will be saved for 10 minutes)
 - Get all available metadata: `curl http://localhost:8080/images/<your-image-id>/metadata | jq`
 - Save image from server: `curl http://localhost:8080/images/<your-image-id> | convert - <new-file-name>.png | open <new-file-name>.png`
 - Get chunks found in your image: `curl http://localhost:8080/images/<your-image-id>/metadata | jq '[.chunks[].type]'`
 - Get metadata of specific chunk: `curl http://localhost:8080/images/<your-image-id>/metadata | jq '.chunks[] | select (.type=="<chunk-name>")'`

## Architecture
<p align="center">
  <img src="https://user-images.githubusercontent.com/77827442/230593213-9ecfca96-193d-4e26-a2e0-07cd2c5cb228.png" alt='concept diagram' width="620">
</p>
