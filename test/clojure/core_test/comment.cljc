(ns clojure.core-test.comment
  (:require [clojure.test :as t :refer [are deftest is testing]]
            [clojure.core-test.portability #?(:cljs :refer-macros :default :refer) [when-var-exists]]))

(when-var-exists comment
  (deftest test-comment
    (is (nil? (comment)))
    (is (nil? (comment 1)))
    (is (nil? (comment nil)))
    (is (nil? (comment (throw (ex-info "Bad things!!!" {:foo :bar})))))))


;; ./vendor/phel-lang/phel-lang/bin/phel test
;; [PHEL001] Cannot resolve symbol 'when-var-exists'
;; in /home/user/dev/phel/clojure-test-suite/test/clojure/core_test/comment.cljc:5
;;
;; 5| (when-var-exists comment
;;      ^^^^^^^^^^^^^^^
;; 6|   (deftest test-comment
;; 7|     (is (nil? (comment)))
;; 8|     (is (nil? (comment 1)))
;; 9|     (is (nil? (comment nil)))
;; 10|     (is (nil? (comment (throw (ex-info "Bad things!!!" {:foo :bar})))))))
