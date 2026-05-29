(ns clojure.core-test.transient
  (:require [clojure.test :as t :refer [are deftest is testing]]
            [clojure.core-test.portability #?(:cljs :refer-macros :default :refer) [when-var-exists] :as p]))

(when-var-exists transient
  (deftest test-transient
    (testing "creation"
      (are [coll] (let [a-transient (transient coll)
                        persisted (persistent! a-transient)]
                    (and (= coll persisted)
                         (= (type coll) (type persisted))))
                  [1 2 3]
                  {:x 0 :y -1}
                  ;; Basilisp does not currently implement sorted collections.
                  #?@(:lpy [] :default [(array-map :a 1)])
                  (hash-map :b 2)
                  #{42 "life"}
                  (hash-set 43 "thing")))

    (testing "support read-only interface"
      (testing "for transient vector"
        (let [avec [1 2 3]]
          (is (= (nth avec 1) (nth (transient avec) 1)))
          (is (= (get avec 1) (get (transient avec) 1)))
          (is (= (contains? avec 1) (contains? (transient avec) 1)))
          #?@(:cljr [(is (p/thrown? ((transient avec) 1)))] ;; arity exception
              :default [(is (= (avec 1) ((transient avec) 1)))])
          (is (= (count avec) (count (transient avec))))))

      (testing "for transient map"
        (let [amap {:x 1 :y -1}]
          (is (= (get amap :x) (get (transient amap) :x)))
          (is (= (contains? amap :x) (contains? (transient amap) :x)))
          (is (= (:x amap) (:x (transient amap))))
          (is (= (amap :x) ((transient amap) :x)))
          (is (= (count amap) (count (transient amap))))))

      (testing "for transient set"
        (let [someset #{42 "life"}]
          (is (= (get someset 42) (get (transient someset) 42)))
          (is (= (contains? someset 42) (contains? (transient someset) 42)))
          (is (= (someset 42) ((transient someset) 42)))
          (is (= (count someset) (count (transient someset)))))))

    (testing "calling non-bang interface throws"
      ;; Phel divergence: transients are unguarded (no use-after-persistent! check;
      ;; non-bang ops permitted). Phel's non-bang ops return a transient value
      ;; instead of throwing, so assert non-nil under :phel.
      (testing "for transient vector"
        (let [avec [1 2 3]]
          #?(:phel (is (some? (assoc (transient avec) 0 5)))
             :default (is (p/thrown? (assoc (transient avec) 0 5))))
          #?(:phel (is (some? (conj (transient avec) 5)))
             :default (is (p/thrown? (conj (transient avec) 5))))
          (is (p/thrown? (pop (transient avec))))))

      (testing "for transient map"
        (let [amap {:x 1 :y -1}]
          #?(:phel (is (some? (assoc (transient amap) :x 5)))
             :default (is (p/thrown? (assoc (transient amap) :x 5))))
          #?(:phel (is (some? (dissoc (transient amap) :x)))
             :default (is (p/thrown? (dissoc (transient amap) :x))))
          #?(:phel (is (some? (conj (transient amap) [:x 5])))
             :default (is (p/thrown? (conj (transient amap) [:x 5]))))))

      (testing "for transient set"
        (let [someset #{42 "life"}]
          (is (p/thrown? (disj (transient someset) 42)))
          #?(:phel (is (some? (conj (transient someset) 43)))
             :default (is (p/thrown? (conj (transient someset) 43)))))))

    (testing "calling transient a second time throws"
      (are [a-transient] (p/thrown? (transient a-transient))
                         (transient [1 2 3])
                         (transient {:x 1 :y -1})
                         (transient #{42 "life"})))

    (testing "bad input"
      (are [v] (p/thrown? (transient v))
               'sym
               `sym
               "meow"
               1
               1N
               1.0
               10.0M
               #?@(:cljs [] ; most Clojure dialects support ratios - not CLJS
                   :default [111/7])
               \newline
               nil
               true
               false
               ##Inf
               :kw
               :ns/kw
               #(+ 1 %)
               '(1 2 3)
               ;; Basilisp does not currently implement sorted collections.
               ;; Phel divergence: transients are unguarded (no use-after-persistent! check;
               ;; non-bang ops permitted). (transient sorted-coll) returns a value instead
               ;; of throwing, so skip like :lpy.
               #?@(:lpy []
                   :phel []
                   :default [(sorted-set :i :j :k)
                             (sorted-map :hp 99)])
               #?@(:cljs [] ;; thrown? range error in clojurescript causes Javacript heap OOM
                   :default [(range)])))))
