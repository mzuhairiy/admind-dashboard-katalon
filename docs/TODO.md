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
- [x] Re-run `TS_RBAC` in Katalon Studio after the helper correction; the user
  reports the suite passed on 2026-10-27. The 2026-10-28 rerun passed 3/3;
  its HTML report is recorded in `KATALON_EXECUTION_LOG.md`.

## P0 — implement next

- [x] Add `TC_Environment_Preflight` as the first case in both P0 and P1
  regression suites. It checks `/api/health` returns 200 and verifies the UTC
  30-day fixed-seed KPIs before the rest of either suite runs.
- [x] `TS_P0_Regression` and `TS_P1_Regression` passed in Katalon Studio per
  the user's 2026-10-23 confirmation with the new preflight included. Both
  suites also passed on 2026-10-28; report links are recorded in
  `KATALON_EXECUTION_LOG.md`.
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
- [x] Create `TC_Filter_UrlTimezoneDataDriven`, the registered CSV test data,
  and native suite binding for `Data Files/filter_scenarios.csv`.
- [x] `TS_Filter_DataDriven` passed in Katalon Studio per the user's
  2026-10-22 confirmation, after correcting the CSV data-file metadata.
- [x] `TS_P0_Regression` passed in Katalon Studio per the user's 2026-10-22
  confirmation after the recent UI test updates. Its 2026-10-28 report is
  available in `Reports/` and linked from `KATALON_EXECUTION_LOG.md`.

## P1 — after P0 is green

- [x] Transactions: sorting, pagination, page-size, out-of-range page clamping,
  and empty state are implemented in `TC_Transactions_SortPagination` and
  passed in the user-reported `TS_P1_Regression` run on 2026-10-21.
- [x] Revenue: monthly chart, category drill-down, and explicit `preset=30d`
  persistence are implemented in `TC_Revenue_MonthlyDrilldown` and passed in
  the user-reported `TS_P1_Regression` run on 2026-10-21.
- [x] Settings: timezone/currency save, success toast, persisted defaults, and
  restoration are implemented in `TC_Settings_PersistedPreferences` and passed
  in the user-reported `TS_P1_Regression` run on 2026-10-21.
- [x] Regression: refunded-only zero revenue and nonzero orders are implemented
  in `TC_Dashboard_RefundedRevenueRegression` and passed in the user-reported
  `TS_P1_Regression` run on 2026-10-21. Table/export query parity is covered by
  P0 CSV-001.
- [x] Add `TC_Dashboard_CurrencyTooltipFormatting` using the SUT's stable
  `chart-tooltip` / `chart-tooltip-value` hooks. It checks revenue trend, top
  category, and Revenue breakdown tooltips under IDR, USD, and SGD, and confirms
  raw amounts are unchanged while display formatting follows the currency.
- [x] `TC_Dashboard_CurrencyTooltipFormatting` passed as part of
  `TS_P1_Regression` in Katalon Studio per the user's 2026-10-26 confirmation.
  Follow-up hover failures exposed an outdated SUT image that lacked
  `chart-tooltip-value`. The app image was rebuilt on 2026-10-28, then the P1
  suite passed 6/6; its report is linked from `KATALON_EXECUTION_LOG.md`.
- [x] `TS_P1_Regression` passed in Katalon Studio per the user's 2026-10-26
  confirmation, including the monetary tooltip currency test. The 2026-10-28
  report confirms the rerun after rebuilding the SUT also passed.

## Data-driven follow-up

- [x] Validate the native `TS_Filter_DataDriven` data binding in Katalon Studio;
  the user reports the suite passed on 2026-10-22.
- [x] Add `TC_Filter_CategoryStatusDataDriven` for single/multiple category and
  status filters plus unknown-category empty results. Its original three-row
  native data binding and dashboard/chart/table/CSV parity passed in Katalon
  Studio per the user's 2026-10-26 confirmation.
- [x] Extend the same DDT with repeated category/status keys (first value wins),
  mixed valid/invalid status values (invalid values are dropped), and invalid-
  only status fallback to all statuses. The seven-row matrix and assertions are
  implemented; the user reports the expanded run passed in Katalon Studio on
  2026-10-27. After rebuilding the SUT on 2026-10-28, the case exposed three
  stale test assumptions: dashboard empty results omit KPI cards, zero paid
  revenue may omit category bars, and invalid status tokens remain in the raw
  URL while being dropped from the effective filter. Assertions now follow the
  documented SUT contract. The 2026-10-28 report confirms the updated
  seven-row matrix passed within the 10/10 DDT suite. Fixed-seed oracle values
  must remain explicit and reviewed when adding future scenarios.

## Execution hygiene

- [x] Automate the `/api/health` and fixed-seed preflight as the first case in
  each P0/P1 full regression suite; both updated suites passed in Katalon Studio
  per the user's 2026-10-23 confirmation.
- [x] Keep credentials and URLs in execution profiles; keep local profile
  overrides and generated `Reports`, `Downloads`, `Libs`, and `bin` out of Git.
  Verified against `Profiles/default.glbl`, `.gitignore`, and tracked profile
  files. Documented the local override workflow in `EXECUTION_HYGIENE.md`.
- [x] Record environment metadata and report artifacts for the 2026-10-28
  suite runs. Katalon version, browser, OS, report links, and profile `default`
  are verified from the retained execution properties and reports in
  `KATALON_EXECUTION_LOG.md`. Older historical runs lack those artifacts, so
  their missing metadata remains explicitly undocumented rather than guessed.
