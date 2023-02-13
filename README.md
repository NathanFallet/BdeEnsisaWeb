# Website of ENSISA's BDE

[![License](https://img.shields.io/github/license/NathanFallet/BdeEnsisaWeb)](LICENSE)
[![Issues](https://img.shields.io/github/issues/NathanFallet/BdeEnsisaWeb)]()
[![Pull Requests](https://img.shields.io/github/issues-pr/NathanFallet/BdeEnsisaWeb)]()
[![Code Size](https://img.shields.io/github/languages/code-size/NathanFallet/BdeEnsisaWeb)]()
[![CodeFactor](https://www.codefactor.io/repository/github/NathanFallet/BdeEnsisaWeb/badge)](https://www.codefactor.io/repository/github/NathanFallet/BdeEnsisaWeb)
[![Open Source Helpers](https://www.codetriage.com/nathanfallet/bdeensisaweb/badges/users.svg)](https://www.codetriage.com/nathanfallet/bdeensisaweb)

The new website, built with [Kotlin](https://kotlinlang.org) and [Ktor](https://ktor.io).

The mobile app source code is also available [here](https://github.com/NathanFallet/BdeEnsisaMobile).

## Start the server

```bash
mvn install exec:java
```

Then, go to [http://127.0.0.1:8068/](http://127.0.0.1:8068/).

## Environment variables

Here is the list of environment variables used by the server:

| Name           | Description             |
| -------------- | ----------------------- |
| DB_HOST        | MySQL database host     |
| DB_NAME        | MySQL database name     |
| DB_USER        | MySQL user name         |
| DB_PASSWORD    | MySQL user password     |
| EMAIL_PASSWORD | Password to send emails |
| JWT_SECRET     | Secret to sign JWTs     |

Also, for notifications, the google service account key must be in the `firebase-adminsdk.json` file.
