#!/bin/bash
TARGET_COMPOSE_FILE=docker/kafka-stack-docker-compose/full-stack.yml
CONNECT_DIR=docker/kafka-stack-docker-compose/connectors
CONNECT_SERVER=http://localhost:8083
KAFKA_SERVER=kafka1
KAFKA_TOPIC=test
OUTPUT_FILE=output.txt
KAFKA_BOOTSTRAP=localhost:9092
USERNAME=""
PASSWORD=""
TOKEN=""
PUBLISH_EVENTS=100


# Process command-line options
while getopts ":u:p:t:j:a:" opt; do
  case $opt in
    a)
      PARTICLE_HWIL_DEVICE0="$OPTARG"
      ;;
    b)
      PARTICLE_HWIL_DEVICE1="$OPTARG"
      ;;
    c)
      PARTICLE_HWIL_DEVICE2="$OPTARG"
      ;;
    d)
      PARTICLE_HWIL_PRODUCT_ID="$OPTARG"
      ;;
    j)
      CONNECTOR_JSON_FILE="$OPTARG"
      ;;
    t)
      TOKEN="$OPTARG"
      ;;
    u)
      USERNAME="$OPTARG"
      ;;
    p)
      PASSWORD="$OPTARG"
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      exit 1
      ;;
    :)
      echo "Option -$OPTARG requires an argument." >&2
      exit 1
      ;;
  esac
done

# PARTICLE_HWIL_PRODUCT_ID
# PARTICLE_PASSWORD
# PARTICLE_TOKEN
# PARTICLE_USERNAME


ALL_EVENTS_MULTIPLE_DEVICES="{"name": "particle-device-connector-multi", "config": { "particle.connector": "event", "particle.connector.target": "device", "particle.connector.device_id": "$PARTICLE_HWIL_DEVICE0,$PARTICLE_HWIL_DEVICE1,$PARTICLE_HWIL_DEVICE2", "particle.connector.token": "$TOKEN", "particle.connector.key_mode": "device", "particle.connector.topic": "test-device-multi", "connector.class": "com.github.fermi4.particle.ParticleSourceConnector", "tasks.max": 5 }}"


ALL_EVENTS_ONE_DEVICE=$('{
    "name": "particle-device-connector-all",
    "config": {
      "particle.connector": "event",
      "particle.connector.target": "device",
      "particle.connector.device_id": "370038000150483553353520",
      "particle.connector.token": "$TOKEN",
      "particle.connector.key_mode": "device",
      "particle.connector.topic": "test-device",
      "connector.class": "com.github.fermi4.particle.ParticleSourceConnector",
      "tasks.max": 1
    }
}' | envsubst)

#
# captures all events matching (required) prefix temp
# TODO: move each of these to a single script and put infra bring up in single script also
export ALL_EVENTS_TOPIC='test-all'
export ALL_EVENTS_PREFIX='temp'
ALL_EVENTS=$('{
    "name": "particle-event-connector",
    "config": {
      "particle.connector": "event",
      "particle.connector.target": "all",
      "particle.connector.event_prefix": "$ALL_EVENTS_PREFIX",
      "particle.connector.topic": "$ALL_EVENTS_TOPIC",
      "particle.connector.token": "$TOKEN",
      "connector.class": "com.github.fermi4.particle.ParticleSourceConnector",
      "name": "particle-event-connector",
      "tasks.max": 1
    }
}' | envsubst)

echo $ALL_EVENTS
echo $ALL_EVENTS_ONE_DEVICE
echo $ALL_EVENTS_MULTIPLE_DEVICES

exit 1


sudo apt-get update

install_jq() {
    echo "[INFO] Installing jq..."
    sudo apt-get install -y jq
}

install_particle_cli() {
    echo "[INFO] Installing Particle CLI..."
    sudo npm install -g particle-cli
}

install_maven() {
    echo "[INFO] Installing Maven..."
    sudo apt-get update
    sudo apt-get install -y maven
}

# Check for jq and install if not exists
echo "[INFO] checking if jq is installed..."
if ! command -v jq &> /dev/null
then
    install_jq
else
    echo "[INFO] jq is already installed."
fi

# Check for Particle CLI and install if not exists
echo "[INFO] checking if particle cli is installed..."
if ! command -v particle &> /dev/null
then
    install_particle_cli
else
    echo "[INFO] Particle CLI is already installed."
    echo $(particle --version)
fi

# Attempt to login to Particle
echo "[INFO] particle login..."
if [ -z "$USERNAME" ] || [ -z "$PASSWORD" ]; then
    echo "[WARNING] USERNAME or PASSWORD is not set. Cannot login to Particle."
else
    particle login --username "$USERNAME" --password "$PASSWORD"
fi

# Check for Maven and install if not exists
echo "[INFO] checking if mvn is installed..."
if ! command -v mvn &> /dev/null
then
    install_maven
else
    echo "[INFO] Maven is already installed."
fi

# Build with Maven
echo "[INFO] Building connector..."
mvn clean test package

# stage connector
JAR_SUFFIX="-uber"
mkdir -p $CONNECT_DIR
cp target/*$JAR_SUFFIX.jar $CONNECT_DIR

# Bring up infrastructure
echo "[INFO] Bringing up infrastructure..."
docker-compose -f $TARGET_COMPOSE_FILE up -d


# Wait on connect to come up
echo "[INFO] Waiting on Connect server to come up..."
while true; do
        response_code=$(curl -s -X GET $CONNECT_SERVER/connector-plugins -o /dev/null -w "%{http_code}")
        if [ $response_code -eq 200 ]; then
          echo "[INFO] Curl succeeded with HTTP 200. Exiting the loop."
          break
        else
          echo "[INFO] Pinging $CONNECT_SERVER/connector-plugins. Got response: $response_code..."
        fi
        sleep 1
done

# create the source topic
echo "[INFO] Creating test topic $KAFKA_TOPIC..."
docker exec -i $KAFKA_SERVER kafka-topics --create --topic $KAFKA_TOPIC --bootstrap-server $KAFKA_BOOTSTRAP --partitions 3 --replication-factor 1

echo "[INFO] topics:"
docker exec -i $KAFKA_SERVER kafka-topics --list --bootstrap-server $KAFKA_BOOTSTRAP


# Start console consumer which writes to output file
echo "[INFO] Starting kafka consumer..."
nohup docker exec -i $KAFKA_SERVER kafka-console-consumer --bootstrap-server $KAFKA_BOOTSTRAP --topic $KAFKA_TOPIC --from-beginning > $OUTPUT_FILE &
sleep 3


# create connector instance

cat "$CONNECTOR_JSON_FILE" | jq -c '.[]' | while read -r CONNECTOR_ENTRY; do
    CONNECTOR_JSON=$(cat <<EOF
$CONNECTOR_ENTRY 
EOF
)
    echo "Creating connector instance..."
    $CONNECTOR_JSON | jq
    RESPONSE=$(curl -s -X POST -H "Content-Type: application/json" --data $CONNECTOR_JSON "$CONNECT_SERVER/connectors")
    echo "$RESPONSE" | jq
    CONNECTOR_NAME=$(echo "$RESPONSE" | jq -r '.name')
    echo "Connector created with name: $CONNECTOR_NAME"
    sleep 3
done

# Producer Particle Events
for ((i=1; i<=$PUBLISH_EVENTS; i++)); do particle publish temperature "$i" --private && sleep 1; done

# Get return code - if data was received
grep -a --text "data" "$OUTPUT_FILE"

# Capture the return code of the last command
ret=$?

# Check the value of ret
if [ "$ret" -eq 0 ]; then
  echo "[INFO] Data was received."
else
  echo "[INFO] No data found or an error occurred."
fi

# bring down infrastructure
# docker-compose -f $TARGET_COMPOSE_FILE down

exit $ret
