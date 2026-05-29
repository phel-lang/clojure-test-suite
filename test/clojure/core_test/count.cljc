(ns clojure.core-test.count
  (:require [clojure.test :as t :refer [are deftest]]
            [clojure.core-test.portability #?(:cljs :refer-macros :default :refer) [when-var-exists] :as p]))

(when-var-exists count
  (deftest test-count
    (are [expected x] (= expected (count x))
      0 nil
      0 '()
      0 []
      0 {}
      0 #{}
      0 ""
      1 '(:a)
      1 [:a]
      1 {:a 1}
      1 #{:a}
      1 "a"
      2 '(:a :b)
      2 [:a :b]
      2 {:a 1 :b 2}
      2 #{:a :b}
      2 "ab")

    ;; Negative tests
    ;; Phel divergence: a char literal \a is a 1-char string, so (count \a) returns 1
    ;; (structural) rather than throwing — exclude it from the throwing cases.
    (are [x] (p/thrown? (count x))
      1
      :a
      'a
      #?@(:phel [] :lpy [] :cljs [] :default [\a]))))
