# Automation Architecture

This is a hybrid POM design. Native Katalon test cases own scenarios and assertions. Page classes own navigation and meaningful page operations. Components hold reusable shell/filter behavior. The Object Repository contains stable, high-value shared elements; `TestObjectFactory` creates scoped dynamic objects for repeatable rows and future chart buckets, preventing an oversized duplicate repository.

`TS_Smoke` is a quick login/RBAC suite. `TS_RBAC` isolates role checks. Each case creates a new browser and closes it in `finally`, so Viewer and Manager cookies never leak across scenarios. Future suite-level data binding will consume the CSV files in `Data Files`; it must be created in Katalon Studio where the native binding metadata can be verified.

Selectors follow the current handoff exactly: CSS `data-testid` hooks, raw `data-value`, UTC `data-iso`, and chart local bucket keys. No visual CSS classes, XPath paths, or OCR are used.
