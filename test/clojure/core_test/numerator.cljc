(ns clojure.core-test.numerator
  (:require [clojure.test :as t :refer [deftest is]]
            [clojure.core-test.portability #?(:cljs :refer-macros :default :refer) [when-var-exists] :as p]))

(when-var-exists numerator
 (deftest test-numerator
   #?@(:cljs []
       :default
       [(is (= 1 (numerator 1/2)))
        (is (= 2 (numerator 2/3)))
        (is (= 3 (numerator 3/4)))])

   ;; Phel divergence: int/long/float/double throw on non-numeric (phel-lang #2224); no bigint-promote/overflow (Bucket B, #2223).
   ;; Phel treats integers as ratios with denominator 1, so numerator of an int is the int itself.
   #?@(:phel
       [(is (= 1 (numerator 1)))
        (is (= 1 (numerator 1N)))]
       :lpy
       [(is (= 1 (numerator 1)))
        (is (= 1 (numerator 1N)))]
       :default
       [(is (p/thrown? (numerator 1)))
        (is (p/thrown? (numerator 1N)))])
   (is (p/thrown? (numerator 1.0)))
   (is (p/thrown? (numerator 1.0M)))
   (is (p/thrown? (numerator ##Inf)))
   (is (p/thrown? (numerator ##NaN)))
   (is (p/thrown? (numerator nil)))))
