(ns clojure.core-test.peek
  (:require [clojure.test :refer [are deftest is testing]]
            [clojure.core-test.portability #?(:cljs :refer-macros :default :refer) [when-var-exists] :as p]))

(when-var-exists peek
  (deftest test-peek

    (testing "list"
      (is (nil? (peek '())))
      (is (= :a (peek '(:a :b :c)))))

    (testing "vector"
      (is (nil? (peek [])))
      (is (= :c (peek [:a :b :c]))))

    (testing "nil"
      (is (nil? (peek nil))))

    (testing "bad shape"
      ;; Phel divergence: nil/mixed-type comparison returns a bool; compare on collections returns 0; peek is structural.
      ;; In Phel, peek is structural: it returns the last char of a string, nil for
      ;; an empty-ordered map, and the first element of a seq/list. Only sets and
      ;; plain numbers throw.
      (are [coll] (p/thrown? (peek coll))
                  #{1 2 3}
                  42)
      #?@(:phel
          [(is (= nil (peek {:a 1 :b 2})))
           (is (= 1 (peek (cons 1 '()))))
           (is (= 0 (peek (range 10))))
           (is (= "r" (peek "str")))]
          :default
          [(is (p/thrown? (peek {:a 1 :b 2})))
           (is (p/thrown? (peek (cons 1 '()))))
           (is (p/thrown? (peek (range 10))))
           (is (p/thrown? (peek "str")))]))))
