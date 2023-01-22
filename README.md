# Website of ENSISA's BDE

The new website, built with Kotlin and Ktor.

## Start the server

```bash
mvn install exec:java
```

Then, go to `http://127.0.0.1:8080`.

## Environment variables

| Name          | Description         |
| ------------- | ------------------- |
| DB_HOST       | MySQL database host |
| DB_NAME       | MySQL database name |
| DB_USER       | MySQL user name     |
| DB_PASSWORD   | MySQL user password |
| JWT_SECRET    | Secret to sign JWTs |
