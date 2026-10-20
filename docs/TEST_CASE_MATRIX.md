# Test Case Matrix

| ID | Feature | Scenario | Priority | Expected result | Status |
| --- | --- | --- | --- | --- | --- |
| AUTH-001 | Authentication | Viewer signs in | P0 | Dashboard loads | Passed, user-reported |
| AUTH-002 | Authentication | Bad credentials | P0 | Inline error; no session | Passed, user-reported |
| RBAC-001 | RBAC | Viewer visibility and Revenue deep link | P0 | No Revenue/CSV; `/forbidden` | Passed, user-reported |
| RBAC-002 | RBAC | Manager Revenue access | P0 | Nav and `/revenue` available | Passed, user-reported |
| RBAC-003 | Export API | Unauth/Viewer/Manager statuses | P0 | 401/403/200 | Passed, user-reported |
| DATA-001 | Dashboard | UTC 30-day KPI/chart aggregation | P0 | Match fixed independent oracle | Passed, user-reported |
| FILTER-001 | URL/timezone | Refresh, deep link, invalid/repeated params | P0 | Contract fallback/first-key behavior | Passed, user-reported |
| CSV-001 | CSV | Filtered export integrity | P0 | Header, filename, RFC4180 rows/order | Passed, user-reported |
| TX-001 | Transactions | Sort, page-size, next page, oversized page, empty state | P1 | Correct ordered rows and pagination state | Implemented, unexecuted |
| REV-001 | Revenue | UTC 30D month and category drill-down | P1 | Independent totals and persistent 30D selection | Implemented, unexecuted |
| SETTINGS-001 | Settings | Save timezone/currency and restore originals | P1 | Toast, persisted preferences, updated default timezone | Implemented, unexecuted |
| DATA-002 | Dashboard | Refunded-only revenue regression | P1 | Zero paid revenue, nonzero orders, zero revenue charts | Implemented, unexecuted |
