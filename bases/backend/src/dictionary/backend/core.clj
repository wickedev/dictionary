(ns dictionary.backend.core
  (:require [ring.adapter.jetty :refer [run-jetty]]))

(defn handler [_]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello World"})

(defn -main []
  (run-jetty handler {:port 3000
                      :join? false}))

(comment
  (-main))