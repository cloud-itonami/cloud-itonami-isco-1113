(ns chiefs.actor-test
  (:require [clojure.test :refer [deftest is testing]]
            [chiefs.actor :as actor]
            [chiefs.store :as store]))

(defn- fresh-store []
  (let [st (store/mem-store)]
    (store/register-community! st {:community-id "village-1" :name "Acme Village"})
    st))

(deftest commits-a-clean-low-risk-request
  (let [st (fresh-store)
        graph (actor/build-graph {:store st})
        request {:community-id "village-1" :op :draft-correspondence :stake :low}
        result (actor/run-request! graph request {} "thread-1")]
    (is (= :done (:status result)))
    (is (some? (get-in result [:state :record])))
    (is (= 1 (count (store/records-of st "village-1"))))))

(deftest holds-on-unregistered-community-without-committing
  (let [st (fresh-store)
        graph (actor/build-graph {:store st})
        request {:community-id "no-such-village" :op :draft-correspondence :stake :low}
        result (actor/run-request! graph request {} "thread-2")]
    (is (= :done (:status result)))
    (is (nil? (get-in result [:state :record])))
    (is (empty? (store/records-of st "no-such-village")))
    (is (= :hold (:disposition (:state result))))))

(deftest holds-on-scope-violation
  (let [st (fresh-store)
        graph (actor/build-graph {:store st})
        ;; attempt to issue a ruling (hard violation)
        request {:community-id "village-1" :op :issue-ruling :stake :high}
        result (actor/run-request! graph request {} "thread-3")]
    (is (= :done (:status result)))
    (is (nil? (get-in result [:state :record])))
    (is (empty? (store/records-of st "village-1")))
    (is (= :hold (:disposition (:state result))))))

(deftest interrupts-then-commits-on-human-approval
  (let [st (fresh-store)
        graph (actor/build-graph {:store st})
        ;; dispute flagging always escalates (governor invariant)
        request {:community-id "village-1" :op :flag-dispute :stake :high}
        interrupted (actor/run-request! graph request {} "thread-4")]
    (is (= :interrupted (:status interrupted)))
    (is (empty? (store/records-of st "village-1")))
    (let [resumed (actor/approve! graph "thread-4")]
      (is (= :done (:status resumed)))
      (is (some? (get-in resumed [:state :record])))
      (is (= 1 (count (store/records-of st "village-1")))))))
