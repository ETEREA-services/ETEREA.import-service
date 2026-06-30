# WordPress/WooCommerce Query API — External Consumer Reference

A read-only HTTP service that exposes WordPress/WooCommerce order, gift-card, product, and booking data through a thin Spring Boot layer. Responses mirror a legacy PHP plugin's frozen JSON contract byte-for-byte.

---

## Table of Contents

1. [Overview](#overview)
2. [Conventions](#conventions)
   - [Pagination](#pagination)
   - [Error Shape](#error-shape)
   - [Date Input Formats](#date-input-formats)
   - [Date Output Formats](#date-output-formats)
   - [Enum Binding](#enum-binding)
3. [Endpoints](#endpoints)
   - [GET /api/v1/orders](#get-apiv1orders)
   - [GET /api/v1/orders/export-feed](#get-apiv1ordersexport-feed)
   - [GET /api/v1/orders/abandoned](#get-apiv1ordersabandoned)
   - [GET /api/v1/gift-cards](#get-apiv1gift-cards)
   - [GET /api/v1/products](#get-apiv1products)
   - [GET /api/v1/booking-services](#get-apiv1booking-services)
   - [GET /health](#get-health)
4. [Error Examples](#error-examples)

---

## Overview

| Property | Value |
|---|---|
| Base URL | `http://<host>:8081` (port configurable via `SERVER_PORT`) |
| Base path | `/api/v1` |
| Health endpoint | `/health` (un-versioned) |
| Content-Type | `application/json` |
| Authentication | **None** — all endpoints are open/unauthenticated |
| CORS | Not configured — same-origin only |

> **Note:** This API is not yet production-hardened. Authentication and CORS headers are planned but not yet implemented. Do not expose this service directly to the public internet.

---

## Conventions

### Pagination

Applies to: `/api/v1/orders`, `/api/v1/orders/export-feed`, `/api/v1/orders/abandoned`, `/api/v1/gift-cards`.

**Request parameters:**

| Parameter | Type | Min | Max | Default | Description |
|---|---|---|---|---|---|
| `page` | integer | 0 | — | 0 | 0-indexed page number |
| `size` | integer | 1 | 50 | 20 | Items per page |

**Response envelope** (Spring Data `PagedModel`):

```json
{
  "content": [ ],
  "page": {
    "size": 20,
    "number": 0,
    "totalElements": 150,
    "totalPages": 8
  }
}
```

- `size` — items per page
- `number` — current page (0-indexed)
- `totalElements` — total matching rows across all pages
- `totalPages` — total number of pages

---

### Error Shape

All errors return an `ApiError` object:

```json
{ "code": "...", "message": "...", "source": "..." }
```

`source` is nullable — it contains the parameter name, subsystem name, or `null`.

| HTTP Status | `code` | When |
|---|---|---|
| 400 | `INVALID_PARAM` | Invalid enum value, type mismatch, or constraint violation (e.g. `size > 50`, `page < 0`, bad email) |
| 400 | `INVALID_DATE_PARAM` | Malformed or calendar-invalid date parameter |
| 503 | `YITH_GIFT_CARDS_INACTIVE` | Gift-cards endpoint called when the YITH Gift Cards plugin/post-type is absent |
| 500 | `INTERNAL_ERROR` | Unexpected server error |

---

### Date Input Formats

Date filter parameters (`date_from`, `date_to`) accept strict ISO formats only:

| Format | Example |
|---|---|
| `yyyy-MM-dd` | `2025-11-28` |
| `yyyy-MM-dd'T'HH:mm` | `2025-11-28T10:30` |
| `yyyy-MM-dd'T'HH:mm:ss` | `2025-11-28T10:30:00` |

The following are **rejected with `400 INVALID_DATE_PARAM`**:

- Non-zero-padded: `2025-1-5`
- Non-ISO format: `05/01/2025`
- Calendar-invalid: `2025-13-40`

---

### Date Output Formats

Date fields in responses are returned as strings, not ISO-8601 objects.

| Format | Example | Used for |
|---|---|---|
| `yyyy-MM-dd HH:mm:ss` | `2025-11-28 10:30:00` | Most date-time fields |
| `yyyy-MM-dd` | `2025-10-02` | Date-only fields (e.g. gift-card `delivery_date`) |

**Timezone rules:**

- Order and gift-card date metas render in the WordPress site timezone: **`America/Argentina/Mendoza`**.
- **Exception:** YITH booking dates (`booking_start`, `booking_end` inside order line items) render in **UTC**.
- Empty, zero, or invalid timestamps render as `""` (empty string).

---

### Enum Binding

Enum query parameters bind by their **wire value** (lowercase strings listed in each endpoint's table). An unrecognised value returns `400 INVALID_PARAM`. The parameter can be repeated to OR multiple values (e.g. `?status=completed&status=processing`).

---

## Endpoints

---

### GET /api/v1/orders

Returns a paginated list of WooCommerce orders with optional filters.

#### Query Parameters

| Parameter | Type | Required | Default | Notes |
|---|---|---|---|---|
| `date_from` | string | No | — | Inclusive lower bound; ISO date/datetime |
| `date_to` | string | No | — | Inclusive upper bound; ISO date/datetime |
| `status` | string (repeatable) | No | — | Wire values: `completed`, `pending`, `processing`, `on-hold`, `cancelled`, `refunded`, `failed`. Repeat to OR values. |
| `orderby` | string | No | `date` | Values: `date`, `id`, `modified` |
| `order` | string | No | `desc` | Values: `asc`, `desc` (case-insensitive) |
| `order_number` | integer | No | — | **Single-order fast path:** when provided, returns just that order and all other filters are ignored. Min: 1. |
| `page` | integer | No | `0` | Min: 0 |
| `size` | integer | No | `20` | Min: 1, max: 50 |

#### curl Example

```bash
curl "http://localhost:8081/api/v1/orders?status=completed&status=processing&date_from=2025-11-01&page=0&size=20"
```

#### Response

`Page<OrderData>` — standard pagination envelope with an array of `OrderData` objects in `content`.

```json
{
  "content": [
    {
      "order_number": "1234",
      "order_id": 1234,
      "order_status": "Completado",
      "order_date": "2025-11-28 10:30:00",
      "paid_date": "2025-11-28 10:35:00",
      "completed_date": "2025-11-30 14:00:00",
      "woe_order_exported": "1764511200",
      "modified_date": "2025-11-30 14:00:00",
      "order_currency": "ARS",
      "customer_note": "Llamar antes de entregar",
      "billing_first_name": "Juan",
      "billing_last_name": "Pérez",
      "billing_full_name": "Juan Pérez",
      "_billing_dni_o_pasaporte": "12345678",
      "billing_address": "Av. San Martín 123",
      "billing_city": "Mendoza",
      "billing_state": "Mendoza",
      "billing_postcode": "5500",
      "billing_country": "AR",
      "billing_email": "juan@example.com",
      "billing_phone": "+54 261 1234567",
      "shipping_first_name": "Juan",
      "shipping_last_name": "Pérez",
      "shipping_full_name": "Juan Pérez",
      "shipping_address": "Av. San Martín 123",
      "shipping_city": "Mendoza",
      "shipping_state": "Mendoza",
      "shipping_postcode": "5500",
      "shipping_country_full": "Argentina",
      "payment_method_title": "Transferencia bancaria",
      "cart_discount": 0.0,
      "order_subtotal": 25000.0,
      "order_subtotal_refunded": 0.0,
      "order_shipping": 0.0,
      "order_shipping_refunded": 0.0,
      "shipping_method_title": "",
      "order_total": 25000.0,
      "order_total_tax": 0.0,
      "products": [
        {
          "line_id": 1,
          "sku": "SPA-DAY",
          "name": "Día de Spa",
          "qty": 2,
          "item_price": 12500.0,
          "booking_id": 567,
          "booking_start": "2025-12-01 13:00:00",
          "booking_end": "2025-12-01 16:00:00",
          "booking_duration": 3,
          "booking_persons": 2,
          "person_types": "(2) Adulto",
          "servicios-adicionales": "[55-Traslado Spa]",
          "punto-de-encuentro": "Recepción del hotel",
          "encuentro-hotel": "yes",
          "punto-de-encuentro-transporte": "Hotel Ariosto"
        }
      ],
      "order_notes": ""
    }
  ],
  "page": {
    "size": 20,
    "number": 0,
    "totalElements": 45,
    "totalPages": 3
  }
}
```

#### OrderData Fields

| JSON Key | Type | Nullable | Description |
|---|---|---|---|
| `order_number` | string | No | Order number |
| `order_id` | integer | No | WooCommerce order/post ID |
| `order_status` | string | No | Translated status label (e.g. `"Completado"`) |
| `order_date` | string | No | Order creation date (site tz) |
| `paid_date` | string | No | Paid date (site tz); `""` if unpaid |
| `completed_date` | string | No | Completion date (site tz); `""` if not completed |
| `woe_order_exported` | string | No | Export timestamp/marker; `""` if never exported |
| `modified_date` | string | No | Last modified date (site tz) |
| `order_currency` | string | No | ISO currency code (e.g. `ARS`) |
| `customer_note` | string | No | Customer note |
| `billing_first_name` | string | No | |
| `billing_last_name` | string | No | |
| `billing_full_name` | string | No | |
| `_billing_dni_o_pasaporte` | string | No | DNI/passport custom field (note leading underscore in key) |
| `billing_address` | string | No | |
| `billing_city` | string | No | |
| `billing_state` | string | No | |
| `billing_postcode` | string | No | |
| `billing_country` | string | No | 2-letter country code |
| `billing_email` | string | No | |
| `billing_phone` | string | No | |
| `shipping_first_name` | string | No | |
| `shipping_last_name` | string | No | |
| `shipping_full_name` | string | No | |
| `shipping_address` | string | No | |
| `shipping_city` | string | No | |
| `shipping_state` | string | No | |
| `shipping_postcode` | string | No | |
| `shipping_country_full` | string | No | Full country name |
| `payment_method_title` | string | No | |
| `cart_discount` | number | No | |
| `order_subtotal` | number | No | |
| `order_subtotal_refunded` | number | No | |
| `order_shipping` | number | No | |
| `order_shipping_refunded` | number | No | |
| `shipping_method_title` | string | No | |
| `order_total` | number | No | |
| `order_total_tax` | number | No | |
| `products` | array of OrderItem | No | Line items (see OrderItem table below) |
| `order_notes` | string | No | |

#### OrderItem Fields

Booking fields are flattened directly into the line-item object. For non-bookable products, `booking_*` fields are `null` and the two static meeting-point fields are empty strings.

| JSON Key | Type | Nullable | Description |
|---|---|---|---|
| `line_id` | integer | No | |
| `sku` | string | No | |
| `name` | string | No | |
| `qty` | integer | No | |
| `item_price` | number | No | |
| `booking_id` | integer | Yes | `null` for non-bookable products |
| `booking_start` | string | Yes | **UTC**; `null` for non-bookable |
| `booking_end` | string | Yes | **UTC**; `null` for non-bookable |
| `booking_duration` | integer | Yes | `null` for non-bookable |
| `booking_persons` | integer | Yes | `null` for non-bookable |
| `person_types` | string | Yes | `null` for non-bookable |
| `servicios-adicionales` | string | Yes | Hyphenated key; `null` for non-bookable |
| `punto-de-encuentro` | string | No | Hyphenated key; static product-level meeting point; `""` for non-bookable |
| `encuentro-hotel` | string | No | Hyphenated key; static product-level flag; `""` for non-bookable |
| `punto-de-encuentro-transporte` | string | Yes | Hyphenated key; the transport pickup point the customer selected at checkout (TM Extra Product Options `_tmdata`); `null` when the product has no transport option. The value is read from the `tmcp_post_fields` → `tmcp_select_*` selections (all concatenated), with TM EPO's trailing `_<index>` suffix stripped (e.g. stored `"Hotel Ariosto_8"` → `"Hotel Ariosto"`); the explicit no-transport choice `"Sin Traslado"` is kept as-is. |

---

### GET /api/v1/orders/export-feed

Returns a paginated work-queue of completed orders for an export job, using the same `OrderData` shape as `/api/v1/orders`.

> **Warning — Write Side Effect:** This endpoint has a write side effect. Fetching it stamps a `woe_order_exported` meta value onto orders that have never been exported before. This is the **only write path** in the application. The feed returns completed orders not yet exported, plus orders completed within the last `export_recheck_days` days.

#### Query Parameters

| Parameter | Type | Required | Default | Notes |
|---|---|---|---|---|
| `export_recheck_days` | integer | No | `2` | Min: 0. Orders completed within this many days are always re-included regardless of export status. |
| `page` | integer | No | `0` | Min: 0 |
| `size` | integer | No | `20` | Min: 1, max: 50 |

#### curl Example

```bash
curl "http://localhost:8081/api/v1/orders/export-feed?export_recheck_days=3&page=0&size=20"
```

#### Response

`Page<OrderData>` — same pagination envelope and `OrderData` shape as [GET /api/v1/orders](#get-apiv1orders). Refer to that endpoint's field tables.

---

### GET /api/v1/orders/abandoned

Returns a paginated **abandoned-checkout recovery list**: stalled or cancelled checkouts within a recent lookback window, collapsed to **one row per billing email**. An email is included only if it has **no** later completed or processing order within the same window (so customers who eventually succeeded are dropped). Read-only — no side effects.

Each lead carries an attempt count, the most recent attempt time, a staleness stage (`etapa`), and the distinct product names that email attempted.

#### Query Parameters

All thresholds default to the values of the original operational SQL. The three minute thresholds must be monotonic — `pending_min_minutes ≤ critical_minutes ≤ expired_minutes` — otherwise `400 INVALID_PARAM`.

| Parameter | Type | Required | Default | Notes |
|---|---|---|---|---|
| `pending_min_minutes` | integer | No | `10` | Min: 0. Minimum age (minutes) a `wc-pending` order must reach to count as a stalled attempt (the `pendiente_temprano` threshold). |
| `critical_minutes` | integer | No | `45` | Min: 0. Age boundary (minutes) for the `pendiente_critico` stage. |
| `expired_minutes` | integer | No | `60` | Min: 0. Age boundary (minutes) for the `pendiente_vencido` stage. |
| `lookback_days` | integer | No | `2` | Min: 0. Only consider attempts within this many days; also the window for the exclude-later-success check. |
| `page` | integer | No | `0` | Min: 0 |
| `size` | integer | No | `20` | Min: 1, max: 50 |

#### curl Example

```bash
curl "http://localhost:8081/api/v1/orders/abandoned?lookback_days=2&pending_min_minutes=10&page=0&size=20"
```

#### Response

`Page<AbandonedLeadData>` — standard pagination envelope with an array of `AbandonedLeadData` objects in `content`, ordered by most recent attempt first.

```json
{
  "content": [
    {
      "billing_email": "juan@example.com",
      "billing_first_name": "Juan",
      "billing_last_name": "Pérez",
      "billing_full_name": "Juan Pérez",
      "billing_phone": "+54 261 1234567",
      "billing_country": "AR",
      "attempts": 3,
      "last_attempt": "2026-06-22 18:45:30",
      "etapa": "pendiente_critico",
      "attempted_products": ["Día de Spa", "Masaje"]
    }
  ],
  "page": {
    "size": 20,
    "number": 0,
    "totalElements": 8,
    "totalPages": 1
  }
}
```

#### AbandonedLeadData Fields

| JSON Key | Type | Nullable | Description |
|---|---|---|---|
| `billing_email` | string | No | Grouping key — the billing email of the abandoned attempts |
| `billing_first_name` | string | No | Most recent value across the attempts |
| `billing_last_name` | string | No | |
| `billing_full_name` | string | No | First + last joined with a space |
| `billing_phone` | string | No | |
| `billing_country` | string | No | 2-letter country code |
| `attempts` | integer | No | Number of distinct stalled/cancelled orders for this email in the window |
| `last_attempt` | string | No | Most recent attempt date (site tz); `""` if absent |
| `etapa` | string | No | Staleness stage (raw Spanish slug, not translated) — see below |
| `attempted_products` | array of string | No | Distinct line-item names attempted; `[]` if none |

**`etapa` values** (derived from the most recent attempt's status and age):

| Value | Meaning |
|---|---|
| `cancelado` | Order is `wc-cancelled` |
| `pendiente_vencido` | `wc-pending`, older than `expired_minutes` |
| `pendiente_critico` | `wc-pending`, older than `critical_minutes` |
| `pendiente_temprano` | `wc-pending`, older than `pending_min_minutes` |

---

### GET /api/v1/gift-cards

Returns a paginated list of YITH gift cards.

> **Note:** Returns `503 YITH_GIFT_CARDS_INACTIVE` if the YITH Gift Cards plugin is not active (i.e. the `gift_card` post type is not registered in WordPress).

#### Query Parameters

| Parameter | Type | Required | Default | Notes |
|---|---|---|---|---|
| `code` | string | No | — | Exact gift-card code fast path |
| `date_from` | string | No | — | Inclusive lower bound; ISO date/datetime |
| `date_to` | string | No | — | Inclusive upper bound; ISO date/datetime |
| `status` | string (repeatable) | No | — | Wire values: `enabled`, `disabled`, `dismissed`, `pre-printed`. Repeat to OR values. |
| `order_id` | integer | No | — | Filter by purchase order ID. Min: 1. |
| `recipient` | string | No | — | Filter by recipient email; must be a valid email address |
| `orderby` | string | No | `date` | Values: `date`, `id`, `expiration` |
| `order` | string | No | `desc` | Values: `asc`, `desc` |
| `page` | integer | No | `0` | Min: 0 |
| `size` | integer | No | `20` | Min: 1, max: 50 |

#### curl Example

```bash
curl "http://localhost:8081/api/v1/gift-cards?status=enabled&recipient=ana@example.com"
```

#### Response

`Page<GiftCardData>` — standard pagination envelope.

```json
{
  "content": [
    {
      "gift_card_id": 789,
      "code": "ABCD-1234-EFGH",
      "status": "Activa",
      "amount_total": 10000.0,
      "balance_total": 7500.0,
      "currency": "ARS",
      "purchase_date": "2025-10-01 09:00:00",
      "order_id": 1200,
      "customer_id": 42,
      "sender_name": "María López",
      "recipient_name": "Ana García",
      "recipient_email": "ana@example.com",
      "message": "¡Feliz cumpleaños!",
      "is_digital": true,
      "delivery_date": "2025-10-02",
      "delivery_sent_date": "2025-10-02 08:00:00",
      "expiration_date": "2026-10-01",
      "internal_notes": "",
      "redeemed_in_orders": [1300],
      "redemption_history": []
    }
  ],
  "page": {
    "size": 20,
    "number": 0,
    "totalElements": 12,
    "totalPages": 1
  }
}
```

#### GiftCardData Fields

| JSON Key | Type | Nullable | Description |
|---|---|---|---|
| `gift_card_id` | integer | No | |
| `code` | string | Yes | |
| `status` | string | Yes | Translated label (e.g. `"Activa"`); not the filter enum wire value |
| `amount_total` | number | No | Original card value |
| `balance_total` | number | No | Remaining balance |
| `currency` | string | Yes | |
| `purchase_date` | string | Yes | Site tz |
| `order_id` | integer | No | Source order ID |
| `customer_id` | integer | No | |
| `sender_name` | string | Yes | |
| `recipient_name` | string | Yes | |
| `recipient_email` | string | Yes | |
| `message` | string | Yes | |
| `is_digital` | boolean | No | |
| `delivery_date` | string | Yes | Site tz |
| `delivery_sent_date` | string | Yes | Site tz |
| `expiration_date` | string | Yes | Site tz |
| `internal_notes` | string | Yes | |
| `redeemed_in_orders` | array of integer | Yes | Order IDs where the card was redeemed |
| `redemption_history` | array | Yes | Redemption events; **currently always returns `[]`** |

---

### GET /api/v1/products

Returns a flat list of all published WooCommerce products. **Not paginated.**

#### Query Parameters

None.

#### Response

A plain JSON array (not the pagination envelope) of `ProductData` objects.

```json
[
  {
    "id": 1234,
    "sku": "SPA-DAY",
    "name": "Día de Spa",
    "price": 15000.0,
    "punto-de-encuentro": "Recepción del hotel",
    "encuentro-hotel": "yes"
  },
  {
    "id": 5678,
    "sku": "MASAJE",
    "name": "Masaje Relax",
    "price": 8000.0,
    "punto-de-encuentro": null,
    "encuentro-hotel": null
  }
]
```

#### ProductData Fields

| JSON Key | Type | Nullable | Description |
|---|---|---|---|
| `id` | integer | No | Product post ID |
| `sku` | string | Yes | From `_sku` meta |
| `name` | string | No | Product title |
| `price` | number | No | From `_price` meta; `0.0` if missing or blank |
| `punto-de-encuentro` | string | Yes | Hyphenated key; custom bookable-product meta |
| `encuentro-hotel` | string | Yes | Hyphenated key; custom bookable-product meta |

---

### GET /api/v1/booking-services

Returns a flat list of YITH booking services (additional services master list). **Not paginated.**

#### Query Parameters

None.

#### Response

A plain JSON array (not the pagination envelope) of `BookingServiceData` objects.

```json
[
  { "id": 55, "name": "Traslado Spa" },
  { "id": 99, "name": "Espero en la puerta" }
]
```

#### BookingServiceData Fields

| JSON Key | Type | Nullable | Description |
|---|---|---|---|
| `id` | integer | No | `term_id` of the `yith_booking_service` taxonomy term |
| `name` | string | No | Service name |

---

### GET /health

Connectivity check. Not part of the ported WooCommerce contract.

#### Query Parameters

None.

#### Response

```json
{ "db": "ok" }
```

`db` is `"ok"` when the database is reachable, or `"down"` when it is not.

---

## Error Examples

### 400 — Invalid date parameter

```
GET /api/v1/orders?date_from=2025-13-40
```

```json
{
  "code": "INVALID_DATE_PARAM",
  "message": "Invalid value for parameter 'date_from'.",
  "source": "date_from"
}
```

---

### 400 — Invalid enum value

```
GET /api/v1/orders?status=bogus
```

```json
{
  "code": "INVALID_PARAM",
  "message": "Invalid value for parameter 'status'.",
  "source": "status"
}
```

---

### 400 — Page size constraint violation

```
GET /api/v1/orders?size=999
```

```json
{
  "code": "INVALID_PARAM",
  "message": "Invalid value for parameter 'size'.",
  "source": "size"
}
```

---

### 503 — YITH Gift Cards plugin inactive

```
GET /api/v1/gift-cards
```

```json
{
  "code": "YITH_GIFT_CARDS_INACTIVE",
  "message": "The gift_card post type is not registered.",
  "source": null
}
```
