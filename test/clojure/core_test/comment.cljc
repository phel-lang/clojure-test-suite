(ns clojure.core-test.comment
  (:require [clojure.test :as t :refer [are deftest is testing]]

            ;; NOTE: other dialects like Basilisp seem to be able to require
            ;; clojure.test but Phel needs to be explicit about requiring it's
            ;; own testing namespace at the moment.
            #?(:phel [phel.test :as t :refer [are deftest is testing]])

            [clojure.core-test.portability #?(:cljs :refer-macros :default :refer) [when-var-exists]]))

(when-var-exists comment
  (deftest test-comment
    (is (nil? (comment)))
    (is (nil? (comment 1)))
    (is (nil? (comment nil)))
    (is (nil? (comment (throw (ex-info "Bad things!!!" {:foo :bar})))))))
