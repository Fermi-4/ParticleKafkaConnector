
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

# Process command-line options
while getopts ":u:p:t:" opt; do
  case $opt in
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

sudo apt-get install -y jq;

# install particle CLI
echo "[INFO] Installing particle CLI..."
sudo npm install -g particle-cli
echo $(particle --version)
particle login --username "$USERNAME"  --password "$PASSWORD"


# Build with Maven
echo "[INFO] Building connector..."
mvn clean test package

# stage connector
mkdir -p $CONNECT_DIR
cp target/*-jar-with-dependencies.jar $CONNECT_DIR

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
docker exec -i $KAFKA_SERVER kafka-topics --create --topic $KAFKA_TOPIC --bootstrap-server $KAFKA_BOOTSTRAP --partitions 1 --replication-factor 1

# Start console consumer which writes to output file
echo "[INFO] Starting kafka consumer..."
nohup docker exec -i $KAFKA_SERVER kafka-console-consumer --bootstrap-server $KAFKA_BOOTSTRAP --topic $KAFKA_TOPIC --from-beginning > $OUTPUT_FILE &
sleep 3


# create connector instance
echo "[INFO] Creating connector instance..."

CONNECTOR_JSON=$(cat <<EOF
{
  "name": "particle-event-connector",
  "config": {
    "connector.class": "com.github.fermi4.particle.ParticleSourceConnector",
    "particle.connector.mode": "sse",
    "particle.connector.topic": "$KAFKA_TOPIC",
    "particle.connector.token": "$TOKEN",
    "particle.event.mode": "all",
    "particle.event.prefix": "temp"
  }
}
EOF
)
curl -s -X POST -H "Content-Type: application/json" --data "$CONNECTOR_JSON" $CONNECT_SERVER/connectors | jq; sleep 3

# Producer Particle Events
for ((i=1; i<=10; i++)); do particle publish temperature "$i" --private && sleep 1; done

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
docker-compose -f $TARGET_COMPOSE_FILE down

exit $ret