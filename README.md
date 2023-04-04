## Exemplary integration of the Elastic stack into a microservice architecture

### Build productive services
In root directory
```bash
./mvnw clean install -DskipTests
```

### Build and run Docker containers
If possible: close currently running Docker containers, since their output would get ingested, too
```bash
docker stop $(docker ps -aq)
```
Start the system
```bash
(sudo) docker-compose up
```
Wait for about a minute before sending requests, or until the string "green" appears in the Elastic logs

### Run queries for demonstration and to create data
/car/src/test/java/.../demonstration_via_curl.txt

With Intellij IDEA the .http file in the same directory can be used

### Kibana import:
Import the Kibana configurations and visualisations via curl: /kibana/import/import_via_curl.txt

```bash
curl -X POST \
  http://localhost:5601/api/saved_objects/_import?overwrite=true \
   -H 'kbn-xsrf: true' \
   --form file=@./export.ndjson
```
Visit Kibana at localhost:5601

The dashboard 'Overview' with the index patterns and visualizations should appear under Analytics -> Dashboard

### Run platform tests
Stop all running containers
```bash
(sudo) docker-compose down
```
Run the test with mvnw from the root directory:
```bash
./mvnw -pl car/ clean test
```

### Troubleshooting
Tested with Java versions 11.0.13/14/18

and with docker-compose versions 1.21, 2.17.2

under with Arch Linux 6.2.8-arch1-1, Debian stable 4.19.0-18-amd64, 4.19.0-23-amd64
