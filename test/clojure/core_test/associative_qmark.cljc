(ns clojure.core-test.associative-qmark
  (:require [clojure.test :as t :refer [are deftest]]
            [clojure.core-test.portability #?(:cljs :refer-macros :default :refer)  [when-var-exists]]))

(when-var-exists clojure.core/associative?
  (deftest test-associative?
    (are [expected x] (= expected (associative? x))
      ;; vectors and maps are associative
      true []
      true [1 2 3]
      true {}
      true {:a 1 :b 2}

      ;; list, set, string, array, keywords, symbols, numbers, characters
      false nil
      false '()
      false (range 10)                  ; lazy seq
      false (range)                     ; infinite lazy seq
      false #{}
      false #{:a :b}
      false "ab"

      ;; https://github.com/phel-lang/phel-lang/issues/1739#issuecomment-4338423558
      #?@(:phel [true (seq "ab")
                 true (to-array [1 2 3])]  ; PHP array is associative
          :default
          [false (seq "ab")                  ; seq
           false (to-array [1 2 3])])
      false :a
      false 'a
      false 1
      false \a)))
