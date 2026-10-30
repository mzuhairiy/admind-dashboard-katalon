# Test Strategy

## Scope and priority

P0 covers authentication/session isolation, role enforcement, deterministic dashboard KPIs, query/date/timezone conversion, and CSV access/integrity. P1 covers transaction sorting/pagination, Revenue detail, and persisted settings. Visual styling, third-party browser rendering quirks, and SUT CRUD flows are out of scope; this is a read-only analytics application.

## Test design and oracles

Use equivalence classes and boundary cases for role, preset, timezone, status, category, invalid/repeated query keys, and pagination. Numeric checks read raw `data-value`; dates read UTC `data-iso`; chart grouping keys read `data-date`/`data-month`. Independent integer aggregation is the oracle: revenue is PAID amounts, AOV is rounded PAID revenue / PAID count, and refund rate is refunded / all matching orders rounded to one decimal percentage. Avoid comparing two UI values derived from the same aggregation alone.

## Environment dependencies

The suite requires the local Docker SUT and its deterministic 5,000-row seed. It uses the fixed application time `2026-01-01T00:00:00Z`, never the host clock. Browser execution requires Katalon Studio/Runtime and a configured browser driver.

## Current status

P0, P1, the native CSV-driven filter suite, RBAC, and Smoke suites passed in
Katalon Studio. The 2026-10-28 reports record P0 (10/10), P1 (6/6), filter DDT
(10/10), RBAC (3/3), and Smoke (3/3), with environment and report links in
`KATALON_EXECUTION_LOG.md`. Both regression suites were also rerun successfully on 2026-10-23 with
`TC_Environment_Preflight` first, checking health and the independent fixed-seed
dashboard oracle. `TC_Dashboard_CurrencyTooltipFormatting` subsequently passed
as part of `TS_P1_Regression` on 2026-10-26, covering IDR/USD/SGD monetary
tooltips through the SUT's stable raw-value and currency hooks. Follow-up
trend-tooltip hover failures reported on 2026-10-27 and 2026-10-28 were traced
to an outdated app container whose production bundle lacked the monetary tooltip
hooks. The app was rebuilt and recreated on 2026-10-28; the health endpoint is
200, the deployed bundle contains `chart-tooltip-value`, and the P1 rerun passed.
CSV rows drive filter inputs and per-scenario expectations, not the oracle's source values.
`TS_Filter_DataDriven` includes category/status integrity scenarios. Its original
three category/status rows passed in Katalon Studio per user confirmation on
2026-10-26. The expanded seven-row set, including repeated category/status keys
and invalid-status semantics, passed in Katalon Studio per user confirmation on
2026-10-27. The updated 2026-10-28 run passed 10/10 after aligning assertions
with the SUT contract for empty results, zero-revenue charts, and raw versus
effective invalid-status values. This measures documented contract coverage,
not 100% source-code or branch coverage.
