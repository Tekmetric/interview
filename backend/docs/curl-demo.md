# Manual API Demo — curl Scripts

All scripts target `http://localhost:8080`. Start the app before running them:

```bash
mvn package -DskipTests && java -jar target/interview-1.0-SNAPSHOT.jar
```

Swagger UI is also available at `http://localhost:8080/swagger-ui/index.html`.

Scripts require `curl` and `jq`. Make them executable once:

```bash
chmod +x curl/customers/*.sh curl/credit_applications/*.sh
```

### Authentication

All `/api/v1/**` endpoints require HTTP Basic Auth. Credentials (configured in `application.properties`):

| Field    | Value        |
|----------|--------------|
| Username | `api-user`   |
| Password | `changeme`   |

The curl scripts include `-u "api-user:changeme"` automatically. To test auth failure, run any script with a wrong password or omit the `-u` flag — the server returns `401 Unauthorized`.

Swagger UI includes a pre-configured "Authorize" button (top-right) — enter the credentials once and all requests will include the header.

---

## Seeded Data (H2 — resets on every restart)

| Entity | ID | Detail |
|--------|----|--------|
| Customer — Jane Doe | `018fae10-0000-7000-8000-000000000001` | EMPLOYED, income $95k |
| Customer — John Smith | `018fae10-0000-7000-8000-000000000002` | SELF_EMPLOYED, income $120k |
| Customer — Maria Garcia | `018fae10-0000-7000-8000-000000000003` | EMPLOYED, income $72k |
| Application — Jane (SUBMITTED) | `018fae20-0000-7000-8000-000000000001` | $35k vehicle purchase — 2 documents (PROOF_OF_INCOME, GOVERNMENT_ID) |
| Application — John (UNDER_REVIEW) | `018fae20-0000-7000-8000-000000000002` | $55k refinance — 2 documents (PROOF_OF_INCOME, TAX_RETURN) |
| Application — Maria (APPROVED) | `018fae20-0000-7000-8000-000000000003` | $28k vehicle purchase — 1 document (PROOF_OF_INCOME) |

---

## Document URL Lifecycle

Supporting documents flow through two distinct URL types:

1. **Upload (PUT) URLs** — returned once in the `201` create response as `documentUploadUrls[]`. Each entry has a `documentType` and a presigned S3 `PUT` URL valid for **15 minutes**. The client uploads directly to S3 using this URL.

2. **Download (GET) URLs** — returned in `documentDownloadUrls[]` on all subsequent read responses (`GET`, `PATCH`). Fresh presigned S3 `GET` URLs are generated on every request and are valid for **60 minutes**. Never cache these — re-fetch the application to get a fresh URL.

Locally (`aws.enabled=false`) both URL types are no-op placeholder strings — they will not reach a real S3 backend.

---

## Customers

### `01_list_all.sh`
`GET /api/v1/customers`

Returns a paginated list of all customers sorted by creation date descending.

```
Expected: 200 — page containing all 3 seeded customers
```

---

### `02_get_by_id.sh`
`GET /api/v1/customers/:id`

Fetches Jane Doe by her seeded ID. Demonstrates SSN masking — the raw SSN `123-45-6789` is returned as `***-**-6789` in the response.

```
Expected: 200 — Jane Doe, ssn="***-**-6789"
```

---

### `03_create.sh`
`POST /api/v1/customers`

Creates a new customer (Carlos Rivera). The request body uses nested `address` and `employmentDetails` objects. The response is flat — address and employment fields are inlined at the top level.

```
Expected: 201 — Location header set to /api/v1/customers/{newId}, ssn masked in response body
```

---

### `04_update.sh`
`PUT /api/v1/customers/:id`

Partially updates John Smith's employment details. `UpdateCustomerRequest` accepts `firstName`, `lastName`, `email`, `phone`, `address`, and `employmentDetails` — fields not provided are left unchanged. `ssn` and `dateOfBirth` are immutable after creation and cannot be updated.

```
Expected: 200 — annualIncome updated to 135000.00
```

---

### `05_delete.sh`
`DELETE /api/v1/customers/:id`

Deletes John Smith. Before removing the customer row, the service fetches all of John's credit applications, collects their supporting documents, and calls `S3DocumentService.deleteDocuments()` to purge those S3 objects. The customer row (and its cascaded application/document rows) is then deleted from the DB.

```
Expected: 204 No Content
```

---

### `06_error_missing_first_name.sh`
`POST /api/v1/customers` — **400 validation**

Omits the required `firstName` field to demonstrate the Bean Validation error response shape.

```
Expected: 400 — ProblemDetail with field-level errors listing "firstName"
```

---

### `07_error_invalid_ssn.sh`
`POST /api/v1/customers` — **400 validation**

Sends `ssn: "NOT-AN-SSN"` to trigger the `@ValidSSN` custom constraint (requires `XXX-XX-XXXX` format).

```
Expected: 400 — ProblemDetail, @ValidSSN constraint violation on "ssn"
```

---

### `08_error_underage.sh`
`POST /api/v1/customers` — **400 validation**

Sends `dateOfBirth: "2015-01-01"` (under 18) to trigger the `@ValidAdultAge` custom constraint.

```
Expected: 400 — ProblemDetail, @ValidAdultAge constraint violation on "dateOfBirth"
```

---

### `09_error_duplicate_email.sh`
`POST /api/v1/customers` — **409 conflict**

Attempts to create a second customer with `jane.doe@example.com`, which is already in use by the seeded Jane Doe record.

```
Expected: 409 — ProblemDetail, unique constraint violation on email
```

---

### `10_error_not_found.sh`
`GET /api/v1/customers/:id` — **404**

Requests a customer by an all-zeros UUID that does not exist.

```
Expected: 404 — ProblemDetail, CustomerNotFoundException
```

---

## Credit Applications

### `01_list_all.sh`
`GET /api/v1/credit-applications`

Returns all applications paginated, sorted by `submittedAt` descending. Each application in the list includes `documentDownloadUrls[]` — fresh presigned GET URLs for every persisted supporting document.

```
Expected: 200 — page of 3 seeded applications (SUBMITTED, UNDER_REVIEW, APPROVED), each with documentDownloadUrls[]
```

---

### `02_list_filter_by_status.sh`
`GET /api/v1/credit-applications?status=SUBMITTED`

Filters applications by status using a JPA Specification — composable and avoids proliferating query methods.

```
Expected: 200 — 1 result (Jane Doe's SUBMITTED application) with documentDownloadUrls[]
```

---

### `03_get_by_id.sh`
`GET /api/v1/credit-applications/:id`

Fetches Jane Doe's application by its seeded ID. `customerId` and `customerName` are included for display convenience. The response includes `documentDownloadUrls[]` — one fresh presigned S3 GET URL per persisted supporting document, valid for 60 minutes.

```
Expected: 200 — status=SUBMITTED, customerName="Jane Doe", documentDownloadUrls[] with 2 entries (PROOF_OF_INCOME, GOVERNMENT_ID)
```

---

### `04_get_by_customer.sh`
`GET /api/v1/credit-applications/customers/:customerId`

Returns a paginated list of all applications belonging to a specific customer, sorted by `submittedAt` descending. Each application includes `documentDownloadUrls[]`.

```
Expected: 200 — page containing 1 application for Jane Doe with documentDownloadUrls[]
```

---

### `05_create.sh`
`POST /api/v1/credit-applications`

Submits a new application for Maria Garcia with two supporting documents (`PROOF_OF_INCOME` and `GOVERNMENT_ID`). The request body requires a non-empty `documents` array — each entry has a `documentType` (required) and an optional `fileName` hint.

The 201 response includes `documentUploadUrls[]` — one entry per document, each containing a `documentType` and a one-time presigned S3 `PUT` URL valid for 15 minutes. Locally (`aws.enabled=false`) these are no-op placeholder URLs. Use `05b_upload_document.sh` to exercise each upload.

> **Note:** `documentUploadUrls[]` is only present on this initial create response. All subsequent reads return `documentDownloadUrls[]` (presigned GET URLs) instead.

```
Expected: 201 — status=SUBMITTED, documentUploadUrls[] with 2 entries (one per document type)
```

---

### `05b_upload_document.sh`
`PUT <presignedUrl>`

Uploads a supporting document to a presigned URL from `documentUploadUrls[]` in the create response. Pass the URL via `UPLOAD_URL`. Run once per document.

By default (no `DOCUMENT_FILE` set) the script sends a small inline dummy payload so it is immediately runnable without preparing a file. Set `DOCUMENT_FILE` to upload a real file.

```bash
# Upload the first document (PROOF_OF_INCOME) — dummy payload
UPLOAD_URL=$(./curl/credit_applications/05_create.sh | jq -r '.documentUploadUrls[0].presignedUrl') \
  ./curl/credit_applications/05b_upload_document.sh

# Upload all documents in a loop — dummy payload
RESPONSE=$(./curl/credit_applications/05_create.sh)
echo "$RESPONSE" | jq -r '.documentUploadUrls[].presignedUrl' | while read -r url; do
  UPLOAD_URL="$url" ./curl/credit_applications/05b_upload_document.sh
done

# With a real file
UPLOAD_URL="<paste presignedUrl>" DOCUMENT_FILE="/path/to/paystub.pdf" \
  ./curl/credit_applications/05b_upload_document.sh
```

```
Expected (locally):  connection error or non-200 — no-op URL has no real S3 backend
Expected (real AWS): HTTP 200, empty response body
```

---

### `05c_confirm_documents.sh`
`POST /api/v1/credit-applications/:id/confirm-documents`

Verifies that all expected supporting documents have been uploaded to S3 by issuing a `HeadObject` call for each persisted document key. Call this after completing all presigned PUT uploads from `05b_upload_document.sh`.

Set `APP_ID` to target a specific application, or it defaults to Jane's seeded application.

```
Expected (all docs present):  200 — application response with documentDownloadUrls[] populated
Expected (doc missing):       422 — ProblemDetail with detail listing the missing document types
Expected (app not found):     404 — ProblemDetail, CreditApplicationNotFoundException
Expected (locally, new app):  200 — no-op registers keys at create time; confirm passes for the same session
Expected (locally, seeded):   422 — seeded documents were never registered via create; use the happy-path walkthrough to exercise the full upload → confirm flow
```

---

### `06_advance_to_under_review.sh`
`PATCH /api/v1/credit-applications/:id/status`

Moves Jane's application from `SUBMITTED` to `UNDER_REVIEW`. This is the only valid transition out of `SUBMITTED`. As a side effect, `SqsPublisher` fires — locally this is a no-op since `aws.enabled=false` by default. The response includes `documentDownloadUrls[]` with fresh presigned GET URLs.

```
Expected: 200 — status=UNDER_REVIEW, documentDownloadUrls[] populated
Side effect: SQS event published (no-op locally)
```

---

### `07_approve.sh`
`PATCH /api/v1/credit-applications/:id/status`

Moves John's application from `UNDER_REVIEW` to `APPROVED`. Demonstrates `decidedAt` being stamped on the response. `documentDownloadUrls[]` is included for the persisted documents.

```
Expected: 200 — status=APPROVED, decidedAt populated, documentDownloadUrls[] populated
```

---

### `08_delete.sh`
`DELETE /api/v1/credit-applications/:id`

Deletes Jane's application. S3 objects for all supporting documents are deleted first (via `S3DocumentService.deleteDocuments()`), then the application and its document rows are removed from the DB. Does not affect her customer record.

```
Expected: 204 No Content
```

---

### `09_error_missing_customer_id.sh`
`POST /api/v1/credit-applications` — **400 validation**

Omits the required `customerId` field. The `documents` array is present to isolate the `customerId` constraint.

```
Expected: 400 — ProblemDetail, validation error on "customerId"
```

---

### `10_error_loan_exceeds_limit.sh`
`POST /api/v1/credit-applications` — **400 validation**

Requests $500,000 for Jane Doe whose annual income is $95,000 (max loan = 5× income = $475,000). Triggers the `@ValidLoanAmount` cross-field constraint.

```
Expected: 400 — ProblemDetail, @ValidLoanAmount cross-field constraint violation
```

---

### `11_error_customer_not_found.sh`
`POST /api/v1/credit-applications` — **404**

Submits an application referencing a customer UUID that does not exist.

```
Expected: 404 — ProblemDetail, CustomerNotFoundException
```

---

### `12_error_invalid_state_transition.sh`
`PATCH /api/v1/credit-applications/:id/status` — **409 conflict**

Attempts to move Jane's `SUBMITTED` application directly to `APPROVED`, skipping `UNDER_REVIEW`. The state machine only permits `SUBMITTED → UNDER_REVIEW`.

```
Expected: 409 — ProblemDetail, InvalidApplicationStateException
Valid transitions: SUBMITTED → UNDER_REVIEW → APPROVED | DENIED
```

---

### `13_error_terminal_state.sh`
`PATCH /api/v1/credit-applications/:id/status` — **409 conflict**

Attempts to transition Maria's `APPROVED` application to `DENIED`. `APPROVED` and `DENIED` are terminal — no further transitions are permitted.

```
Expected: 409 — ProblemDetail, InvalidApplicationStateException
```

---

### `14_error_not_found.sh`
`GET /api/v1/credit-applications/:id` — **404**

Requests an application by an all-zeros UUID that does not exist.

```
Expected: 404 — ProblemDetail, CreditApplicationNotFoundException
```

---

## Full Happy-Path Walkthrough

Run these in order against a freshly started server to walk through the complete lifecycle:

```bash
# 1. Browse existing customers and applications (note documentDownloadUrls[] on each app)
./curl/customers/01_list_all.sh
./curl/credit_applications/01_list_all.sh

# 2. Create a new customer
./curl/customers/03_create.sh

# 3. Submit a credit application — captures presigned PUT URLs for direct S3 upload
RESPONSE=$(./curl/credit_applications/05_create.sh)
echo "$RESPONSE" | jq .

# 4. Upload each supporting document directly to S3 (dummy payload by default)
echo "$RESPONSE" | jq -r '.documentUploadUrls[].presignedUrl' | while read -r url; do
  UPLOAD_URL="$url" ./curl/credit_applications/05b_upload_document.sh
done

# 5. Confirm all documents are present in S3 — returns 200 with documentDownloadUrls[]
APP_ID=$(echo "$RESPONSE" | jq -r '.id') \
  ./curl/credit_applications/05c_confirm_documents.sh

# 6. Fetch the application — documentDownloadUrls[] now contains fresh presigned GET URLs
./curl/credit_applications/03_get_by_id.sh

# 7. Advance through the state machine (each response includes fresh documentDownloadUrls[])
./curl/credit_applications/06_advance_to_under_review.sh
./curl/credit_applications/07_approve.sh

# 8. Clean up
./curl/credit_applications/08_delete.sh
./curl/customers/05_delete.sh
```
