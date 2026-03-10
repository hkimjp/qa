(ns qa.handler.auth
  (:require
   [buddy.hashers :as hashers]
   [clojure.edn]
   ; [clojure.string :as str]
   [environ.core :refer [env]]
   ; [hato.client :as hc]
   [org.httpkit.client :as hk]
   [integrant.core :as ig]
   [qa.view.page :refer [index-page]]
   [ring.util.response :as resp]
   [taoensso.timbre :refer [info debug] :as timbre]))

(defmethod ig/init-key :qa.handler.auth/login [_ _]
  (fn [req]
    (index-page req)))

(defn find-user [login]
  (-> (str (env :auth) login)
      (hk/get {:headers {"Accept" "application/edn"}})
      deref
      :body
      slurp
      clojure.edn/read-string))

(defn auth? [login password]
  (if-not (env :auth)
    (= login "hkimura") ; "admin"?
    (try
      (hashers/check password (:password (find-user login)))
      (catch Exception e
        (info {:level :error :msg (.getMessage e)})))))

(defmethod ig/init-key :qa.handler.auth/login-post [_ _]
  (fn [{[_ {:strs [login password]}] :ataraxy/result}]
    (info "login password=>" login password)
    (if (and (seq login) (auth? login password))
      (let [ret (-> (resp/redirect "/qs")
                    (assoc-in [:session :identity] login))]
        (info "login success" login)
        (debug "ret" ret)
        ret)
      (do
        (info "login failure")
        (-> (resp/redirect "/")
            (assoc :flash "login failure"))))))

(defmethod ig/init-key :qa.handler.auth/logout [_ _]
  (fn [_]
    (-> (resp/redirect "/")
        (assoc :session {}))))
