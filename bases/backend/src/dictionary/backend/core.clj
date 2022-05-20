(ns dictionary.backend.core
  (:require
   [aleph.http :as http :refer [wrap-ring-async-handler]]
   [clojure.java.jdbc :as jdbc]
   [dictionary.ndic.interface :as ndic]
   [integrant.core :as ig]
   [manifold.deferred :as d]
   [ring.middleware.json :refer [wrap-json-response]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.util.response :refer [response]]))

(defmethod ig/init-key :database.sql/connection [_ {:keys [uri]}]
  (jdbc/get-connection {:connection-uri uri}))

(defn wrap-deferred-handler
  "Chains the Deferred response of handler to respond and raise callbacks."
  [handler]
  (fn [request respond raise]
    (-> (d/chain (handler request)
                 respond)
        (d/catch raise))))

(defmethod ig/init-key :backend/handler [_ {:keys [db]}]
  (fn [{:keys [params]}]
    (let [word (get params "word" "")]
      (d/chain
       (ndic/search-in-dict word)
       response))))

(defmethod ig/init-key :ring.adapter/aleph [_ {:keys [handler] :as options}]
  (http/start-server  (-> handler
                          wrap-deferred-handler
                          wrap-params
                          wrap-json-response
                          wrap-ring-async-handler) options))

(defmethod ig/halt-key! :ring.adapter/aleph
  [_ aleph]
  (when aleph
    (.close aleph)))
