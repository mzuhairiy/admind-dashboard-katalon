# Automation TODO

This checklist is derived from [QA-HANDOFF.md](QA-HANDOFF.md), which is the
current SUT contract, and the P0/P1 priorities in [TEST_STRATEGY.md](TEST_STRATEGY.md).
Mark an item complete only after its Katalon test case, supporting keywords, and
relevant native suite configuration have been exercised successfully.

## Current baseline

- [x] AUTH-001 — Viewer sign-in; included in the user-reported passing P0 suite.
- [x] AUTH-002 — Invalid credentials; included in the user-reported passing P0 suite.
- [x] RBAC-001 — Viewer visibility and `/forbidden` redirect; included in the
  user-reported passing P0 suite.
- [x] RBAC-002 — Manager Revenue access; included in the user-reported passing
  P0 suite.
- [x] Correct `NavigationComponent.hasRevenueNavigation()` so it returns the
  presence result rather than its inverse.
- [ ] Re-run `TS_RBAC` in Katalon Studio after the helper correction and retain
  the execution result. This is a validation task, not a claim that the suite
  has already passed.

## P0 — implement next

- [x] **RBAC-003: CSV API authorization.** Implemented as an isolated API test case for
  `GET /api/export/csv`: unauthenticated = 401, Viewer = 403, Manager = 200.
  It authenticates using the Auth.js CSRF/credentials flow and was included in
  the user-reported passing P0 suite.
- [x] **DATA-001: UTC 30-day dashboard oracle.** Implemented a `DashboardPage` model
  that reads raw `data-value` attributes and chart bucket attributes. Set
  `preset=30d&tz=UTC`, then assert Revenue `225641000`, Orders `544`, AOV
  `480087`, and refund rate `6.1`, as well as chart-sum consistency.
- [x] **FILTER-001: URL and timezone contract.** Implemented coverage for fresh-session deep
  links, refresh persistence, invalid presets preserving valid filters, and
  repeated query keys using the first value. Include a timezone check proving
  localized text changes while `data-iso` stays unchanged.
- [x] **CSV-001: filtered export integrity.** Implemented an API-level Manager
  export check for an active filter set: exact header, deterministic filename,
  RFC 4180 parsing, row count/order, raw amounts, and parity with the rendered
  table. A separate Chrome browser-download smoke test is now implemented for
  the Export button so UI wiring and file delivery are also covered. Both
  cases were included in the user-reported passing P0 suite.
- [ ] Create the corresponding native Katalon suite/data binding for
  `Data Files/filter_scenarios.csv`. Verify the binding in Katalon Studio;
  do not treat the CSV file alone as configured data-driven execution.
- [x] `TS_P0_Regression` passed in Katalon Studio per the user's 2026-10-20
  confirmation. The passing report is not currently present in this workspace;
  attach or retain that report for the final handoff.

## P1 — after P0 is green

- [ ] Transactions: sorting, pagination, page-size, out-of-range page clamping,
  and empty state are implemented in `TC_Transactions_SortPagination`; run in Katalon.
- [ ] Revenue: monthly chart, category drill-down, and explicit `preset=30d`
  persistence are implemented in `TC_Revenue_MonthlyDrilldown`; run in Katalon.
- [ ] Settings: timezone/currency save, success toast, persisted defaults, and
  restoration are implemented in `TC_Settings_PersistedPreferences`; run in Katalon.
- [ ] Regression: refunded-only zero revenue and nonzero orders are implemented
  in `TC_Dashboard_RefundedRevenueRegression`; run in Katalon. Table/export
  query parity is covered by P0 CSV-001. Chart tooltip formatting for USD/SGD
  still needs a stable SUT test hook or a deliberately scoped tooltip assertion.
- [ ] Run `TS_P1_Regression` in Katalon Studio and record actual results.

## Execution hygiene

- [ ] Confirm `GET /api/health` is 200 and the app uses the documented fixed
  seed before each full run.
- [ ] Keep credentials and URLs in execution profiles; never commit a local
  profile or generated `Reports`, `Libs`, `bin`, or downloads.
- [ ] Record each executed suite's environment, date, and outcome in the QA
  handoff or a run report only after it genuinely runs.
