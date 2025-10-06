#!/usr/bin/env bb
(require '[babashka.deps :as deps])
(deps/add-deps '{:deps {org.babashka/http-client {:mvn/version "0.4.23"}}})
(require '[babashka.http-client :as http])

(defn date []
  (if (seq *command-line-args*)
    (first *command-line-args*)
    (-> (java.time.LocalDate/now) str)))

(-> (http/get (str "https://qa.melt.kyutech.ac.jp/api/readers/" (date)))
    :body
    println)
