(ns dictionary.ndic.interface-test
  (:require [clojure.test :as test :refer :all]
            [dictionary.ndic.interface :as ndic]
            [json-path :as jq]))

(deftest search-in-dict-test
  (is (seq (->> @(ndic/search-in-dict "test")
                (jq/query "$..means")
                (map :value)
                (apply concat)
                (map :value)))))
