# Urja Meter Ops Portal Protocol

## 1. Overview

The Urja Meter Ops portal is the upstream source for meter information, energy readings, and meter location data. This project accesses the portal through its observed HTTP endpoints and exposes a separate API for other programs.

Portal URL: [https://urja-ops.flockenergy.tech](https://urja-ops.flockenergy.tech)

## 2. Authentication

The observed portal requests use a session cookie named `__Secure-better-auth.session_token`.

The API service reads the complete cookie pair from the `FLOCK_PORTAL_SESSION_COOKIE` environment variable and sends it in the `Cookie` request header.

The session cookie is sensitive. It must not be committed to source control, included in documentation, or written to logs. Requests can fail with HTTP 401 when the session is missing, invalid, or expired. A user may need to sign in again and update the environment variable.

The service uses the normal access available to the logged-in portal user. It does not attempt to bypass portal authentication or permissions.

## 3. Observed Portal Endpoints

### 3.1 Search meters

**Request**

`GET /portal/meters/search?q={query}&page={page}`

**Observed response shape**

```json
{
  "data": [
    {
      "meterId": "J100000",
      "serialNo": "SE33962",
      "make": "HPL",
      "phaseType": "single",
      "installStatus": "Decommissioned",
      "dtCode": "DT-001"
    }
  ],
  "total": 403,
  "page": 1,
  "pageSize": 20
}
```

The response contains a `data` array and pagination metadata. The observed page size is 20, and the observed total was 403. These values describe the inspected dataset and may change.

The `q` query parameter is used for searching, and `page` selects a page. The API forwards these parameters to the portal.

### 3.2 Meter details

**Request**

`GET /meters/{meterId}/__data.json`

The details page returns a SvelteKit indexed-data representation rather than a conventional flat JSON object. The response contains a `nodes` array, and fields refer to values or objects by array index.

The service decodes this representation into two objects:

- `details`: meter ID, serial number, make, phase type, installation status, and installation type.
- `hierarchy`: zone, circle, division, subdivision, substation, feeder, and distribution transformer (DT).

This decoder depends on the indexed response structure observed during investigation. Changes to the portal's serialization format may require changes to the decoder.

### 3.3 Energy readings

**Request**

`GET /portal/meters/{meterId}/energy`

**Observed response shape**

```json
{
  "data": [
    {
      "timestamp": "23/06/2026 23:30",
      "kwh": "6850.32",
      "kvah": "7398.35",
      "voltR": "227"
    }
  ]
}
```

The response contains a `data` array of energy readings. The observed readings were at 30-minute intervals.

The timestamp format observed was `dd/MM/yyyy HH:mm`. Numeric-looking values such as `kwh`, `kvah`, and `voltR` were returned as strings.

### 3.4 Meter geographic location

**Request**

`GET /portal/meters/{meterId}/geo`

**Observed response shape**

```json
{
  "data": {
    "latitude": "26.871615946670158",
    "longitude": "75.76268730618544"
  }
}
```

The geographic coordinates were returned as strings inside the `data` object.

## 4. Authentication and Error Observations

- The portal uses a session cookie for the observed requests.
- A missing or invalid session can produce HTTP 401 Unauthorized.
- Requests require ordinary authenticated access to the portal.
- Network failures and upstream HTTP errors must be handled by the API service.

## 5. Observed Behaviour and Limitations

- Search results use pagination.
- Energy readings contain timestamped measurements.
- Location coordinates and energy measurements are represented as strings in the observed responses.
- The meter details endpoint uses indexed page data and requires decoding.
- The response examples in this document are representative observations, not guarantees that all meters have identical values or available fields.
- The investigation covered the endpoints needed for the current API implementation; it does not claim to document every endpoint in the portal.
