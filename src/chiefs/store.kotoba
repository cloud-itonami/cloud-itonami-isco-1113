(ns chiefs.store
  "SSoT for the ISCO-08 1113 traditional chief/village-head actor. Store is a
  protocol injected into the `chiefs.actor` StateGraph — `MemStore` is the
  default, deterministic, zero-dep backend; a Datomic/kotoba-server-backed
  implementation can be swapped in without touching the actor or governor
  (itonami actor pattern, per ADR-2607011000 / CLAUDE.md Actors section).

  Domain:

    community  — a registered community (:community-id, :name)
    record     — a committed administrative record under a community
                 (correspondence draft, meeting schedule, log entry) — written
                 ONLY via commit-record!, never mutated in place
    ledger     — an append-only audit trail of every proposal/verdict/
                 disposition, regardless of outcome (commit or hold)")

(defprotocol Store
  (community [s community-id])
  (records-of [s community-id])
  (ledger [s])
  (register-community! [s community])
  (commit-record! [s record])
  (append-ledger! [s fact]))

(defrecord MemStore [a]
  Store
  (community [_ community-id] (get-in @a [:communities community-id]))
  (records-of [_ community-id] (filter #(= community-id (:community-id %)) (:records @a)))
  (ledger [_] (:ledger @a))
  (register-community! [s community]
    (swap! a assoc-in [:communities (:community-id community)] community) s)
  (commit-record! [s record]
    (swap! a update :records (fnil conj []) record) s)
  (append-ledger! [s fact]
    (swap! a update :ledger (fnil conj []) fact) s))

(defn mem-store
  ([] (mem-store {}))
  ([seed] (->MemStore (atom (merge {:communities {} :records [] :ledger []} seed)))))
