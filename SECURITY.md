# Security Policy

This project handles traditional chiefs' and village heads' administrative
workflows. Treat vulnerabilities as potentially high impact even when the demo
data is synthetic.

## Do Not Disclose Publicly

Report privately before opening public issues for:

- credential exposure
- real community or operator data exposure
- authorization bypass
- Chiefs Governor bypass
- scope-enforcement bypass (attempts to issue rulings, allocate resources, or
  resolve disputes directly)
- audit-ledger tampering
- over-disclosure in reports or exports
- unsafe administrative action dispatch

## Reporting

Use GitHub private vulnerability reporting when available for the repository.
If that is unavailable, contact the repository maintainers through the
gftdcojp organization before publishing details.

Include:

- affected commit or version
- reproduction steps
- expected and actual behavior
- impact on community data, policy enforcement, scope boundaries, or audit logging
- suggested fix, if known

## Production Guidance

- Store secrets outside Git.
- Keep real community/operator data outside this repository.
- Run policy tests before deployment.
- Export and review audit logs regularly.
- Use least privilege for operators and service accounts.
- **Scope enforcement is non-negotiable**: never allow this actor to issue
  customary rulings, allocate land/resources, or resolve disputes. These
  remain the chief's exclusive traditional and legal authority.
