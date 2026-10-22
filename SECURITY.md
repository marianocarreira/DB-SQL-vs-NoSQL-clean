# Security scope

This snapshot removes known literal laboratory credentials and personal filesystem paths from the original JMeter assets. Connection settings are external, writes require explicit opt-in in the JMeter plan, and the publication starts with a new root commit.

The original PDF and screenshots are **excluded**, not certified clean. They remain in the owner's private source clone. This repository contains only reviewed text assets, workload scripts and the historical-results CSV. It does not contain the recovered configuration, source Git objects, database backups, driver binaries, raw logs or raw result exports.

The automated checks validate the publication file set and configuration structure. Gitleaks additionally scans the final working tree and the new Git history. They are evidence within that scope, not a proof that all future edits or runtime deployments are secure.

No credential was tested against a service, revoked or rotated. The owner must rotate or retire any previously exposed credentials. No database benchmark was executed as part of publication. The historical queries retain known methodological issues documented in `docs/METHODOLOGY.md`.

For a security concern, use GitHub's private vulnerability reporting if enabled. Do not put real passwords, connection strings or sensitive logs in public issues.
