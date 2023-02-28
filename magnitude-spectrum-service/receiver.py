import base64
import io
from bson import ObjectId
import bson
import pika, sys, os
import cv2 as cv
import numpy as np
from pymongo import MongoClient
from PIL import Image

exchange_name = 'image_to_convert_exchange'
queue_name = 'image_to_convert_queue'
routing_key = 'routing-key-fft'


def main(client: MongoClient):
    db = client['mongo-test']
    collection = db['image']
    
    connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()
    channel.queue_bind(exchange=exchange_name, queue=queue_name)

    def callback(ch, method, properties, body: bytes):
        id = body.decode('utf-8')
        document = collection.find_one({ '_id': ObjectId(id) })
        print(f'Received ID: {id}')
        print(document['fileName'])

        image = np.asarray(bytearray(document['bytes']), dtype="uint8")
        image = cv.imdecode(image, cv.IMREAD_ANYCOLOR)

        gray = cv.cvtColor(image, cv.COLOR_BGR2GRAY)

        fourier = cv.dft(np.float32(gray), flags=cv.DFT_COMPLEX_OUTPUT)
        fourier_shift = np.fft.fftshift(fourier)

        magnitude = 20 * np.log(cv.magnitude(fourier_shift[:,:,0], fourier_shift[:,:,1]))
        magnitude = cv.normalize(magnitude, None, 0, 255, cv.NORM_MINMAX, cv.CV_8UC1)

        _, image_bytes = cv.imencode('.png', magnitude)
        image_bytes = image_bytes.tobytes()
        # img_bytes = cv.imencode('.png', magnitude)[1].tobytes()

        result = collection.update_one(filter = {
            '_id': ObjectId(id)
        }, update = {
            '$set': {
                'magnitude': bson.Binary(image_bytes)
            }
        })
        print(f'affected documents: {result.matched_count}')
        
        if cv.imwrite(filename=f'./generated/{id}.png', img=magnitude):
            print('saved file')
        else:
            print('did not save file')

    channel.basic_consume(queue=queue_name, on_message_callback=callback, auto_ack=True)

    print(' [*] Waiting for messages. To exit press CTRL+C')
    channel.start_consuming()

if __name__ == '__main__':
    try:
        mongo_client = MongoClient("mongodb://localhost:27017")
        print("Connection Successful")
        main(client=mongo_client)
    except KeyboardInterrupt:
        print('Interrupted')
        mongo_client.close()
        try:
            sys.exit(0)
        except SystemExit:
            os._exit(0)