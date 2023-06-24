import io
import logging
from bson import ObjectId
import bson
import pika
from PIL import Image
import numpy as np
from pymongo import MongoClient


EXCHANGE_NAME = 'image_to_convert_exchange'
QUEUE_NAME = 'image_to_convert_queue'
RABBIT_HOSTNAME = 'rabbit'
MONGO_URI = 'mongodb://mongo:27017'
DATABASE_NAME = 'png'
COLLECTION_NAME = 'images'

class rabbitMQServer():
    def __init__(self, queue, host, mongo_uri, exchange=''):
        self.queue = queue
        self.host = host
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
        
        id = body.decode('utf-8')
        document = mongo_collection.find_one({'_id': ObjectId(id)})

        image = Image.open(io.BytesIO(document['bytes'])).convert('L')

        image_array = np.array(image)
        fft_result = np.fft.fft2(image_array)
        fft_result_shift = np.fft.fftshift(fft_result)

        magnitude_spectrum = np.asarray(20*np.log10(np.abs(fft_result_shift)), dtype=np.uint8) 
        phase_spectrum = np.asarray(np.angle(fft_result_shift), dtype=np.uint8)

        magnitude_image = Image.fromarray(np.uint8(magnitude_spectrum))
        phase_image = Image.fromarray(np.uint8(phase_spectrum))

        magnitude_bytes = io.BytesIO()
        magnitude_image.save(magnitude_bytes, format='PNG')
        magnitude_bytes = magnitude_bytes.getvalue()

        phase_bytes = io.BytesIO()
        phase_image.save(phase_bytes, format='PNG')
        phase_bytes = phase_bytes.getvalue()

        result = mongo_collection.update_one(filter={
            '_id': ObjectId(id)
        }, update={
            '$set': {
                'magnitude': bson.Binary(magnitude_bytes),
                'phase': bson.Binary(phase_bytes)
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
            logging.error(f'Exception {e.with_traceback()}')


if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO)
    server = rabbitMQServer(QUEUE_NAME, RABBIT_HOSTNAME, MONGO_URI, EXCHANGE_NAME)
    server.get_messages()