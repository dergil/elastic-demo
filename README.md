## Exemplary integration of the Elastic stack into a microservice architecture

### Build productive services
In root directory
```bash
./mvnw clean install -DskipTests
```
Used java JDK version: corretto-11.0.14.1


### Kibana import:
Start the Kibana container and import the configurations and visualisations via curl:
```bash
curl -X POST \
  http://<kibana-host>:5601/api/saved_objects/_import \
  -H 'kbn-xsrf: true' \
  --form file=@<path-to-file>.ndjson
```
The dashboard 'Overview' with the index patterns and visualizations should appear under Analytics -> Dashboard
