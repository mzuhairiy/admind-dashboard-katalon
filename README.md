# Admin Dashboard Katalon Automation

Katalon Studio 10.4.1 automation project for the read-only Admin Dashboard SUT. The suite targets business-risk areas: authentication/RBAC, deterministic analytics values, URL and timezone state, transaction data, CSV export, revenue, and settings using hybrid POM.

## Current implementation

The implemented vertical slice is Phase 1 plus minimal UI RBAC: Viewer sign-in, invalid sign-in, Viewer Revenue/CSV visibility restrictions and `/revenue` redirect, and Manager Revenue access. It has not been executed from Katalon on this host because no Katalon Runtime/Studio command-line executable was discoverable.

## Architecture

- `Keywords/pages`: page-focused operations with no scenario assertions.
- `Keywords/components`: cross-page UI components.
- `Keywords/utils`: dynamic, test-id based `TestObject` construction.
- `Object Repository`: important shared roots and sign-in controls.
- `Test Cases`: independently runnable Katalon cases; `Scripts` holds each case script.
- `Test Suites`: native GUI-runnable suites.
- `Profiles/default.glbl`: URL, timeouts, and local seeded test configuration.

The contract is [`docs/QA-HANDOFF.md`](docs/QA-HANDOFF.md). Its current behavior takes precedence over the original plan when they differ.

## Prerequisites and use

1. Start the SUT at `http://localhost:3000` and verify `GET /api/health` returns 200.
2. Open `admin-dashboard-katalon.prj` in Katalon Studio 10.4.1 or compatible.
3. Select the `default` execution profile. For non-local environments create an uncommitted profile for URL and credentials.
4. Run `Test Suites/TS_Smoke` in Chrome from Katalon Studio.

Edit Groovy source in VS Code, then refresh the project in Katalon Studio. Do not commit generated `Reports`, `bin`, browser downloads, or an environment-specific secret profile.

## Test data

The seeded accounts and deterministic data are described in the handoff. The default UTC 30-day KPI oracle is Revenue `225641000`, Orders `544`, AOV `480087`, Refund rate `6.1`; it must only be asserted after explicitly selecting `tz=UTC` and no other filters.

## Known limitations

CSV API status tests, KPI/chart numerical oracles, data-bound test-suite execution, transaction/table checks, and settings coverage are intentionally next-phase work. `role_scenarios.csv` and `filter_scenarios.csv` are prepared inputs; no suite-level binding is claimed until configured and exercised in the Katalon GUI.
