# Liferay User Factory

Simple Spring Boot 3 service that lets you upload an Excel workbook containing `email`, `name`, and `surname` columns and creates users in a Liferay 6.2 instance via the JSON Web Services API.

## Running locally

```bash
./mvnw spring-boot:run
```

Or with Maven installed:

```bash
mvn spring-boot:run
```

The app starts on port `8081` by default. Adjust `src/main/resources/application.properties` with your Liferay endpoint, credentials, and company id. Set `liferay.dry-run=false` to actually invoke the remote API instead of logging.

## Usage

1. Open [http://localhost:8081](http://localhost:8081) in your browser.
2. Select an `.xlsx` file with columns: email, name, surname (the first row may be a header).
3. Click **Upload** to import; the page will show successes and any failures returned by the service.
