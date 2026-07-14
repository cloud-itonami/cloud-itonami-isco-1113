# Contributing

`cloud-itonami-isco-1113` accepts contributions to the OSS actor, policy tests,
documentation, examples and open occupation blueprint.

## Development

```bash
clojure -M:test
```

Keep changes small and include tests for policy, audit, store or disclosure
behavior.

## Rules

- Do not commit real community data, credentials or operating documents.
- Keep administrative actions and escalations behind Chiefs Governor.
- **Treat scope enforcement as absolute**: any proposal to issue customary
  rulings, allocate land/resources, or resolve disputes must be permanently
  blocked (:hard? true). The chief's traditional and legal authority is never
  automatable or delegable.
- Treat this occupation's workflows as high-risk: add tests for permission,
  purpose, safety and audit logging.
- Document any new business-model or operator assumption in `docs/`.

## Pull Requests

PRs should describe:

- what behavior changed
- which policy invariant is affected (especially scope-enforcement boundaries)
- how it was tested
- whether operator or certification docs need updates
