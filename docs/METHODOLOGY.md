# Measure the workload before drawing the conclusion

## Scope of the historical evidence

The original study measures elapsed query/operation times on one local machine, across three data sizes and two read passes. Raw JMeter exports, repeated independent trials, confidence intervals, and a full environment manifest were not supplied in the source repository.

The checked-in plan is a starting point for further investigation. Its default single-thread configuration does not establish stress capacity or multi-user scalability.

## Known limits to address

The table below describes the historical experiment. This publication externalizes configuration, reuses the MongoDB client within each thread/iteration, closes it on normal iteration completion, and disables GUI listeners by default. These changes have not been benchmarked; the historical measurements still describe the original implementation.

| Issue | Why it matters | Next validation |
| --- | --- | --- |
| Different result consumption | Timing can include different amounts of transfer and client processing | Consume and verify equivalent rows on both sides |
| Collection/key mismatches | The same label may target different data | Normalize casing, key types, and predicates |
| Relational data migrated to MongoDB | Tests do not represent an independently designed document model | Evaluate a second, native document-model track |
| Two read passes | Cache effects and random variation cannot be isolated | Define warm-up and collect repeated independent runs |
| Connection preprocessor scope | MongoDB setup may execute before each sampler and create new clients | Inspect lifecycle, reuse policy, and client cleanup |
| Sequential execution order | Earlier workloads may affect later workloads and caches | Restore baselines and alternate engine order |
| Synthetic data differences | Different values/distributions can change selectivity | Generate a shared deterministic dataset |
| Index-oriented labels | Index definitions and actual use are not established by names | Capture SQL plans and MongoDB explain output |
| GUI listeners and logging | Client overhead can affect measurements | Use non-GUI execution and controlled logging |
| Mutating setup operations | Dataset state can vary between runs | Restore a known snapshot for every trial |

## A stronger measurement protocol

1. Specify the question: latency at a fixed load, throughput under rising load, or repeat-execution behavior.
2. Freeze engine, driver, Java, JMeter, hardware, and storage settings in a run manifest.
3. Match logical records, query predicates, returned fields, cardinality, indexes, and durability settings.
4. Confirm correctness using counts and representative values before comparing time.
5. Standardize connection reuse and result consumption.
6. Define warm-up, run duration, concurrency, ordering, and reset policy.
7. Export all samples, including failures; capture CPU, memory, I/O, and connection activity alongside timings.
8. Repeat independent trials. Report sample counts, error rate, throughput, and latency distributions where sample size supports them.
9. Publish the exact configuration and raw results behind each comparison.

## Reading the historical CSV

`elapsed_ms` is the reported time in milliseconds, not a newly measured value. Each row records a query, dataset scenario, pass, and engine. Write operations have only a first-pass entry. A reported zero is retained as zero and must not be interpreted as zero execution cost.

The CSV was transcribed from the original report's table on page 44. It is a secondary transcription, not a raw JMeter export. Consult the original table when auditing the values.

## Research roadmap

- Establish equivalent queries and complete result consumption.
- Package schema, import, index, and scenario reconstruction steps.
- Replace random independent seeds with shared deterministic data.
- Add controlled concurrency and failure reporting.
- Compare relationally shaped and native document models as separate experiments.
- Publish repeatable runs with raw exports and environment manifests.
