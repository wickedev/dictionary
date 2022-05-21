(ns dictionary.backend.core
  (:require [aleph.http :as http]
            [clojure.data.json :as json]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [com.walmartlabs.lacinia :refer [execute]]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
            [dictionary.ndic.interface :as ndic]
            [integrant.core :as ig]
            [manifold.deferred :as d]
            [muuntaja.core :as m]
            [reitit.coercion.spec]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [ring.util.response :refer [response]]))

(defn resolver-map
  []
  {:query/game-by-id
   (fn [context args value]
     {:id "1"
      :name "name"
      :summary "summary"
      :description "description"
      :min_players 1
      :max_players 1
      :play_time 1})})

(def dict-schema (-> "backend/schema.edn"
                     io/resource
                     slurp
                     edn/read-string
                     #_parser/parse-schema
                     (attach-resolvers (resolver-map))
                     schema/compile))



(defmethod ig/init-key :database.sql/connection [_ {:keys [uri]}]
  (jdbc/get-connection {:connection-uri uri}))

(defmethod ig/init-key :backend/handler [_ {:keys [db]}]
  (fn [{:keys [params]}]
    (let [word (get params "word" "")]
      (d/chain
       (ndic/search-in-dict word)
       response))))

(defn- variables
  [req]
  (get-in req [:body-params :variables]))

(defn- build-plain-query-variables [request]
  {:query     (get-in request [:body-params :query])
   :variables (variables request)})

(defn- graphql-handler [request]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (let [{:keys [query variables]} (build-plain-query-variables request)

               result (execute dict-schema query variables nil)]
           (json/write-str result))})

(defn- graphql-route []
  ["/graphql" {:post {:summary    "graphql handler"
                      :responses  {200 {:body any?}}
                      :handler    graphql-handler}}])

(defn- default-route []
  ["/" {:get {:no-doc    true
              :summary   "health check"
              :responses {200 {:body {:message string?}}}
              :handler   (fn [_] (response {:message "Hello Dictionary"}))}}])


(defmethod ig/init-key :backend/app app [_ _]
  (ring/ring-handler
   (ring/router
    [(graphql-route)
     (default-route)]
    {:data {:coercion   reitit.coercion.spec/coercion
            :muuntaja   m/instance
            :middleware [parameters/parameters-middleware
                         muuntaja/format-middleware
                         muuntaja/format-negotiate-middleware
                         muuntaja/format-request-middleware
                         muuntaja/format-response-middleware
                         coercion/coerce-request-middleware
                         coercion/coerce-response-middleware]}})))

(defmethod ig/init-key :ring.adapter/aleph [_ {:keys [app] :as options}]
  (http/start-server app options))

(defmethod ig/halt-key! :ring.adapter/aleph
  [_ aleph]
  (when aleph
    (.close aleph)))
