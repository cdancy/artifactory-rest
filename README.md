
#artifactory-rest 

artifactory-rest is a java-based client used to interact with Artifactory's REST API.

## Setup

Client's can be built like so:

	ArtifactoryApi artifactoryApi = ContextBuilder.newBuilder("artifactory")
	.endpoint("http://127.0.0.1:8081/artifactory")
	.credentials("N/A", "admin:password")
	.buildApi(ArtifactoryApi.class);
	
	boolean deleted = artifactoryApi
						.artifactApi()
						.deleteArtifact("libs-release-local", 
										"hello/world/1.0/hello-world-1.0.jar");

## Credentials

artifactory-rest credentials can take 1 of 3 forms:

- Colon delimited username and password: __admin:password__ 
- Base64 encoded username and password: __YWRtaW46cGFzc3dvcmQ=__ 
- Generated API key: __AKCp2TfiyqrqHmfzUzeQhJmQrDyEx1o2S25pcC2hLzCTu65rpVhEoL1G6ppHn4exmHYfCiyT4__ 

## Components

- jclouds \- used as the backend for communicating with Artifactory's REST API


## Troubleshooting

TODO: explain how to troubleshoot potential issues
    
## Testing

Runnign tests can be done like so:

	mvn clean install -Plive -Ptest.artifactory.endpoint=http://127.0.0.1:8081/artifactory
