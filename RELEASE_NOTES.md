### Version 0.9.5 (TBA)

### Version 0.9.4 (08/20/2018)
* BUG: `DockerApi.promote` needs to consume text lest the invocation fail.

### Version 0.9.3 (08/20/2018)
* REFACTOR: `DockerApi` endpoint `promote` changed default version to `v2`.
* ADDED: Bump `jclouds` to `2.1.1`.
* ADDED: Bump `AutoValue` to `1.6.2`
* ADDED: Bump `gradle-bintray-plugin` to `1.8.4`
* ADDED: Bump `gradle` to `4.9`

### Version 0.9.2 (08/09/2018)
* REFACTOR: ArtifactoryClient will now implement Closeable in favor of AutoCloseable.

### Version 0.9.1 (08/08/2018)
* ADDED: Bump gradle to `4.5` - [Commit 95a05](https://github.com/cdancy/artifactory-rest/commit/95a0535883dcefb5a6cc3a547b38b97c9783c658)
* REFACTOR: Change `AutoValue` dependency to `compileOnly` as it's not required at runtime - [Commit 49493](https://github.com/cdancy/artifactory-rest/commit/49493e41ea913e7a79de26fb9f9bd7b441fd1df3)
* REFACTOR: all `mock` and `integ` tests now use `assertj` assertions over `testng` assertions - [Commit 25b1b](https://github.com/cdancy/artifactory-rest/commit/25b1b4f29363863175ca667b7602946f8f467fd2)
* ADDED: ArtifactoryClient is now AutoCloseable.

### Version 0.9.0 (01/21/2018)
* REFACTOR: complete refactor of ArtifactoryClient and how the builder works.
* ADDED: exposed `jclouds` overrides to the ArtifactoryClient via the builder, setting system properties or environment variables.
* REFACTOR: complete re-write of how authentication works.
* ADDED: lots of documentation cleanups, fixes, and additions to make things easier to understand.
* BREAKING: couple of constructors were removed in an effort to simplify things but all apis and endpoints remain the same. This is the last release in preparation for the initial `1.0.0` rollout.

### Version 0.0.11 (01/16/2018)
* REFACTOR: `BuildApi.promote` now takes a `long` instead of an `int` for the buildNumber - [PR 1](https://github.com/cdancy/artifactory-rest/pull/1)
* REFACTOR: Refactor client builder implementation - [Commit 6c7cd5](https://github.com/cdancy/artifactory-rest/commit/6c7cd51bd99fc8e0cd4e452bc9f0b1afb1fe97a3)

### Version 0.0.10 (12/20/2017)
* REFACTOR: bump jclouds to `2.0.3`
* BUG: fix broken `all` jar packaging

### Version 0.0.9 (12/15/2017) 
* ADDED: bump gradle to `4.4`
* ADDED: bump jclouds to `2.0.2`
* ADDED: can now build on `travis-ci`
* REFACTOR: requires jdk1.8 to build
* REFACTOR: removed use of okhttp
* REFACTOR: code cleanup and general refactorings all around

### Version 0.0.8 (1/3/2017) 
* ADDED: do not relocate 'org' package for `all` jar

### Version 0.0.5 (8/22/2016)
* ADDED: `StorageApi' gained endpoint 'listFiles'

### Version 0.0.4 (6/28/2016)
* ADDED: `SystemApi` gained endpoint `system`
* ADDED: `SearchApi` gained endpoint `latestVersionWithLayout`

### Version 0.0.3 (April 11, 2016)
* ADDED: `com.cdancy.artifactory.rest.domain.search.Result` will now return properties should they be requested.

### Version 0.0.2 (April 6, 2016)
* ADDED: BuildApi along with 'promote' endpoint.
* ADDED: ArtifactApi.copyArtifact endpoint.

### Version 0.0.1 (March 20, 2016)
* init for project
