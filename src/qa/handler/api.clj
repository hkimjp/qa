(ns qa.handler.api
  (:require
   [integrant.core :as ig]
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

