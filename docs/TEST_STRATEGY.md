# Test Strategy

## Scope and priority

P0 covers authentication/session isolation, role enforcement, deterministic dashboard KPIs, query/date/timezone conversion, and CSV access/integrity. P1 covers transaction sorting/pagination, Revenue detail, and persisted settings. Visual styling, third-party browser rendering quirks, and SUT CRUD flows are out of scope; this is a read-only analytics application.

## Test design and oracles

Use equivalence classes and boundary cases for role, preset, timezone, status, category, invalid/repeated query keys, and pagination. Numeric checks read raw `data-value`; dates read UTC `data-iso`; chart grouping keys read `data-date`/`data-month`. Independent integer aggregation is the oracle: revenue is PAID amounts, AOV is rounded PAID revenue / PAID count, and refund rate is refunded / all matching orders rounded to one decimal percentage. Avoid comparing two UI values derived from the same aggregation alone.

## Environment dependencies

The suite requires the local Docker SUT and its deterministic 5,000-row seed. It uses the fixed application time `2026-01-01T00:00:00Z`, never the host clock. Browser execution requires Katalon Studio/Runtime and a configured browser driver.

## Current status

Implemented but not executed: basic auth and UI RBAC. Planned: API export statuses (401/403/200), numeric chart/KPI checks, filters, CSV parser checks, and data binding. No failing defects or exploratory findings are claimed.
