# Publication review - 2026-10-22

## Published scope

The publication snapshot contains the original SQL/MongoDB workload files and a sanitized JMeter plan, external configuration logic, a blank credential template, textual documentation and transcribed historical results. It begins with a new root commit; no source Git objects, tags or branches are imported.

The original PDF/screenshots, backup files, driver binaries, raw result logs and recovered configuration are withheld from the public repository. Their exclusion prevents their contents from entering this snapshot; it does not assert that the original assets have been fully audited or sanitized. The owner retains them in a separate private source clone.

## Changes

- Extracted SQL/MongoDB credentials into a separate owner-local configuration outside the published directory.
- Replaced JDBC literal URL/credentials with external properties and moved MongoDB initialization into `config/`.
- Removed the author's absolute Windows driver paths and the standalone MongoDB script's hardcoded connection.
- Added explicit target confirmation, validation of profile and TLS options, and separate MongoDB authentication/data databases.
- Placed the connection preprocessor inside the main workload group, so it does not run before configuration validation.
- Gated six JMeter mutating samplers behind `benchmark.allowWrites=false` by default; standalone files remain manual examples.
- Parameterized JMeter concurrency, loops, ramp and insert row count.
- Disabled GUI listeners and response-body capture by default. Raw result exports remain private and ignored.
- Added publication structure checks, custom Gitleaks rules and a read-only CI workflow with a pinned checkout action and a checksum-verified scanner.

## Validation performed

- Offline publication structure check passed.
- Gitleaks v8.30.1 working-tree scan with default and custom rules reported no findings.
- Sixteen offline configuration test cases passed, including remote plaintext rejection, certificate-bypass rejection, explicit target confirmation, administrator rejection and local-profile host checks.
- Thirty-two embedded/external Groovy scripts compiled with the historical driver API available. Compilation did not execute any database operations.
- Apache JMeter 5.6.3 loaded the full sanitized plan. With the incomplete public template, its only sample returned `CONFIGURATION_REJECTED`, then the engine stopped before starting the main group.
- The new Git history is scanned before pushing. The CI workflow repeats content/history scans on subsequent pushes and pull requests.

No live database authentication, benchmarks, credential rotation or dependency vulnerability audit was performed. TLS/driver behavior against actual servers and workload equivalence still require a configured laboratory. The legacy MongoDB driver used for offline compilation is a compatibility fixture, not a recommendation of a currently supported release.

Historic credentials may have been exposed before this publication. Their revocation/rotation is separate from source sanitation and must be completed by the owner wherever still applicable. A clean scan does not prove absence of every possible secret or deployment vulnerability.
