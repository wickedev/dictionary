(ns user
  (:require [integrant.core :as ig]
            [integrant.repl :as ig-repl]
            [dictionary.backend.core :as backednd]))

(ig-repl/set-prep!
 (fn [] (-> "development/resources/config.edn" slurp ig/read-string)))

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)

(comment
  (go)
  (halt)
  (reset)
  (reset-all))
