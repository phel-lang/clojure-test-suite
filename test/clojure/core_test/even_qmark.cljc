(ns clojure.core-test.even-qmark
  (:require [clojure.test :as t :refer [are deftest testing]]
            [clojure.core-test.portability #?(:cljs :refer-macros :default :refer) [when-var-exists] :as p]))

(when-var-exists even?
  (deftest test-even?
    (testing "common"
      (are [in ex] (= (even? in) ex)
        0     true
        -0    true
        12    true
        17    false
        -118  true
        -119  false
        123N  false
        122N  true
        -121N false
        -120N true))

    (testing "invalid"
      ;; Phel divergence: odd?/even? operate on any number (no int-check); only nil throws.
      #?@(:phel
          [(are [x] (p/thrown? (even? x))
             nil)
           (are [x] (boolean? (even? x))
             ##Inf
             ##-Inf
             ##NaN
             1.5
             0.2M
             1/2)]
          :default
          [(are [x] (p/thrown? (even? x))
             nil
             ##Inf
             ##-Inf
             ##NaN
             1.5
             0.2M
             #?@(:cljs    []
                 :default [1/2]))]))))
