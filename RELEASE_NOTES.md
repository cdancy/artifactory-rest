### Version 0.0.11 (TBA)
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