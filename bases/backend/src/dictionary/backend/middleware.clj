(ns dictionary.backend.middleware
  (:require    [manifold.deferred :as d]))

(defn wrap-deferred-handler
  "Chains the Deferred response of handler to respond and raise callbacks."
  [handler]
  (fn [request respond raise]
    (-> (d/chain (handler request)
                 respond)
        (d/catch raise))))