(ns dictionary.ndic.interface
  (:require [manifold.deferred :as d]
            [aleph.http :as http]
            [byte-streams :as bs]
            [clojure.data.json :as json]
            [clojure.walk :as walk]
            [clojure.string :as string]))

(def naver-dict-base-uri "https://en.dict.naver.com/api3/enko/search")

(defn params->query-string [m]
  (string/join "&" (for [[k v] m] (str (name k) "=" v))))

(defn search-in-dict [word]
  (let [query (params->query-string
               {:query word
                :m "mobile"
                :shouldSearchVlive false
                :lang "ko"})]
    (d/chain (http/get (str naver-dict-base-uri "?" query))
             :body
             bs/to-string
             json/read-str
             walk/keywordize-keys)))