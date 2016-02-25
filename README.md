
#artifactory-rest 

artifactory-rest is a java-based client used to interact with Artifactory's REST API.

## Setup

Client's can be built like so:

      ArtifactoryClient client = new ArtifactoryClient.Builder()
      .endPoint("http://127.0.0.1:8081/artifactory")
      .credentials("admin:password")
      .build();

      Version version = client.api().systemApi().version();

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

Running mock tests can be done like so:

	gradle clean build mockTest 
	
Running integration tests can be done like so (also runs mock tests):

	gradle clean build integTest -PtestArtifactoryEndpoint=http://127.0.0.1:8081/artifactory -PtestArtifactoryCredential=admin:password
