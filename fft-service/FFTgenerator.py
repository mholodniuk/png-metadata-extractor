import logging
from bson import ObjectId
import bson
import pika
import cv2 as cv
import numpy as np
from pymongo import MongoClient


EXCHANGE_NAME = 'image_to_convert_exchange'
QUEUE_NAME = 'image_to_convert_queue'
ROUTING_KEY = 'routing-key-fft'
HOSTNAME = 'rabbit'
MONGO_URI = 'mongodb://mongo:27017'
DATABASE_NAME = 'mongo-test'
COLLECTION_NAME = 'image'

class rabbitMQServer():
    def __init__(self, queue, host, routing_key, mongo_uri, exchange=''):
        self.queue = queue
        self.host = host
        self.routing_key = routing_key
        self.exchange = exchange
        self.mongo_client = MongoClient(mongo_uri)
        self.start_server()

    def start_server(self):
        self.create_channel()
        self.create_exchange()
        self.create_bind()
        logging.info("Channel created...")

    def create_channel(self):
        parameters = pika.ConnectionParameters(self.host)
        self._connection = pika.BlockingConnection(parameters)
        self._channel = self._connection.channel()

    def create_exchange(self):
        self._channel.exchange_declare(
            exchange=self.exchange,
            durable=False,
            auto_delete=False
        )
        self._channel.queue_declare(queue=self.queue)

    def create_bind(self):
        self._channel.queue_bind(
            queue=self.queue,
            exchange=self.exchange,
        )

    @staticmethod
    def callback(channel, method, properties, body, mongo_collection):
        logging.info(f'Consumed message {body.decode()} from queue')
        logging.info(f'Channel {channel}')
        
        id = body.decode('utf-8')
        document = mongo_collection.find_one({'_id': ObjectId(id)})

        image = np.asarray(bytearray(document['bytes']), dtype="uint8")
        image = cv.imdecode(image, cv.IMREAD_ANYCOLOR)
        gray = cv.cvtColor(image, cv.COLOR_BGR2GRAY)
        fourier = cv.dft(np.float32(gray), flags=cv.DFT_COMPLEX_OUTPUT)
        fourier_shift = np.fft.fftshift(fourier)

        magnitude = 20 * np.log(cv.magnitude(fourier_shift[:, :, 0], fourier_shift[:, :, 1]))
        magnitude = cv.normalize(magnitude, None, 0, 255, cv.NORM_MINMAX, cv.CV_8UC1)

        _, image_bytes = cv.imencode('.png', magnitude)
        image_bytes = image_bytes.tobytes()

        result = mongo_collection.update_one(filter={
            '_id': ObjectId(id)
        }, update={
            '$set': {
                'magnitude': bson.Binary(image_bytes)
            }
        })
        logging.info(f'affected documents: {result.matched_count}')

    def get_messages(self):
        try:
            logging.info("Starting the server...")
            mongo_collection = self.mongo_client[DATABASE_NAME][COLLECTION_NAME]
            self._channel.basic_consume(
                queue=self.queue,
                on_message_callback=lambda ch, method, properties, body: 
                    rabbitMQServer.callback(ch, method, properties, body, mongo_collection),
                auto_ack=True
            )
            self._channel.start_consuming()
            logging.info("Server finished")
        except Exception as e:
            logging.debug(f'Exception: {e}')


if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO)
    server = rabbitMQServer(QUEUE_NAME, HOSTNAME, ROUTING_KEY, MONGO_URI, EXCHANGE_NAME)
    server.get_messages()
