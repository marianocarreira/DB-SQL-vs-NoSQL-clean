# Configuration and safe execution

All runtime settings live in an external Java-properties file. `benchmark.example.properties` is the **public template**, not a working connection. Copy it to `benchmark.local.properties`, which is ignored by Git, or use a file outside this checkout. Never commit the recovered historical configuration or new credentials.

## Prerequisites and dataset

Use Apache JMeter 5.6.3 or a separately validated compatible version, an appropriate Java runtime, a Microsoft SQL Server JDBC driver, and a coherent MongoDB Java 3.x driver set compatible with the plan's legacy `com.mongodb.MongoClient` and `com.mongodb.client` APIs. Javadoc archives are not runtime drivers. Install drivers in JMeter's `lib` directory and restart JMeter. Check maintained versions and advisories before using any historical dependency; no driver binaries are distributed here.

Provision disposable SQL Server and MongoDB databases. Restore/import corresponding Stack Overflow data and create/verify indexes. Database backups, automated provisioning and an executable import pipeline are not supplied. Verify collection names, key types, record counts and query equivalence before running the workload. Standalone MongoDB scripts and the JMeter plan historically use different casing and key representations.

## Properties

| Property | Purpose and accepted values |
| --- | --- |
| `benchmark.profile` | `local` or `remote`. Local requires literal loopback hosts. |
| `benchmark.targetConfirmed` | Leave blank until you verify BOTH targets; then set `DISPOSABLE_DATABASES`. This is an acknowledgement, not automatic detection of production. |
| `benchmark.allowWrites` | `false` by default; `true` enables the six JMeter insert/update/delete samplers. |
| `benchmark.writeRows` | JMeter insert size, 1-500000; default template 1000. Other workload predicates are historical examples. |
| `benchmark.threads` | 1-100; default 1. More threads change load and write counts. |
| `benchmark.loops` | 1-1000; default 2. |
| `benchmark.rampSeconds` | 1-86400; default 1. |
| `sql.url` | SQL Server JDBC URL, including explicit `databaseName`, `encrypt` and `trustServerCertificate=false`. No credentials in the URL. |
| `sql.username` | Database-scoped benchmark login. `sa` is rejected. |
| `sql.password` | Fresh password, supplied only locally. |
| `mongo.host` / `mongo.port` | MongoDB hostname and port, 1-65535. Local accepts `localhost` or `127.0.0.1`. |
| `mongo.database` | Explicit target data database. |
| `mongo.authDatabase` | Database where the benchmark user is authenticated; independently configurable. |
| `mongo.collection` | Target posts collection; default template `Posts`. |
| `mongo.username` / `mongo.password` | Fresh database-scoped credentials, supplied locally. |
| `mongo.tls` | Explicit `true` or `false`; remote requires `true`. Hostname verification remains enabled. |

Usernames in the example are placeholders. No accounts are created. Use only the rights needed on the disposable databases. Provisioning and `IDENTITY_INSERT` may require additional privileges on those test objects; do not grant server-wide administrator privileges to make the benchmark convenient.

## TLS

The template enables SQL encryption and MongoDB TLS. Configure valid server certificates and the JVM trust store as required by your drivers. Do not use certificate-validation bypasses. Remote mode rejects plaintext for either engine. SQL must explicitly use `trustServerCertificate=false`.

For an isolated, disposable experiment on the SAME computer, you may deliberately choose `benchmark.profile=local`, use loopback hosts and set SQL `encrypt=false` / MongoDB `mongo.tls=false`. This changes the security and measurement conditions. The validator blocks that combination with remote hosts; do not use a tunnel/forwarder to disguise a remote service as loopback.

## Run from the repository root

The JMeter external Groovy script paths are relative to the current working directory. Start both CLI and GUI JMeter from this repository's root. Do not launch the `.jmx` by double-clicking it from an unrelated directory.

```powershell
Copy-Item config/benchmark.example.properties config/benchmark.local.properties
# Edit the local file using a text editor; do not paste passwords in the shell command.
New-Item -ItemType Directory -Force results/raw
jmeter -n -t Jmeter/Jmeter_SQLvsMongo.jmx -q config/benchmark.local.properties -l results/raw/run-001.jtl -j results/raw/jmeter.log
```

For the GUI, use `jmeter -q config/benchmark.local.properties -t Jmeter/Jmeter_SQLvsMongo.jmx` from the root. The setup sampler validates configuration and stops the test on rejection. MongoDB connection initialization is scoped to the main workload group, after setup validation. `CONFIGURATION_REJECTED` intentionally avoids printing supplied values; compare your file with the property table above.

JMeter uses Java-properties escaping. Escape backslashes as `\\` and use `\uXXXX` escapes for non-ASCII characters when required by the properties loader. Keep local files readable only by your user; they are plaintext secrets, not encrypted vaults. Prefer an external secret store for shared/remote environments. Never pass passwords using `-J...` arguments or embed them in URLs, screenshots or shared logs.

## Mutating operations

Reads are enabled; the six JMeter mutating samplers are inside `benchmark.allowWrites` conditions. Enabling writes runs insert/update/delete examples against the selected databases. Writes in a Once Only Controller can run once per thread. Inspect predicates and restore a baseline before each trial. A successful configuration validation does not prove that the workload is equivalent, correct, non-destructive or suitable for a production dataset.

Standalone `.sql` / `.mongo` files are manual examples. They do not read the JMeter property file or obey its write switch. Select a disposable database through your authenticated client, review each script, and run it intentionally. `Scripts/MongoDb/A_0.mongo` relies on the `db` selected by your mongosh session instead of opening a hardcoded connection. Its insert size remains a script-local example parameter.

Raw results can contain queries, returned values or driver errors. Keep `results/raw/`, logs and generated reports private. Publish only reviewed aggregate results. The committed CSV contains the historical transcription, not raw execution logs.

## Historical credentials

The publication process recovered old laboratory settings into a separate LOCAL private file outside this checkout. That file is not copied, committed or uploaded. Treat those values as exposed if they were public; rotate/retire them before reuse. New history and new visibility do not invalidate credentials or remove other people's copies.
