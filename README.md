
[![Build Status](https://travis-ci.org/cdancy/artifactory-rest.svg?branch=master)](https://travis-ci.org/cdancy/artifactory-rest)
[![codecov](https://codecov.io/gh/cdancy/artifactory-rest/branch/master/graph/badge.svg)](https://codecov.io/gh/cdancy/artifactory-rest)
[![Download](https://api.bintray.com/packages/cdancy/java-libraries/artifactory-rest/images/download.svg) ](https://bintray.com/cdancy/java-libraries/artifactory-rest/_latestVersion)
[![Stack Overflow](https://img.shields.io/badge/stack%20overflow-artifactory&#8211;rest-4183C4.svg)](https://stackoverflow.com/questions/tagged/artifactory+rest)

# artifactory-rest

java-based client to interact with Artifactory's REST API.

## Setup

Client's can be built like so:

      ArtifactoryClient client = new ArtifactoryClient.Builder()
      .endPoint("http://127.0.0.1:8081/artifactory")
      .credentials("admin:password")
      .build();

      Version version = client.api().systemApi().version();
      
## Latest release

Can be sourced from jcenter like so:

	<dependency>
	  <groupId>com.cdancy</groupId>
	  <artifactId>artifactory-rest</artifactId>
	  <version>X.Y.Z</version>
	  <classifier>sources|docs|tests|all</classifier> (Optional)
	</dependency>

## Documentation

javadocs can be found via [github pages here](http://cdancy.github.io/artifactory-rest/docs/javadoc/)

## Property based setup

Client's do NOT need supply the endPoint or credentials as part of instantiating
the ArtifactoryClient object. Instead one can supply them through system properties,
environment variables, or a combination of the 2. System properties will be searched
first and if not found we will attempt to query the environment.

Setting the `endpoint` can be done with any of the following (searched in order):

- `artifactory.rest.endpoint`
- `artifactoryRestEndpoint`
- `ARTIFACTORY_REST_ENDPOINT`

Setting the `credentials` can be done with any of the following (searched in order):

- `artifactory.rest.credentials`
- `artifactoryRestCredentials`
- `ARTIFACTORY_REST_CREDENTIALS`

## Credentials

artifactory-rest credentials can take 1 of 3 forms:

- Colon delimited username and password: __admin:password__ 
- Base64 encoded username and password: __YWRtaW46cGFzc3dvcmQ=__ 
- Generated API key: __AKCp2TfiyqrqHmfzUzeQhJmQrDyEx1o2S25pcC2hLzCTu65rpVhEoL1G6ppHn4exmHYfCiyT4__ 

## Examples

The [mock](https://github.com/cdancy/artifactory-rest/tree/master/src/test/java/com/cdancy/artifactory/rest/features) and [live](https://github.com/cdancy/artifactory-rest/tree/master/src/test/java/com/cdancy/artifactory/rest/features) tests provide many examples
that you can use in your own code.

## Components

- jclouds \- used as the backend for communicating with Artifactory's REST API
    
## Testing

Running mock tests can be done like so:

	./gradlew clean build mockTest
	
Running integration tests can be done like so (also runs mock tests):

	./gradlew clean build integTest -PtestArtifactoryEndpoint=http://127.0.0.1:8081/artifactory -PtestArtifactoryCredential=admin:password
	
# Additional Resources

* [Artifactory REST API](https://www.jfrog.com/confluence/display/RTF/Artifactory+REST+API)

