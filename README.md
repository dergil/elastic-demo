## Exemplary integration of the Elastic stack into a microservice architecture

### Build productive services
In root directory
```bash
./mvnw clean install -DskipTests
```
Used java JDK version: corretto-11.0.14.1

### Build and run Docker containers
```bash
docker-compose up
```
Wait for about a minute before sending requests, or until the string "green" appears in the Elastic logs

### Kibana import:
Start the Kibana container and import the configurations and visualisations via curl:

```bash
curl -X POST \
  http://localhost:5601/api/saved_objects/_import?overwrite=true \
   -H 'kbn-xsrf: true' \
   --form file=@<path-to-file>/export.ndjson
```
Visit Kibana at localhost:5601
The dashboard 'Overview' with the index patterns and visualizations should appear under Analytics -> Dashboard
