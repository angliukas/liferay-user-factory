# Liferay User Factory

Simple Spring Boot 3 service that lets you upload an Excel workbook containing `email`, `name`, and `surname` columns and creates users in a Liferay 6.2 instance via the JSON Web Services API.

## Specification

### Functional requirements

* Accept an `.xlsx` spreadsheet upload and create users in a remote Liferay 6.2 instance.
* Require the importing user to choose a target Liferay organization before processing uploads; reject requests without an `organizationId`.
* Provide a browser UI at `/` that lists organizations fetched from the backend, allows searching the list, and posts the selected organization ID alongside the uploaded file to the API.
* Display import feedback in the UI, including created users, existing users, validation errors, and failures returned from the backend.

### Input format

* Read only the first worksheet of the uploaded Excel file.
* Expect three columns in order: `email`, `name`, and `surname`; the first row may be a header and will be skipped when it looks like one.
* Ignore completely blank rows; trim whitespace on all captured values.

### Import flow

1. Parse the uploaded spreadsheet into user records.
2. Validate each record:
   * `organizationId` must be supplied.
   * `email` must be present and match a simple RFC 5322–style pattern.
   * All configured default roles must exist in Liferay; otherwise, the entire batch is marked as failed with a "Missing roles" reason.
3. For each valid row:
   * Check for an existing user in the local database by email (case-insensitive).
   * If not found locally, query the remote Liferay instance to see whether the user already exists.
   * If the user exists, add it to the `existingUsers` summary; otherwise, create the user via JSON Web Services and add it to `importedUsers`.
   * Capture any Liferay API errors as per-row failures without interrupting the remainder of the import.
4. Return an `ImportResult` payload summarizing totals, created count, validation errors, per-row failures, imported users, and existing users. The UI renders this payload in tables under the upload form.

### API surface

* `POST /api/users/import` — multipart endpoint accepting `file` (Excel file) and `organizationId` (long). Returns an `ImportResult` JSON document detailing import outcomes. Responds with HTTP 400 when the organization is missing.
* `GET /api/users/organizations` — returns the available organizations retrieved from the configured Liferay instance for populating the upload UI dropdown.

### Liferay interaction

* Authentication: HTTP Basic using `liferay.admin-username` and `liferay.admin-password` supplied in configuration.
* Creation: Calls `/api/jsonws/user/add-user` with values such as company ID, locale, birthday, job title, the selected organization ID, and default role IDs. Password and screen name settings honor configuration flags.
* Existence checks: Calls `/api/jsonws/user/get-user-by-email-address` with company ID and email; treats `404` and JSON `exception` fields as non-existence.
* Role validation: Calls `/api/jsonws/role/get-role` for each configured default role ID and marks any missing IDs as a blocking validation failure for the batch.

### Configuration

* Default server port: `8081`.
* Connection settings (base URL, admin credentials, company ID, default roles, and user profile defaults) live under the `liferay.*` namespace in `src/main/resources/application.properties`.
* For MSSQL persistence, set database parameters in `src/main/resources/application-mssql.properties` and run with the `mssql` Spring profile.

## Running locally

```bash
mvn -Dspring-boot.run.profiles=mssql spring-boot:run
```

Or with Maven installed:

```bash
mvn -Dspring-boot.run.profiles=mssql spring-boot:run
```

The app starts on port `8081` by default. Adjust `src/main/resources/application.properties` with your Liferay endpoint, credentials, and company id. Set `liferay.dry-run=false` to actually invoke the remote API instead of logging.

For MSSQL databases, configure the connection in `src/main/resources/application-mssql.properties` and run with the `mssql` profile enabled as shown above.

## Usage

1. Open [http://localhost:8081](http://localhost:8081) in your browser.
2. Select an `.xlsx` file with columns: email, name, surname (the first row may be a header).
3. Click **Upload** to import; the page will show successes and any failures returned by the service.
