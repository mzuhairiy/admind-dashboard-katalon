# Test Case Matrix

| ID | Feature | Scenario | Priority | Expected result | Status |
| --- | --- | --- | --- | --- | --- |
| AUTH-001 | Authentication | Viewer signs in | P0 | Dashboard loads | Implemented, unexecuted |
| AUTH-002 | Authentication | Bad credentials | P0 | Inline error; no session | Implemented, unexecuted |
| RBAC-001 | RBAC | Viewer visibility and Revenue deep link | P0 | No Revenue/CSV; `/forbidden` | Implemented, unexecuted |
| RBAC-002 | RBAC | Manager Revenue access | P0 | Nav and `/revenue` available | Implemented, unexecuted |
| RBAC-003 | Export API | Unauth/Viewer/Manager statuses | P0 | 401/403/200 | Planned |
| DATA-001 | Dashboard | UTC 30-day KPI/chart aggregation | P0 | Match fixed independent oracle | Planned |
| FILTER-001 | URL/timezone | Refresh, deep link, invalid/repeated params | P0 | Contract fallback/first-key behavior | Planned |
| CSV-001 | CSV | Filtered export integrity | P0 | Header, filename, RFC4180 rows/order | Planned |
