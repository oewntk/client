<p align="center">
<img width="256" height="256" src="images/oewntk.png" alt="OEWNTK">
</p>
<p align="center">
<img width="150" src="images/mavencentral.png" alt="MavenCentral">
</p>

# OEWN JSON client

This is a sketch of what a useful client to the JSON API will do.

This emits requests and reads JSON responses.

It either:
- deserializes synsets, senses and lexes from responses
- displays raw JSON responses

Project [client](https://github.com/oewntk/client)

Project [server](https://github.com/oewntk/server)

## Dataflow

![Dataflow](images/dataflow_server_client.png  "Dataflow")

## Maven Central

		<groupId>io.github.oewntk</groupId>
		<artifactId>client</artifactId>
		<version>3.0.1</version>

## Dependencies

![Dependencies](images/client.dot  "Dependencies")
