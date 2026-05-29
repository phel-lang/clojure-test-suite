(ns clojure.core-test.intern
  (:require [clojure.test :as t :refer [deftest is]]
            [clojure.core-test.portability #?(:cljs :refer-macros :default :refer) [when-var-exists] :as p]))

(when-var-exists intern
 (deftest test-intern
   ;; Intern and bind
   (let [x-var (intern 'clojure.core-test.intern 'x 42)]
     (is (= 42 (var-get x-var))))

   ;; Use intern to return the previously interned var
   (let [x-var (intern 'clojure.core-test.intern 'x)]
     (is (= 42 (var-get x-var))))

   ;; Create new namespace and use that as argument to intern
   (let [n (create-ns 'avoid-a-clash)
         x-var (intern n 'x 42)]
     (is (= 42 (var-get x-var))))

   (let [x-var (intern 'avoid-a-clash 'x)]
     (is (= 42 (var-get x-var))))

   ;; Trying to intern to an unknown namespace should throw
   ;; Phel divergence: intern is lenient on an unknown namespace; instead of
   ;; throwing it returns the fully qualified symbol (ns/name) for the binding.
   #?(:phel
      (do
        (is (= 'unknown-namespace/x (intern 'unknown-namespace 'x)))
        (is (= 'unknown-namespace/x (intern 'unknown-namespace 'x 42))))
      :default
      (do
        (is (p/thrown? (intern 'unknown-namespace 'x)))
        (is (p/thrown? (intern 'unknown-namespace 'x 42)))))))
