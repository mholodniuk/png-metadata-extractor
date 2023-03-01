

Running docker images (mongodb + rabbitmq)
```
docker-compose up -d
```

Building frontend
```
cd image-analyser
npm run build --prod
```

Running frontend
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

`TODO`