import pytest
import requests
from kafka_connect import KafkaConnect

CONNECT_URL="http://localhost:8083"

@pytest.fixture
def connect_client():
    client = KafkaConnect(url=CONNECT_URL)
    cluster = client.get_cluster_info()
    print(cluster)
    
    return client


@pytest.fixture
def clear_connectors(connect_client: KafkaConnect):
    try:
        connect_client.delete_all_connectors()
        yield
    finally:
        connect_client.delete_all_connectors()