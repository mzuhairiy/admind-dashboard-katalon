# Katalon execution log

Historical entries below are user-reported confirmations. The 2026-10-28
entries are verified from the Katalon HTML/JUnit reports in `Reports/`.

| Suite / scope | Date (UTC) | Result | Environment details retained |
| --- | --- | --- | --- |
| `TS_P1_Regression` (transactions, Revenue, Settings, refunded-revenue regression) | 2026-10-21 | Passed | Katalon Studio; exact version, browser, profile, and report not retained |
| `TS_Filter_DataDriven` (timezone/URL DDT) | 2026-10-22 | Passed | Katalon Studio; exact version, browser, profile, and report not retained |
| `TS_P0_Regression` (after UI test updates) | 2026-10-22 | Passed | Katalon Studio; exact version, browser, profile, and report not retained |
| `TS_P0_Regression` and `TS_P1_Regression` (with `TC_Environment_Preflight`) | 2026-10-23 | Passed | Katalon Studio; exact version, browser, profile, and report not retained |
| `TS_Filter_DataDriven` (initial category/status DDT and existing timezone DDT) | 2026-10-26 | Passed | Katalon Studio; exact version, browser, profile, and report not retained |
| `TS_P1_Regression` (including currency tooltip formatting) | 2026-10-26 | Passed | Katalon Studio; exact version, browser, profile, and report not retained |
| `TS_Filter_DataDriven` (expanded seven-row category/status DDT) | 2026-10-27 | Passed | Katalon Studio; exact version, browser, profile, and report not retained |
| `TS_RBAC` (after `hasRevenueNavigation()` correction) | 2026-10-27 | Passed | Katalon Studio; exact version, browser, profile, and report not retained |
| `TS_P0_Regression` | 2026-10-28 02:43 UTC | Passed (10/10) | Katalon 10.4.1.0; Chrome 152 headless; Windows 11; profile `default`; [HTML report](../Reports/20261028_024349/TS_P0_Regression/20261028_024349/20261028_024349.html) |
| `TS_P1_Regression` (includes currency tooltip formatting) | 2026-10-28 03:25 UTC | Passed (6/6) | Katalon 10.4.1.0; Chrome 152 headless; Windows 11; profile `default`; [HTML report](../Reports/20261028_032528/TS_P1_Regression/20261028_032529/20261028_032529.html) |
| `TS_Filter_DataDriven` (timezone + category/status scenarios) | 2026-10-28 04:24 UTC | Passed (10/10) | Katalon 10.4.1.0; Chrome 152 headless; Windows 11; profile `default`; [HTML report](../Reports/20261028_042441/TS_Filter_DataDriven/20261028_042441/20261028_042441.html) |
| `TS_RBAC` | 2026-10-28 04:51 UTC | Passed (3/3) | Katalon 10.4.1.0; Chrome 152 headless; Windows 11; profile `default`; [HTML report](../Reports/20261028_045135/TS_RBAC/20261028_045135/20261028_045135.html) |
| `TS_Smoke` | 2026-10-28 04:53 UTC | Passed (3/3); expected absence-check warnings in log | Katalon 10.4.1.0; Chrome 152 headless; Windows 11; profile `default`; [HTML report](../Reports/20261028_045301/TS_Smoke/20261028_045302/20261028_045302.html) |

For subsequent runs, append a row with the full non-secret environment record
described in [`EXECUTION_HYGIENE.md`](EXECUTION_HYGIENE.md). Do not mark a run
as passed without an actual execution confirmation or report.
