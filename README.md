Welcome to your new Kafka Connect connector!

# Start Development Kafka cluster

This spins up 3 Zookeeper nodes and 3 Kafka brokers

```
cd docker; docker-compose up
```

## Create topic
```
bin/kafka-topics.sh --bootstrap-server localhost:9092 --topic test --create
```

# Run connect standalone

## Update plugin path
...

## Start connector instance
```
curl -X POST -H "Content-Type: application/json" -d '{"name": "particle-sse-connector", "config": {"connector.class": "com.fermi4.particle.ParticleEventSourceConnector", "particle.event.mode": "all", "particle.event.prefix": "temp", "particle.event.access.token": "<token>", "particle.event.topic": "test"}}' http://localhost:8083/connectors

```

# Publish events using Particle CLI
```
i=0; while true; do particle publish temperature "$i" && ((i++)) && sleep 5; done
```

# Running in development

```
mvn clean package
export CLASSPATH="$(find target/ -type f -name '*.jar'| grep '\-package' | tr '\n' ':')"
$CONFLUENT_HOME/bin/connect-standalone $CONFLUENT_HOME/etc/schema-registry/connect-avro-standalone.properties config/MySourceConnector.properties
```
