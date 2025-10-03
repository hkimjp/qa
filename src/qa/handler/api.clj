(ns qa.handler.api
  (:require
   [ataraxy.response :as response]
   ; [clojure.edn :as edn]
   ; [clojure.java.io :as io]
   [integrant.core :as ig]
   ; [java-time.api :as jt]
   ; [next.jdbc.result-set :as rs]
   ; [qa.boundary.answers :as answers]
   ; [qa.boundary.goods :as goods]
   ; [qa.boundary.questions :as questions]
   [qa.boundary.readers :as readers]
   [taoensso.timbre :refer [info]]))

(defmethod ig/init-key :qa.handler.api/readers [_ {:keys [db]}]
  (fn [{[_ date] :ataraxy/result}]
    (let [ret (->> (readers/fetch-readers-on-date db date)
                   (mapv :login))]
      (info (str "ret: " ret))
      {:status 200
       :header {"content-type" "application/json"}
       :body (str {:date date
                   :readers ret})})))

