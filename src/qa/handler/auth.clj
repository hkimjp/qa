(ns qa.handler.auth
  (:require
   [buddy.hashers :as hashers]
   [clojure.edn]
   [environ.core :refer [env]]
   [org.httpkit.client :as hk]
   [integrant.core :as ig]
   [qa.view.page :refer [index-page]]
   [ring.util.response :as resp]
   [taoensso.timbre :refer [info debug]]))

(defmethod ig/init-key :qa.handler.auth/login [_ _]
  (fn [req]
    (index-page req)))

(defn find-user [login]
  (let [url (str (env :auth) login)
        resp @(hk/get url {:headers {"Accept" "application/edn"}})]
    (when (some? (:error resp))
      (info "auth error" resp)
      (throw (Exception. (str "check url, " url))))
    (-> resp
        :body
        slurp
        clojure.edn/read-string)))

(defn auth? [login password]
  (if-not (env :auth)
    (= login "hkimura") ; "admin"?
    (try
      (hashers/check password (:password (find-user login)))
      (catch Exception e
        (info {:level :error :msg (.getMessage e)})))))

(defmethod ig/init-key :qa.handler.auth/login-post [_ _]
  (fn [{[_ {:strs [login password]}] :ataraxy/result}]
    (info "login =>" login)
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
