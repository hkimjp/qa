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

; (defmethod ig/init-key :qa.handler.core/readers [_ {:keys [db]}]
;   (fn [{[_ path n] :ataraxy/result}]
;     (readers-page (readers/fetch-readers db path n @since) @since)))

(defmethod ig/init-key :qa.handler.api/since [_ {:keys [db]}]
  (fn [{[_ date] :ataraxy/result}]
    (let [ret (->> (readers/fetch-readers db "qs" 0 date)
                   (map :login)
                   set
                   vec)]
      {:status 200
       :header {"content-type" "application/json"}
       :body {:since (str vec)}})))


