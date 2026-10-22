# SQL Server vs. MongoDB: a configurable benchmark study

**Real application data. Inspectable workloads. Explicit trade-offs.**

An academic comparison by **Mariano Carreira**, developed for the Database course in the Master's in Software Engineering at UNLP. The original study uses Stack Overflow data and Apache JMeter to investigate reads, joins, aggregations and bulk writes. The largest reported scenario contains more than 17 million posts.

This is a sanitized publication snapshot of the original project, with a **new Git history**. Connection settings are external, credentials are never supplied by the repository, and mutating JMeter workloads require an explicit opt-in.

## Start here

- **[Configuration / Configuración](config/README.md)**: every connection parameter, local vs. remote TLS, credentials and execution instructions.
- [Study and historical findings](docs/STUDY.md)
- [Methodology and limitations](docs/METHODOLOGY.md)
- [Workload catalog](docs/WORKLOADS.md)
- [Historical measurements](results/historical-results.csv)
- [Sanitization scope and security checks](SECURITY.md)

## Configuration

1. Copy `config/benchmark.example.properties` to `config/benchmark.local.properties`.
2. Supply **new, limited laboratory credentials** and select databases you can discard.
3. Read [the setup instructions](config/README.md), install compatible drivers, and explicitly confirm the targets.
4. From this repository's root directory, run:

```powershell
jmeter -n -t Jmeter/Jmeter_SQLvsMongo.jmx -q config/benchmark.local.properties -l results/raw/run-001.jtl -j results/raw/jmeter.log
```

Create `results/raw` first. This directory and local property files are ignored. The supplied example deliberately fails validation until you configure your own credentials and confirm disposable targets. No databases, credentials or backup files are bundled.

## What changed for publication

- Removed literal passwords, privileged default users and personal driver paths from the plan.
- Moved connection logic and validation into `config/`.
- Added TLS settings with certificate validation; plaintext is allowed only in an explicitly selected loopback laboratory profile.
- Disabled all six mutating JMeter samplers by default and parameterized their JMeter insert size.
- Reused MongoDB clients within each thread/iteration and added normal-iteration cleanup. Abrupt process termination can still bypass cleanup.
- Disabled GUI listeners and response-body capture by default.
- Preserved standalone workload scripts, except removal of a hardcoded MongoDB connection. Standalone mutating scripts remain manual and are **not** protected by the JMeter write switch.
- Replaced the original PDF/screenshots with textual study documentation and the historical results transcription. The original binary report is withheld pending a separate visual review.
- Started a new repository without original commits, branches, tags or binaries.

The original private repository is not rewritten or made public by this publication. Historic credentials must still be rotated or retired wherever they exist; a sanitized copy cannot revoke them.

## Interpreting results

Historical figures are observations from one academic experiment, **not a new benchmark run or a universal ranking**. The MongoDB data model was migrated from the relational representation. Query equivalence, result consumption and independently repeated trials require further validation. Security and connection changes in this edition also mean a new run cannot be assumed to reproduce the historical times.

Original authorship is preserved. No new license or change of ownership is introduced.
