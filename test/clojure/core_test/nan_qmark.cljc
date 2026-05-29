(ns clojure.core-test.nan-qmark
  (:require [clojure.test :as t :refer [are deftest is]]
            [clojure.core-test.portability #?(:cljs :refer-macros :default :refer) [when-var-exists] :as p]))

(when-var-exists NaN?
 (deftest test-NaN?
   ;; Phel divergence: NaN? lenient on nil (returns false via is_nan coercion),
   ;; but still throws a TypeError on a string arg (Bucket A/B, #2223).
   #?@(:phel
       [(is (= false (NaN? nil)))
        (is (p/thrown? (NaN? "##NaN")))]
       :cljs
       [(is (not (NaN? nil)))
        (is (NaN? "##NaN"))]            ; Surprising
       :default
       [(is (p/thrown? (NaN? nil)))
        (is (p/thrown? (NaN? "##NaN")))])
   (is (double? ##NaN))
   ;; NaN is not equal to anything, even itself.
   ;; See: https://clojure.org/guides/equality
   ;; Note that we use `(not (= ...))` rather than `(not= ...)` because
   ;; of a bug in clojure.core.
   (is (not (= ##NaN ##NaN)))
   (are [in ex] (= ex (NaN? in))
     0             false
     1.0           false
     -1.0          false
     (double 1.0)  false
     (double -1.0) false
     ##Inf         false
     ##-Inf        false

     ##NaN         true)))
