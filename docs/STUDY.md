# Inside the experiment

## Research question

How do SQL Server and MongoDB respond to comparable database operations as dataset size changes?

The original academic project investigated execution time using Stack Overflow data. Its strongest contribution is an inspectable comparison of specific workloads on a documented local environment.

## Experimental setting

| Component | Reported configuration |
| --- | --- |
| SQL engine | SQL Server 2022 RTM, 16.0.1000.6, Developer Edition, 64-bit |
| Document engine | MongoDB Community Edition 7.0.6 |
| Load tool | Apache JMeter; checked-in plan identifies version 5.6.3 |
| Computer | Lenovo ThinkPad X1 |
| Processor | Intel Core i7-1370P, 14 cores, 20 logical processors |
| Memory | 64 GB installed RAM |
| Execution model | Local engine comparison; two read passes |

These describe the historical study, not independently verified requirements or a newly reproduced environment.

## Dataset

The report selected a SQL Server distribution of the Stack Overflow dataset and migrated relevant data to MongoDB. Posts are the primary workload entity, with Users and PostTypes supporting relationship queries.

| Scenario | Posts | Users | PostTypes |
| --- | ---: | ---: | ---: |
| Large | 17,142,169 | 2,465,713 | 8 |
| Medium | 4,095,531 | 1,364,747 | 8 |
| Small | 59,421 | 870,164 | 8 |

The report describes the selected large dataset as approximately 50 GB. It does not provide an automated recipe for reconstructing all three subsets. These counts should be treated as historical scenario definitions.

## Design

Insert, update, and delete workloads run once at the start of each engine's test. The read workloads run twice to explore repeat-execution effects. The plan includes five query families: simple reads, aggregations, joins, complex queries, and index-oriented queries.

The original study includes a discussion of relational and document databases, candidate tools, and dataset selection. This edition condenses that background to focus on the experiment. Neither a database family nor its consistency guarantees can be characterized from query timing alone.

## Selected historical observations

All values below are milliseconds, taken from the report's results table, page 44. They have not been rerun. The complete transcription is in [historical-results.csv](../results/historical-results.csv).

| Large scenario, first pass | SQL Server | MongoDB |
| --- | ---: | ---: |
| 1.A: all posts | 297,046 | 94,958 |
| 2.A: post count by user | 2,770 | 52,235 |
| 3.A: posts and users | 138 | 10,545 |
| 4.A: score above average | 114,398 | 63,621 |
| 0.A: insert workload | 133,390 | 97,815 |

Several broad reads favored MongoDB in the recorded runs. Joins and many aggregations favored SQL Server. Many second-pass measurements improved, though the table also includes unchanged and slower results. Two passes do not isolate caching as the cause or establish statistical significance.

The MongoDB data model was adapted from relational data. The experiment therefore does not establish how an independently optimized document model would perform. Differences in result retrieval and query semantics further limit interpretation.

## What the evidence supports

The measurements illustrate how workload shape and test design affect observed timings. They provide useful starting points for investigation. They do not establish maximum throughput, behavior under concurrent users, production capacity, availability, transaction guarantees, or a universal database winner.

## Provenance

Source: Mariano Carreira, *Database Concepts and Comparisons*, UNLP Master's in Software Engineering, 2024; original report pages 18-19, 24, and 42-46. The owner retains the Spanish source in a private clone outside this publication. Its PDF and screenshots are not distributed here pending a separate visual review.
