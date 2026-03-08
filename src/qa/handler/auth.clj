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

(comment
  (let [url "http://eq.local:3022/api/user/hkimura"]
    [(-> (hc/get url {:as :json})
         :body)
     (-> @(hk/get url {:headers {"Accept" "application/edn"}})
         :body
         slurp
         clojure.edn/read-string)])
  :rcf)

(defmethod ig/init-key :qa.handler.auth/login [_ _]
  (fn [req]
    (index-page req)))

(comment
  (if-not (env :auth)
    "no auth"
    "auth")
  :rcf)

(defn auth? [login password]
  (if-not (env :auth)
    (= login "hkimura") ; any password
    (try
      (let [url (str (env :auth) login) ; bug! (env :auth) is empty.
            _  (info {:level :info :id "auth?" :msg url})
          ;;
            pw (-> (hk/get url {:headers {"Accept" "application/edn"}})
                   deref
                   :body
                   slurp
                   clojure.edn/read-string
                   :password)
          ;;
            ]
        (debug "pw:" pw)
        (hashers/check password pw))
      (catch Exception e
        (info {:level :error :msg (.getMessage e)})))))

(comment
  (-> "http://eq.local:3022/api/user/hkimura1"
      (hk/get {:headers {"Accept" "application/edn"}})
      deref
      :body
      slurp
      clojure.edn/read-string
      :password)
  (hashers/check "abc" nil)
  :rcf)

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
