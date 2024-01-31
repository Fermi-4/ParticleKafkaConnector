<p align="center">
  <a href="https://docs.particle.io/" target="_blank">
    <img src="https://aws-logs-744494115018-us-west-1.s3.us-west-1.amazonaws.com/particle.png" alt="ParticleKafkaConnector">
  </a>
</p>

[![GitHub Actions Status](https://github.com/Fermi-4/ParticleKafkaConnector/actions/workflows/maven.yml/badge.svg)](https://github.com/Fermi-4/ParticleKafkaConnector/actions)

# Particle.io Kafka Connector
---
You wanna know how hot your toaster is every second-per-second in REAL TIME?
Is it your data, and you need it now???
Well, buckle up mfer.. high availability high durability coming at you like the hulk BITCH
Get your data AS IT HAPPENS! Down to the femtosecond! No really.. FUCK YOU

- Toaster got bluetooth? BAM
- GPS strapped to the cat? BAM




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
curl -X POST -H "Content-Type: application/json" -d '{"name": "particle-sse-connector", "config": {"connector.class": "com.github.fermi4.particle.ParticleEventSourceConnector", "particle.event.mode": "all", "particle.event.prefix": "temp", "particle.event.access.token": "<token>", "particle.event.topic": "test"}}' http://localhost:8083/connectors

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
