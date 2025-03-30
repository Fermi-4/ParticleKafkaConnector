import os
import time
import requests
from kafka_connect import KafkaConnect

CONNECT_URL="http://localhost:8083"
TOKEN=os.getenv("PARTICLE_TOKEN")

def test_create_discover_all_product_max_tasks_1(clear_connectors, connect_client: KafkaConnect):
    connector_name = "particle-event-connector-products-max-tasks-1"
    payload = {
        "name": f"{connector_name}",
        "config": {
                "particle.connector.discover": "discover_products",
                "particle.connector.topic": "test",
                "particle.connector.key_mode": "config_product_id",
                "particle.connector.target": "product",
                "particle.connector.token": f"{TOKEN}",
                "connector.class": "com.github.fermi4.particle.ParticleSourceConnector",
                "tasks.max": 1
            }
    }
    connect_client.create_connector(payload)
    time.sleep(1)
    data = connect_client.list_connector_tasks(connector=connector_name)
    assert(len(data) == 1)
    for task in data:
        assert(len(task["config"]["particle.connector.product_id"].split(",")) == 2)

def test_create_discover_all_product_max_tasks_2(clear_connectors, connect_client: KafkaConnect):
    connector_name = "particle-event-connector-products-max-tasks-2"
    payload = {
        "name": f"{connector_name}",
        "config": {
                "particle.connector.discover": "discover_products",
                "particle.connector.topic": "test",
                "particle.connector.key_mode": "config_product_id",
                "particle.connector.target": "product",
                "particle.connector.token": f"{TOKEN}",
                "connector.class": "com.github.fermi4.particle.ParticleSourceConnector",
                "tasks.max": 2
            }
    }
    connect_client.create_connector(payload)
    time.sleep(1)
    data = connect_client.list_connector_tasks(connector=connector_name)
    assert(len(data) == 2)
    for task in data:
        assert(len(task["config"]["particle.connector.device_id"].split(",")) == 1)

def test_create_discover_all_devices_in_product_group_connector(clear_connectors):
    connector_name = "particle-event-connector-product-devices-groups"
    payload = {
        "name": f"{connector_name}",
        "config": {
                "particle.connector.discover": "discover_devices_in_product_and_groups",
                "particle.connector.product_id": 27883,
                "particle.connector": "event",
                "particle.connector.topic": "test",
                "particle.connector.event_prefix": "",
                "particle.connector.key_mode": "config_product_id",
                "particle.connector.product_groups": "hwil",
                "particle.connector.target": "device",
                "particle.connector.token": f"{TOKEN}",
                "connector.class": "com.github.fermi4.particle.ParticleSourceConnector",
                "tasks.max": 2
            }
    }

    response = requests.post(f"{CONNECT_URL}/connectors", json=payload)
    response.raise_for_status()
    time.sleep(1)

    response = requests.get(f"{CONNECT_URL}/connectors/{connector_name}/tasks")
    response.raise_for_status()
    time.sleep(1)

    data = response.json()

    assert(len(data) == 2)

def test_create_discover_all_devices_in_product_connector(clear_connectors):
    connector_name = "particle-event-connector-product"
    payload = {
        "name": f"{connector_name}",
        "config": {
                "particle.connector.discover": "discover_devices_in_product",
                "particle.connector.product_id": 27883,
                "particle.connector.topic": "test",
                "particle.connector.key_mode": "config_product_id",
                "particle.connector.target": "device",
                "particle.connector.token": f"{TOKEN}",
                "connector.class": "com.github.fermi4.particle.ParticleSourceConnector",
                "tasks.max": 2
            }
    }

    response = requests.post(f"{CONNECT_URL}/connectors", json=payload)
    response.raise_for_status()
    time.sleep(1)

    response = requests.get(f"{CONNECT_URL}/connectors/{connector_name}/tasks")
    response.raise_for_status()
    time.sleep(1)

    data = response.json()

    assert(len(data) == 2)


def test_create_discover_all_devices_max_tasks_2(clear_connectors):
    """
    distribute the discovered devices across the tasks
    4 devices, 2 task == 2 devices per task
    """
    connector_name = "particle-event-connector-devices-max-tasks-2"
    payload = {
        "name": f"{connector_name}",
        "config": {
                "particle.connector.discover": "discover_devices",
                "particle.connector.topic": "test-device",
                "particle.connector.topic_mode": "event_type",
                "particle.connector.event_prefix": "",
                "particle.connector.key_mode": "coreid",
                "particle.connector.target": "device",
                "particle.connector.token": f"{TOKEN}",
                "connector.class": "com.github.fermi4.particle.ParticleSourceConnector",
                "tasks.max": 2
            }
    }

    response = requests.post(f"{CONNECT_URL}/connectors", json=payload)
    print(response)
    response.raise_for_status()
    time.sleep(1)

    response = requests.get(f"{CONNECT_URL}/connectors/{connector_name}/tasks")
    response.raise_for_status()
    time.sleep(1)

    data = response.json()
    assert(len(data) == 2)
    for task in data:
        assert(len(task["config"]["particle.connector.device_id"].split(",")) == 2)
    
    
    
def test_create_discover_all_devices_max_tasks_4(clear_connectors):
    """
    distribute the discovered devices across the tasks
    4 devices, 4 task == 1 device per task
    """
    connector_name = "particle-event-connector-devices-max-tasks-4"
    payload = {
        "name": f"{connector_name}",
        "config": {
                "particle.connector.discover": "discover_devices",
                "particle.connector.topic": "test",
                # "particle.connector.event_prefix": "",
                "particle.connector.key_mode": "config_device_id",
                "particle.connector.target": "device",
                "particle.connector.token": f"{TOKEN}",
                "connector.class": "com.github.fermi4.particle.ParticleSourceConnector",
                "tasks.max": 4
            }
    }

    response = requests.post(f"{CONNECT_URL}/connectors", json=payload)
    response.raise_for_status()
    time.sleep(1)

    response = requests.get(f"{CONNECT_URL}/connectors/{connector_name}/tasks")
    response.raise_for_status()
    time.sleep(1)

    data = response.json()
    assert(len(data) == 4)
    for task in data:
        assert(len(task["config"]["particle.connector.device_id"].split(",")) == 1)



