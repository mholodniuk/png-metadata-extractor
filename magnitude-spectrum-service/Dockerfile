FROM python:3.9

ENV PATH="/root/miniconda3/bin:$PATH"
ARG PATH="/root/miniconda3/bin:$PATH"

RUN apt-get update && \
    apt-get install -y build-essential  && \
    apt-get install -y wget && \
    apt-get install -y python3-opencv && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN pip install opencv-python

WORKDIR /usr/app

COPY requirements.txt .
RUN pip install --upgrade pip && pip install -r requirements.txt

COPY receiver.py .

CMD ["python", "receiver.py"]