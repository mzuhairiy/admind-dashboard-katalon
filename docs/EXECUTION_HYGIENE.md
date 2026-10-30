# Katalon execution hygiene

## Profiles and generated files

- `Profiles/default.glbl` is the committed profile for the local seeded SUT. It
  defines `baseUrl`, `viewerEmail`, `viewerPassword`, `managerEmail`, and
  `managerPassword`; the credentials are the disposable fixture accounts
  documented in `QA-HANDOFF.md`.
- For a different machine/environment, create a local profile such as
  `Profiles/local.dev.glbl`, update the same variables there, and select that
  profile in Katalon Studio's execution configuration. Do not put real or
  environment-specific credentials into test scripts, test data, or the
  committed default profile.
- `Profiles/local*.glbl` is ignored by Git. `Reports`, `Downloads`, `Libs`, and
  `bin` are also ignored. Keep execution reports outside the repository unless
  a sanitized report is intentionally added for a specific handoff.
- Never include passwords, tokens, cookie values, or full secret-bearing
  profile contents in run logs. Record only the profile name.

## Run record format

For every future suite run, record the suite, UTC date/time, result, Katalon
version, browser/version, SUT environment (local Docker or other), non-secret
profile name, and report location. If a detail was not captured, say so rather
than infer it. This project's historical confirmations are summarized in
`KATALON_EXECUTION_LOG.md`.
