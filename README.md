
[![Build Status](https://travis-ci.org/cdancy/artifactory-rest.svg?branch=master)](https://travis-ci.org/cdancy/artifactory-rest)
[![codecov](https://codecov.io/gh/cdancy/artifactory-rest/branch/master/graph/badge.svg)](https://codecov.io/gh/cdancy/artifactory-rest)
[![Download](https://api.bintray.com/packages/cdancy/java-libraries/artifactory-rest/images/download.svg) ](https://bintray.com/cdancy/java-libraries/artifactory-rest/_latestVersion)
[![Stack Overflow](https://img.shields.io/badge/stack%20overflow-artifactory&#8211;rest-4183C4.svg)](https://stackoverflow.com/questions/tagged/artifactory+rest)

# artifactory-rest
![alt tag](http://maven.inria.fr/img/artifactory_logo_720.png)

java client, based on jclouds, to interact with Artifactory's REST API. 

## On jclouds, apis and endpoints
Being built on top of `jclouds` means things are broken up into [Apis](https://github.com/cdancy/artifactory-rest/tree/master/src/main/java/com/cdancy/artifactory/rest/features). 
`Apis` are just Interfaces that are analagous to a resource provided by the server-side program (e.g. /api/branches, /api/pullrequest, /api/commits, etc..). 
The methods within these Interfaces are analagous to an endpoint provided by these resources (e.g. GET /api/branches/my-branch, GET /api/pullrequest/123, DELETE /api/commits/456, etc..). 
The user only needs to be concerned with which `Api` they need and then calling its various methods. These methods, much like any java library, return domain objects 
(e.g. POJO's) modeled after the json returned by `artifactory`. 

Interacting with the remote service becomes transparent and allows developers to focus on getting
things done rather than the internals of the API itself, or how to build a client, or how to parse the json. 

## On new features

New Api's or endpoints are generally added as needed and/or requested. If there is something you want
to see just open an ISSUE and ask or send in a PullRequest. However, putting together a PullRequest
for a new feature is generally the faster route to go as it's much easier to review a PullRequest
than to create one ourselves. There is no problem doing so of course but if you need something done
now than a PullRequest is your best bet otherwise you may have to patiently wait for one of our
contributors to take up the work.

## Latest Release

Can be sourced from jcenter like so:

    <dependency>
      <groupId>com.cdancy</groupId>
      <artifactId>artifactory-rest</artifactId>
      <version>X.Y.Z</version>
      <classifier>sources|tests|javadoc|all</classifier> (Optional)
    </dependency>
	
## Documentation

javadocs can be found via [github pages here](http://cdancy.github.io/artifactory-rest/docs/javadoc/)

## Examples on how to build a _ArtifactoryClient_

When using `Basic` (e.g. username and password) authentication:

    ArtifactoryClient client = ArtifactoryClient.builder()
    .endPoint("http://127.0.0.1:7990") // Optional and can be sourced from system/env. Falls back to http://127.0.0.1:7990
    .credentials("admin:password") // Optional and can be sourced from system/env and can be Base64 encoded.
    .build();

    Version version = client.api().systemApi().version();

When using `Bearer` (e.g. jfrog token) authentication:

    ArtifactoryClient client = ArtifactoryClient.builder()
    .endPoint("http://127.0.0.1:7990") // Optional and can be sourced from system/env. Falls back to http://127.0.0.1:7990
    .token("123456789abcdef") // Optional and can be sourced from system/env.
    .build();

    Version version = client.api().systemApi().version();

When using `Anonymous` authentication or sourcing from system/environment (as described below):

    ArtifactoryClient client = ArtifactoryClient.builder()
    .endPoint("http://127.0.0.1:7990") // Optional and can be sourced from system/env. Falls back to http://127.0.0.1:7990
    .build();

    Version version = client.api().systemApi().version();

## On `System Property` and `Environment Variable` setup

Client's do NOT need to supply the endPoint or authentication as part of instantiating the
_ArtifactoryClient_ object. Instead one can supply them through `System Properties`, `Environment
Variables`, or a combination of the 2. `System Properties` will be searched first and if not
found we will attempt to query the `Environment Variables`. If neither turns up anything
than anonymous access is assumed.

Setting the `endpoint` can be done like so (searched in order):

    `System.setProperty("artifactory.rest.endpoint", "http://my-artifactory-instance:12345")`
    `export ARTIFACTORY_REST_ENDPOINT=http://my-artifactory-instance:12345`

Setting the `credentials`, which represents `Basic` authentication and is optionally Base64 encoded, can be done like so (searched in order):

    `System.setProperty("artifactory.rest.credentials", "username:password")`
    `export ARTIFACTORY_REST_CREDENTIALS=username:password`

Setting the `token`, which represents `Bearer` authentication, can be done like so (searched in order):

    `System.setProperty("artifactory.rest.token", "abcdefg1234567")`
    `export ARTIFACTORY_REST_TOKEN=abcdefg1234567`

## On Overrides

Because we are built on top of jclouds we can take advantage of overriding various internal _HTTP_ properties by
passing in a `Properties` object or, and in following with the spirit of this library, configuring them
through `System Properties` of `Environment Variables`. The properties a given client can configure can be
found [HERE](https://github.com/jclouds/jclouds/blob/master/core/src/main/java/org/jclouds/Constants.java).

When configuring through a `Properties` object you must pass in the keys exactly as they are named within jclouds:

    Properties props = new Properties();
    props.setProperty("jclouds.so-timeout", "60000");
    props.setProperty("jclouds.connection-timeout", "120000");

    ArtifactoryClient client = ArtifactoryClient.builder()
    .overrides(props)
    .build();

    Version version = client.api().systemApi().version();

When configuring through `System Properties` you must prepend the jclouds name with `artifactory.rest.`:

    System.setProperty("artifactory.rest.jclouds.so-timeout", "60000");
    System.setProperty("artifactory.rest.jclouds.connection-timeout", "120000");

    ArtifactoryClient client = ArtifactoryClient.builder()
    .build();

    Version version = client.api().systemApi().version();

When configuring through `Environment Variables` you must CAPITALIZE all characters,
replace any `.` with `_`, and prepend the jclouds name with `ARTIFACTORY_REST_`:

    export ARTIFACTORY_REST_JCLOUDS_SO-TIMEOUT=60000
    export ARTIFACTORY_REST_JCLOUDS_CONNECTION-TIMEOUT=120000

    ArtifactoryClient client = ArtifactoryClient.builder()
    .build();

    Version version = client.api().systemApi().version();

It should be noted that when using this feature a merge happens behind the scenes between all
possible ways one can pass in _overrides_. Meaning if you pass in a `Properties` object, and
there are `System Properties` and `Environment Variables` set, then all 3 will be merged into
a single `Properties` object which in turn will be passed along to _jclouds_. When it comes to 
precedence passed in `Properties` take precedence over `System Properties` which in turn 
take precedence over `Environment Variables`.

## Examples

The [mock](https://github.com/cdancy/artifactory-rest/tree/master/src/test/java/com/cdancy/artifactory/rest/features) and [live](https://github.com/cdancy/artifactory-rest/tree/master/src/test/java/com/cdancy/artifactory/rest/features) tests provide many examples
that you can use in your own code. If there are any questions feel free to open an issue and ask.

## Components

- jclouds \- used as the backend for communicating with Artifactory's REST API
- AutoValue \- used to create immutable value types both to and from the artifactory program
    
## Testing

Running mock tests can be done like so:

    ./gradlew mockTest
	
Running integration tests can be done like so (requires Artifactory instance):

    ./gradlew integTest

Various [properties](https://github.com/cdancy/artifactory-rest/tree/master/gradle.properties) exist for you to configure how the `integTest` task can be run should the defaults not suffice.
	
# Additional Resources

* [Artifactory REST API](https://www.jfrog.com/confluence/display/RTF/Artifactory+REST+API)
* [Apache jclouds](https://jclouds.apache.org/start/)
