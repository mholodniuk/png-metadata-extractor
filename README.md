

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

`TODO`
