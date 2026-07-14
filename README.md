# cloud-itonami-isco-1113

Open Occupation Blueprint for **ISCO-08 1113**: Traditional Chiefs and Heads of Villages.

This repository designs a forkable OSS business for a traditional community leader: an administrative support robot handles correspondence, meeting scheduling, and record-keeping under a governor-gated actor, so the practice keeps its own community records and workflows instead of renting a closed governance SaaS.

## Scope: Administrative Support, Not Authority

**CRITICAL**: This actor provides **administrative back-office support only**. It never itself issues customary rulings, land-allocation decisions, or dispute resolutions — those remain the human chief's/village-head's **exclusive traditional and legal authority**. This actor:

- ✓ Drafts correspondence for the chief's review and signature
- ✓ Schedules community meetings on the chief's authority
- ✓ Logs administrative records (population, resource registrations, etc.) for the chief's reference
- ✓ Surfaces disputes to the chief for the chief's own decision-making

This actor NEVER:

- ✗ Issues a customary ruling or judgment on a dispute (chief's role)
- ✗ Allocates land or resources directly (chief's role)
- ✗ Resolves conflicts without human escalation (chief's role)
- ✗ Overrides the chief's traditional or legal authority in any domain

## Robotics premise

All cloud-itonami verticals are designed on the premise that a **robot performs
the physical domain work**. Here an administrative robot performs correspondence drafting, meeting scheduling, and record-keeping under an actor that proposes
actions and an independent **Chiefs Governor** that gates them. The governor never
dispatches administrative tasks itself; `:high`/`:safety-critical` actions (such as
dispute flagging, or community record amendments) require human sign-off.

## Core Contract

```text
community request + existing records + chief's authority
        |
        v
Chiefs Advisor -> Chiefs Governor -> draft/schedule/log, or human sign-off
        |
        v
robot actions (gated) + operating records + audit ledger
```

No automated advice can dispatch a robot action the governor refuses, suppress
an operating record, or escalate a dispute to the chief without governor approval and
audit evidence.

## Capability layer

Resolves via [`kotoba-lang/occupation`](https://github.com/kotoba-lang/occupation)
(ISCO-08 `1113`). Required capabilities:

- :robotics
- :identity
- :forms
- :dmn
- :bpmn
- :audit-ledger

See [`docs/business-model.md`](docs/business-model.md) and
[`docs/operator-guide.md`](docs/operator-guide.md).

## Reference implementation (`:maturity :implemented`)

Full itonami Actor pattern (per ADR-2607011000 / CLAUDE.md's Actors
section): a real
[`kotoba-lang/langgraph`](https://github.com/kotoba-lang/langgraph)
`StateGraph`, with the Advisor and Governor as distinct graph nodes and
human-in-the-loop interrupt/resume via checkpointing.

```text
:intake -> :advise -> :govern -> :decide -+-> :commit            (:ok? true)
                                           +-> :request-approval   (:escalate? true, interrupt-before)
                                           +-> :hold               (:hard? true)
```

- `src/chiefs/store.cljc` — `Store` protocol + `MemStore`:
  registered communities, committed records, an append-only audit ledger.
- `src/chiefs/advisor.cljc` — `Advisor` protocol; `mock-advisor`
  (deterministic, default) proposes an administrative operation from a
  request; `llm-advisor` wraps a `langchain.model/ChatModel` — either
  way the advisor only ever produces a `:propose`-effect proposal,
  never a committed record, and LLM parse failures always yield
  `confidence 0.0` (forces escalation, never fabricated confidence).
- `src/chiefs/governor.cljc` — `ChiefsGovernor/check`: a pure
  function, wired as its own `:govern` node. Hard invariants
  (unregistered community, a proposal whose `:effect` isn't `:propose`,
  any proposal to issue a customary ruling or resolve a dispute directly)
  always route to `:hold`. Escalation invariants (`:flag-dispute`,
  or low advisor confidence) always route to
  `:request-approval` — an `interrupt-before` node that the graph
  checkpoints and only resumes on explicit human approval
  (`actor/approve!`), matching the README's scope statement that
  dispute escalation always requires human sign-off.
- `src/chiefs/actor.cljc` — `build-graph`, `run-request!`,
  `approve!`: the `langgraph.graph/state-graph` wiring itself.

```bash
clojure -M:test
```

This is what backs this repo's `:maturity :implemented` entry in
[`kotoba-lang/occupation`](https://github.com/kotoba-lang/occupation).

## License

AGPL-3.0-or-later.
