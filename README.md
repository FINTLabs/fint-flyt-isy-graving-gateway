# fint-flyt-isy-graving-gateway

Gateway service for ISY Graving instances in the Flyt ecosystem. It accepts case and journal post payloads, maps them into Flyt web instance objects (including file uploads), and handles callback dispatching when instances are processed.

## What this service does
- **Receive ISY Graving instances** over HTTP (case and journal post payloads).
- **Map incoming payloads** to Flyt web instance objects, persisting documents via the file service.
- **Track dispatch context** so the originating system can be called back when processing completes.
- **Dispatch callbacks** on `instance-dispatched` Kafka events with retry support.
- **Expose a status endpoint** for looking up archive case IDs.

## API
Base path is `EXTERNAL_API/isygraving/instances` (see `no.novari.flyt.webresourceserver.UrlPaths.EXTERNAL_API`).

### GET `/{sourceApplicationInstanceId}/status`
Returns archive case status for a previously submitted case.

Response:
```json
{
  "archiveCaseId": "string"
}
```

### POST `/case`
Submit a case instance.

Body (all fields required):
```json
{
  "caseId": "string",
  "caseArchiveGuid": "string",
  "tenant": "string",
  "municipalityName": "string",
  "caseType": "string",
  "locationReference": "string",
  "caseDate": "string",
  "caseResponsible": "string",
  "status": "string",
  "callback": "string"
}
```

### POST `/journalpost`
Submit a journal post instance. A `mainDocument` must be present or the request is rejected with 400.

Body (all fields required):
```json
{
  "archiveCaseId": "string",
  "journalEntries": [
    {
      "municipalityName": "string",
      "caseType": "string",
      "locationReference": "string",
      "date": "string",
      "documentType": "string",
      "caseHandler": "string",
      "recipients": [
        {
          "name": "string",
          "address": "string",
          "postalCode": "string",
          "organizationNumber": "string"
        }
      ],
      "documents": [
        {
          "title": "string",
          "fileName": "string",
          "mainDocument": true,
          "lastModified": "string",
          "status": "string",
          "mediaType": "string",
          "documentBase64": "string"
        }
      ]
    }
  ],
  "tenant": "string",
  "caseId": "string",
  "caseArchiveGuid": "string",
  "municipalityName": "string",
  "caseType": "string",
  "locationReference": "string",
  "caseDate": "string",
  "caseResponsible": "string",
  "status": "string",
  "callback": "string"
}
```

Notes:
- Only the first `journalEntries` element is mapped into the outgoing instance object.
- The `mainDocument` is uploaded first; remaining documents are treated as attachments.

## Dispatch flow
1. Incoming instances are processed via the Flyt web instance gateway.
2. Dispatch context is stored in Postgres (`dispatch_context`).
3. When `instance-dispatched` Kafka events arrive, the service:
   - builds a payload containing `tenant`, `caseId`, `caseArchiveGuid`, and `archiveCaseId`,
   - sends it to the provided `callback` URL via HTTP PUT,
   - removes the receipt on success and retries failed receipts on a schedule.

## Data model
Flyway migrations live in `src/main/resources/db/migration`.

Tables created in `V1__init.sql`:
- `dispatch_context`: temporary storage for callback details.
- `dispatch_receipt`: retry queue for failed callbacks.

## Configuration
Key configuration is in `src/main/resources`:
- `application.yaml`: base settings and profile includes.
- `application-flyt-web-resource-server.yaml`: external API security, authorized source application IDs.
- `application-flyt-dispatch-web-client.yaml`: dispatch API key header/value.
- `application-flyt-file-web-client.yaml`: OAuth client for file service.
- `application-flyt-postgres.yaml`: datasource and Flyway.
- `application-local-staging.yaml`: local developer defaults (ports, Kafka, Postgres).

Common environment variables:
- `fint.database.url`, `fint.database.username`, `fint.database.password`
- `fint.sso.client-id`, `fint.sso.client-secret`
- `NOVARI_FLYT_ISY_GRAVING_DISPATCH_API_KEY`
- `NOVARI_FLYT_ISY_GRAVING_DISPATCH_API_KEY_HEADER` (defaults to `X-API-KEY`)
- `novari.flyt.isy-graving.dispatch.enabled` (defaults to `true`, disabled in `local-staging`)

## Local development
1. Start Postgres:
   ```bash
   ./start-postgres
   ```
2. Run the app with the local profile:
   ```bash
   ./gradlew bootRun --args='--spring.profiles.active=local-staging'
   ```

## Build and test
```bash
./gradlew build
```

## Deployment
Kustomize overlays live in `kustomize/`. The `scripts/generate-overlay.sh` script renders overlay `kustomization.yaml` files from templates for supported environments.
