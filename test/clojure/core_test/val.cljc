(ns clojure.core-test.val
  (:require [clojure.test :refer [are deftest is testing]]
            [clojure.core-test.portability #?(:cljs :refer-macros :default :refer) [when-var-exists] :as p]))

(when-var-exists val
  (deftest test-val
    (testing "basic tests"
      (is (nil? (val (first {nil nil}))))
      (is (= :v (val (first {:k :v}))))
      (is (= :v (val (first (hash-map :k :v)))))
      (when-var-exists sorted-map
        (is (= :v (val (first (sorted-map :k :v))))))
      (when-var-exists array-map
        (is (= :v (val (first (array-map :k :v)))))))
    ;; Phel divergence: key/val nil-safe + structural; realized? true for non-pending; min-key works on any Comparable.
    (testing "`val` throws on lots of things"
      #?(:phel
         (do
           ;; Phel is lenient: val returns nil for empty/scalar/map inputs,
           ;; structurally returns the second element of non-empty seqs/vectors.
           ;; Scalars like 0 still throw (cannot iterate).
           (is (p/thrown? (val 0)))
           (are [arg] (nil? (val arg))
             nil
             '()
             {}
             {1 2}
             []
             #{})
           (is (= 2 (val '(1 2))))
           (is (= 2 (val [1 2])))
           (is (= 2 (val #{1 2}))))
         :default
         (are [arg] (p/thrown? (val arg))
           nil
           0
           '()
           '(1 2)
           {}
           {1 2}
           []
           [1 2]                           ; might be dialect-specific
           #{}
           #{1 2})))))
