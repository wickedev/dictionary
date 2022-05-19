(ns dictionary.backend.core
  (:require [integrant.core :as ig]
            [ring.adapter.jetty :refer [run-jetty]]
            [clojure.java.jdbc :as jdbc]))

(defmethod ig/init-key :database.sql/connection [_ {:keys [uri]}]
  (jdbc/get-connection {:connection-uri uri}))

(defmethod ig/init-key :backend/handler [_ {:keys [db]}]
  (fn [_] {:status 200
           :headers {"Content-Type" "text/html"}
           :body "Hello World"}))

(defmethod ig/init-key :ring.adapter/jetty [_ {:keys [handler] :as options}]
  (run-jetty handler options))

(defmethod ig/halt-key! :ring.adapter/jetty
  [_ jetty]
  (when jetty
    (.stop jetty)))
