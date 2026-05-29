(ns clojure.core-test.contains-qmark
  (:require [clojure.test :as t :refer [deftest is testing]]
            [clojure.core-test.portability #?(:cljs :refer-macros :default :refer) [when-var-exists] :as p]))

(when-var-exists contains?
  (deftest test-contains?
    (is (= false (contains? nil nil)))
    (is (= false (contains? {} nil)))
    (is (= false (contains? [] nil)))
    ;; Phel divergence: contains? on a string treats the 2nd arg as an index key, so a
    ;; non-integer like "a" is simply absent — returns false instead of throwing.
    #?(:phel (is (= false (contains? "abc" "a")))
       :lpy (is (= true (contains? "abc" "a")))
       :cljs (is (= false (contains? "abc" "a")))
       :default (is (p/thrown? (contains? "abc" "a"))))

    ;; find by index
    (is (= true (contains? ["a" "b" "c"] 0)))
    (is (= false (contains? ["a" "b" "c"] 3)))
    (is (= true (contains? "abc" 0)))
    (is (= true (contains? "abc" 2)))
    (is (= false (contains? "abc" 3)))

    ;; find by key
    (is (= true (contains? {:a 1 :b 1} :a)))
    (is (= false (contains? {:a 1 :b 1} :c)))
    (is (= true (contains? {:a 1 :b (range)} :a)))))
