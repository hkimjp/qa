#!/usr/bin/env bb

(require '[babashka.deps :as deps])
(deps/add-deps '{:deps {org.babashka/http-client {:mvn/version "0.4.23"}}})
(require '[babashka.http-client :as http])

(defn qa []
  (let [uri "https://qa.melt.kyutech.ac.jp/api/readers"
        date (if (seq *command-line-args*)
               (first *command-line-args*)
               (-> (java.time.LocalDate/now) str))
        ret (-> (http/get (str uri "/" date))
                :body
                edn/read-string)]
    (assoc ret :count (count (:readers ret)))))

(println (str (qa)))

