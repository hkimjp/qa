(ns qa.handler.auth
  (:require
   [buddy.hashers :as hashers]
   ; [clojure.string :as str]
   [environ.core :refer [env]]
   [hato.client :as hc]
   [integrant.core :as ig]
   [qa.view.page :refer [index-page]]
   [ring.util.response :as resp]
   [taoensso.timbre :refer [info debug] :as timbre]))

;; see definition of `auth?`
;; (def l22 "https://l22.melt.kyutech.ac.jp")

(comment
  (env :qa-dev)
  (:body (hc/get "https://l22.melt.kyutech.ac.jp/api/user/hkimura"))
  (:body (hc/get "https://l22.melt.kyutech.ac.jp/api/user/hkimura" {:as :json}))
  :rcf)

(defmethod ig/init-key :qa.handler.auth/login [_ _]
  (fn [req]
    (index-page req)))

(defn auth? [login password]
  (debug "auth?" login password)
  (if (env :qa-dev)
    (and (= login "hkimura") true) ; any password
    (let [ep (str "https://l22.melt.kyutech.ac.jp/api/user/" login)
          user (:body (hc/get ep {:as :json}))];;
      (info "user=>" user "password")
      (and (some? user) (hashers/check password (:password user))))))

(defmethod ig/init-key :qa.handler.auth/login-post [_ _]
  (fn [{[_ {:strs [login password]}] :ataraxy/result}]
    (info "login password=>" login password)
    (if (and (seq login) (auth? login password))
      (let [ret (-> (resp/redirect "/qs")
                    (assoc-in [:session :identity] login))]; was (keyword login)
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
