# QA Decision Log

| Date | Decision | Reason |
| --- | --- | --- |
| 2026-10-20 | Treat `QA-HANDOFF.md` as the automation contract. | It records current implementation and supersedes legacy plan ambiguity. |
| 2026-10-20 | Start with auth/RBAC rather than generate all inventory. | The requested initial scope is a verified architecture and first executable vertical slice. |
| 2026-10-20 | Test Viewer `/revenue` as a redirect to `/forbidden`, not HTTP 403. | The handoff explicitly distinguishes page routing from CSV API authorization. |
| 2026-10-20 | Keep Katalon profile values outside test scripts. | Enables local/environment overrides; no new environment secret profile was committed. |
| 2026-10-20 | Do not claim execution success. | SUT health was verified, but Katalon Studio/Runtime CLI was not found on this host. |
| 2026-10-20 | Use Katalon's API/Web Service import workflow when a standalone microservice publishes an OpenAPI/Swagger definition or Postman collection. | Imported request objects suit independently runnable, contract-driven API suites. This dashboard has only a small stateful Auth.js/export flow and no published API definition, so its explicit cookie-aware `ApiClient` remains the simpler fit. |

The original plan describes Viewer Revenue access as “403” in one historic section, while the current handoff says `/forbidden`; the handoff was used.
