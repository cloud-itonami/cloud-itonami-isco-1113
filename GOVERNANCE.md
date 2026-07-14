# Governance

`cloud-itonami-isco-1113` is an OSS open-occupation blueprint. Governance covers
both code and the operator model.

## Maintainers

Maintainers may merge changes that preserve these invariants:

- the Advisor cannot directly dispatch administrative actions or disclose records.
- Chiefs Governor remains independent of the advisor.
- hard policy violations cannot be overridden by human approval.
- **scope enforcement is absolute**: any proposal to issue customary rulings,
  allocate land/resources, or resolve disputes MUST be permanently blocked.
  The chief's traditional and legal authority is never automatable.
- every commit, hold and approval path is auditable.
- real community/operator data stays outside Git.

## Decision Records

Architecture decisions live in `docs/adr/`. Changes to the trust model,
storage contract, public business model, operator certification, scope
boundaries, or license should add or update an ADR.

## Operator Governance

Anyone may fork and operate independently. itonami.cloud certification is a
separate trust mark and should require security, audit, support and data-flow
review.

Certified operators can lose certification for:

- bypassing policy checks or scope-enforcement boundaries
- mishandling community/operator data
- misrepresenting certification status
- failing to respond to security incidents
- hiding material changes to customer-facing operation
- attempting to use this actor to make customary rulings, resource allocations,
  or dispute resolutions (scope violation)
