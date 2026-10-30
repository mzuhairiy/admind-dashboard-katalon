# Test Case Matrix

| ID | Feature | Scenario | Priority | Expected result | Status |
| --- | --- | --- | --- | --- | --- |
| ENV-001 | Environment | Health endpoint and deterministic seed preflight | P0 | `/api/health` = 200; UTC 30D KPIs match the fixed-seed oracle | Passed, user-reported 2026-10-23 |
| AUTH-001 | Authentication | Viewer signs in | P0 | Dashboard loads | Passed, user-reported |
| AUTH-002 | Authentication | Bad credentials | P0 | Inline error; no session | Passed, user-reported |
| RBAC-001 | RBAC | Viewer visibility and Revenue deep link | P0 | No Revenue/CSV; `/forbidden` | Passed, user-reported |
| RBAC-002 | RBAC | Manager Revenue access | P0 | Nav and `/revenue` available | Passed, user-reported |
| RBAC-003 | Export API | Unauth/Viewer/Manager statuses | P0 | 401/403/200 | Passed, user-reported |
| DATA-001 | Dashboard | UTC 30-day KPI/chart aggregation | P0 | Match fixed independent oracle | Passed, user-reported |
| FILTER-001 | URL/timezone | Refresh, deep link, invalid/repeated params | P0 | Contract fallback/first-key behavior | Passed, user-reported |
| FILTER-DDT-001 | URL filters | CSV-driven valid, invalid, and repeated query scenarios | P0 | Expected preset, timezone, status, and scenario behavior per row | Passed, user-reported 2026-10-22 |
| FILTER-DDT-002 | Category/status filters | Single/multiple categories, unknown category, repeated category/status keys, invalid status entry, invalid-only fallback | P1 | Dashboard, table, and CSV agree; repeated keys use first value, invalid statuses are dropped, invalid-only falls back to all statuses | Passed 10/10 in `TS_Filter_DataDriven`, report 2026-10-28 |
| CSV-001 | CSV | Filtered export integrity | P0 | Header, filename, RFC4180 rows/order | Passed, user-reported |
| TX-001 | Transactions | Sort, page-size, next page, oversized page, empty state | P1 | Correct ordered rows and pagination state | Passed, user-reported 2026-10-22 |
| REV-001 | Revenue | UTC 30D month and category drill-down | P1 | Independent totals and persistent 30D selection | Passed, user-reported 2026-10-22 |
| SETTINGS-001 | Settings | Save timezone/currency and restore originals | P1 | Toast, persisted preferences, updated default timezone | Passed, user-reported 2026-10-22 |
| DATA-002 | Dashboard | Refunded-only revenue regression | P1 | Zero paid revenue, nonzero orders, zero revenue charts | Passed, user-reported 2026-10-22 |
| DATA-003 | Dashboard | Monetary chart tooltip currency formatting | P1 | IDR/USD/SGD formatting changes while raw values remain invariant | Passed in `TS_P1_Regression` (6/6), report 2026-10-28 after SUT image rebuild |
