(ns dictionary.backend.core
  (:require [integrant.core :as ig]
            [ring.adapter.jetty :refer [run-jetty]]
            [clojure.java.jdbc :as jdbc]
            [dictionary.ndic.interface :as ndic]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :refer [response]]))

(defmethod ig/init-key :database.sql/connection [_ {:keys [uri]}]
  (jdbc/get-connection {:connection-uri uri}))

(defmethod ig/init-key :backend/handler [_ {:keys [db]}]
  (fn [{:keys [params]}]
    (let [word (get params "word" "")]
      (response (ndic/search-in-dict word)))))

(defmethod ig/init-key :ring.adapter/jetty [_ {:keys [handler] :as options}]
  (run-jetty (-> handler
                 wrap-params
                 wrap-json-response) options))

(defmethod ig/halt-key! :ring.adapter/jetty
  [_ jetty]
  (when jetty
    (.stop jetty)))
