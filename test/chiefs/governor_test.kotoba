(ns chiefs.governor-test
  (:require [clojure.test :refer [deftest is testing]]
            [chiefs.store :as store]
            [chiefs.governor :as governor]))

(defn- fresh-store []
  (let [st (store/mem-store)]
    (store/register-community! st {:community-id "village-1" :name "Acme Village"})
    st))

(deftest ok-on-clean-draft-correspondence
  (let [st (fresh-store)
        proposal {:op :draft-correspondence :effect :propose :confidence 0.9 :stake :low}
        v (governor/check {:community-id "village-1"} {} proposal st)]
    (is (:ok? v))
    (is (not (:hard? v)))
    (is (not (:escalate? v)))))

(deftest ok-on-schedule-meeting
  (let [st (fresh-store)
        proposal {:op :schedule-community-meeting :effect :propose :confidence 0.9 :stake :low}
        v (governor/check {:community-id "village-1"} {} proposal st)]
    (is (:ok? v))
    (is (not (:hard? v)))
    (is (not (:escalate? v)))))

(deftest ok-on-log-record
  (let [st (fresh-store)
        proposal {:op :log-community-record :effect :propose :confidence 0.9 :stake :low}
        v (governor/check {:community-id "village-1"} {} proposal st)]
    (is (:ok? v))
    (is (not (:hard? v)))
    (is (not (:escalate? v)))))

(deftest hard-on-unregistered-community
  (let [st (fresh-store)
        proposal {:op :draft-correspondence :effect :propose :confidence 0.9 :stake :low}
        v (governor/check {:community-id "no-such-village"} {} proposal st)]
    (is (:hard? v))
    (is (some #(= :no-community (:rule %)) (:violations v)))))

(deftest hard-on-no-actuation-violation
  (let [st (fresh-store)
        proposal {:op :draft-correspondence :effect :direct-write :confidence 0.9 :stake :low}
        v (governor/check {:community-id "village-1"} {} proposal st)]
    (is (:hard? v))
    (is (some #(= :no-actuation (:rule %)) (:violations v)))))

(deftest hard-on-issue-ruling-attempt
  (let [st (fresh-store)
        proposal {:op :issue-ruling :effect :propose :confidence 0.9 :stake :high}
        v (governor/check {:community-id "village-1"} {} proposal st)]
    (is (:hard? v))
    (is (some #(= :scope-violation (:rule %)) (:violations v)))))

(deftest hard-on-allocate-resource-attempt
  (let [st (fresh-store)
        proposal {:op :allocate-resource :effect :propose :confidence 0.9 :stake :high}
        v (governor/check {:community-id "village-1"} {} proposal st)]
    (is (:hard? v))
    (is (some #(= :scope-violation (:rule %)) (:violations v)))))

(deftest hard-on-resolve-dispute-attempt
  (let [st (fresh-store)
        proposal {:op :resolve-dispute :effect :propose :confidence 0.9 :stake :high}
        v (governor/check {:community-id "village-1"} {} proposal st)]
    (is (:hard? v))
    (is (some #(= :scope-violation (:rule %)) (:violations v)))))

(deftest escalates-on-flag-dispute
  (let [st (fresh-store)
        proposal {:op :flag-dispute :effect :propose :confidence 0.9 :stake :high}
        v (governor/check {:community-id "village-1"} {} proposal st)]
    (is (:escalate? v))
    (is (not (:hard? v)))))

(deftest escalates-on-low-confidence
  (let [st (fresh-store)
        proposal {:op :draft-correspondence :effect :propose :confidence 0.2 :stake :low}
        v (governor/check {:community-id "village-1"} {} proposal st)]
    (is (:escalate? v))
    (is (not (:hard? v)))))

(deftest store-records-and-ledger-append-only
  (let [st (fresh-store)]
    (store/commit-record! st {:community-id "village-1" :op :draft-correspondence})
    (store/append-ledger! st {:disposition :commit})
    (is (= 1 (count (store/records-of st "village-1"))))
    (is (= 1 (count (store/ledger st))))))
