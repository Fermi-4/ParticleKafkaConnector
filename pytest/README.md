
# Pytest test suite

This set of test are some samples I was using 
with my own Electron devices + Particle product 
configurations etc and running Kafka Connect/Cluster
locally

Electrons are basically useless in the US now
as 2G/3G network access has been sunset here..

So I am checking these in only as a reference for now..
It should take minimal changes if you wanted to adjust 
the Connector configuration payloads for your own setup


# Usage

Create a local env, install depedencies, execute test suite:

```bash
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
pytest -s
```
