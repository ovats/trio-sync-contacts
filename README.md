# Trio take-home project

## Background

This document is about the home task defined in the following link:

https://www.notion.so/trio/Back-End-Project-78fa9bd235be48fd82887f73055ae133

Basically:

- Describes an app to sync contacts between two services: MockAPI and Mailchimp
- Every contact contains the following information: first name, last name and email address.
- The contacts in MockAPI must be synchronized with contacts in Mailchimp
- The application must be deployed in Heroku or Netlify
- The application will synchronize contacts by triggering a GET request to /contacts/sync.
- This endpoint should return the information of contacts synchronized.

The project (code) has been uploaded to Github [here](https://github.com/ovats/trio-sync-contacts).

The project has been deployed in Heroku in [https://trio-take-home-gm.herokuapp.com](https://trio-take-home-gm.herokuapp.com).

The document that describes the technical design can be located in Google Docs [here](https://docs.google.com/document/d/124_7YyWPJWbFZ2UGSVMWgdV169lnBB1PdUwTZfq-em0/edit?usp=sharing).

Video files:
- [How does the software work](https://drive.google.com/file/d/1KgvkpDifpBuge144GDf8h5SgMAR2HQA9/view?usp=sharing)
- [How did I build the project and why did I build it that way](https://drive.google.com/file/d/1QK_O3IDdEnMui3motgbnTVuJr1aWnQR6/view?usp=sharing)
- [Sync successfully](https://drive.google.com/file/d/1tizI0paRpvn7i2yZwqZnFTARVj5pUvbI/view?usp=sharing)    

## Implementation

Scala, JDK and libraries used in this project:

- Scala 2.13.7
- sbt 1.5.5
- JDK 11
- Akka 2.6.17
- Akka Http 10.2.7
- Circe 0.14.1
- PureConfig 0.16.0
- Logback 1.2.7
- Scala Logging 3.9.4

Plugins:

- sbt-scalafmt 2.4.3
- sbt-native-packager 1.7.6


## How to run the service locally

To run the service locally use sbt:

```
sbt run
```

If everything is fine you should see something similar to:

```
14:22:16.769 [main] INFO com.trio.api.MainApiApp$ - Starting MainApiApp ...
14:22:18.034 [main] DEBUG com.trio.common.config.ApiAppConfig$ - Successfully loaded configuration (ApiAppConfig), ApiAppConfig(ApiConfig(contacts,0.0.0.0,8080),MailChimpConfig(b6749f9334fb7684fd16c5818632d647-us20,us20,https://us20.api.mailchimp.com/3.0,9f2da7d6f2,1000),MockApiConfig(http://localhost:8081))
14:22:19.015 [MainApiApp-akka.actor.default-dispatcher-4] INFO com.trio.api.MainApiApp$ - Started at port 8080
```

To trigger the sync:

```
curl localhost:8080/contacts/sync
```

## How to run the service deployed in Heroku:

From the terminal run:

```
curl https://trio-take-home-gm.herokuapp.com/contacts/sync
```

## Configuration (application.conf)

To make changes on the configuration check the technical design in the section [Configuration](https://docs.google.com/document/d/124_7YyWPJWbFZ2UGSVMWgdV169lnBB1PdUwTZfq-em0/edit?usp=sharing).

## Run unit tests:

From the terminal just run:

```
sbt test
```