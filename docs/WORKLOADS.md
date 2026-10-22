# Workload catalog

The identifiers connect the standalone scripts, JMeter labels, and historical measurements. They describe intended workloads; equivalence must be checked against actual query code.

| ID | Intended operation | Standalone script stem |
| --- | --- | --- |
| 0.A | Insert 500,000 synthetic posts | SQL `0_A`; MongoDB `A_0` |
| 0.B | Update matching synthetic posts | SQL `0_B`; MongoDB `0_b` |
| 0.C | Delete posts with negative IDs | `0_C` |
| 1.A | Retrieve all posts | `1_A` |
| 1.B | Filter by post type | `1_B` |
| 1.C | Filter high-scoring posts | `1_C` |
| 1.D | Filter by owner | `1_D` |
| 2.A | Count posts by owner | `2_A` |
| 2.B | Count posts by type and filter groups | `2_B` |
| 2.C | Average score by post type | `2_C` |
| 2.D | Maximum score by owner | `2_D` |
| 3.A | Join posts with users | `3_A` |
| 3.B | Join posts, post types, and users | `4_B` |
| 4.A | Select posts scoring above average | `4_A` |
| 5.A | Identifier lookup | `5_A` |
| 5.B | Title prefix search | `5_B` |

The `4_B` filename is historical: the plan and report categorize it as 3.B. Files have not been renamed, preserving existing references.

## Important implementation details

- The historical MongoDB `0_b.mongo` file contains SQL syntax rather than a MongoDB command. Use the JMeter MongoDB update sampler as a reference and validate its predicate before running it.
- The standalone SQL seed script inserts 500,000 rows despite its original comment referring to one million. The standalone source is retained; the JMeter insert size is independently configurable in this sanitized edition.
- SQL and MongoDB seed generators use different random distributions and field assignments. They do not produce identical records.
- The standalone 5.A SQL script searches numeric `Id = 123`; its MongoDB counterpart searches a specific ObjectId. These do not establish equivalent logical lookups.
- The standalone MongoDB 3.A lookup returns an owner array without unwinding it, unlike an SQL inner join. Missing matches and cardinality require validation.
- SQL 3.A uses a user ID filter. Inspect the plan and standalone versions separately before assuming identical predicates.
- Query names mentioning indexes do not prove index use. Capture the actual execution plans.
- Several SQL samplers call `executeQuery` and close the result set without iterating all rows. MongoDB samplers may use `.last()`, iterate results, or log output. Their measured work can differ substantially.

Use this catalog as a navigation aid, not a certification that each pair is functionally equivalent.
