(ns chiefs.governor
  "ChiefsGovernor — the independent safety/traceability layer for the ISCO-08
  1113 traditional chief/village-head actor. Wired as its own `:govern` node in
  `chiefs.actor`'s StateGraph, downstream of `:advise` — the Advisor has no
  notion of community provenance or dispute escalation risk, so this MUST be a
  separate system able to reject a proposal (itonami actor pattern, per
  ADR-2607011000 / CLAUDE.md Actors section).

  CRITICAL SCOPE ENFORCEMENT: This governor enforces the binding scope
  constraint that this actor NEVER issues customary rulings, land-allocation
  decisions, or dispute resolutions — those remain the human chief's/village
  head's exclusive traditional and legal authority. Any proposal that attempts
  these actions is PERMANENTLY BLOCKED (:hard? true, no override).

  `check` is a pure function of (request, context, proposal, store) ->
  verdict; it never mutates the store. The StateGraph's `:decide` node
  routes on the verdict:
    :hard? true                → :hold  (irreversible, no write)
    :escalate? true            → :request-approval (interrupt-before)
    otherwise                  → :commit

  HARD invariants (:hard? true, ALWAYS :hold, never overridable):
    1. community provenance  — the request's community must be registered.
    2. no-actuation         — proposal :effect must be :propose.
    3. no-customary-ruling  — proposal NEVER attempts :issue-ruling.
    4. no-land-allocation   — proposal NEVER attempts :allocate-resource.
    5. no-dispute-resolution — proposal NEVER attempts :resolve-dispute.
  ESCALATION invariants (:escalate? true, ALWAYS human sign-off, per the
  README scope statement: dispute escalation always requires human sign-off):
    6. :op :flag-dispute.
    7. low confidence (< `confidence-floor`)."
  (:require [chiefs.store :as store]))

(def confidence-floor 0.6)
(def ^:private escalating-ops #{:flag-dispute})
(def ^:private forbidden-ops #{:issue-ruling :allocate-resource :resolve-dispute})

(defn- hard-violations [{:keys [proposal]} community-record]
  (cond-> []
    (nil? community-record)
    (conj {:rule :no-community :detail "unregistered community"})

    (not= :propose (:effect proposal))
    (conj {:rule :no-actuation :detail "effect must be :propose only (no direct actuation)"})

    (contains? forbidden-ops (:op proposal))
    (conj {:rule :scope-violation
           :detail (str "FORBIDDEN: " (name (:op proposal))
                       " is the chief's exclusive authority; this actor provides"
                       " administrative support only, never customary ruling,"
                       " land allocation, or dispute resolution")})))

(defn check
  "Assess a proposal against `request`/`context`/`proposal` and a
  `store` implementing `chiefs.store/Store`. Returns
  `{:ok? bool :violations [...] :confidence n :hard? bool :escalate? bool}`."
  [request context proposal store]
  (let [community-record (store/community store (:community-id request))
        hard (hard-violations {:proposal proposal} community-record)
        hard? (boolean (seq hard))
        conf (or (:confidence proposal) 0.0)
        low? (< conf confidence-floor)
        risky-op? (contains? escalating-ops (:op proposal))]
    {:ok? (and (not hard?) (not low?) (not risky-op?))
     :violations hard
     :confidence conf
     :hard? hard?
     :escalate? (and (not hard?) (or low? risky-op?))}))
