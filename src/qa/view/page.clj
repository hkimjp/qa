(ns qa.view.page
  (:require
   [ataraxy.response :as response]
   [clojure.string :as str]
   [hiccup2.core :as h]
   [nextjournal.markdown :as md]
   [ring.util.anti-forgery :refer [anti-forgery-field]]))

(def ^:private version "3.1.2")
(def ^:private updated "2026-03-08 16:40:32")

(def ^:private wrap-at 80)

(defn- wrap-aux
  [n s]
  (if (< (count s) n)
    s
    (str (subs s 0 n) "\n" (wrap-aux n (subs s n)))))

(defn- wrap
  "fold string `s` at column `n`"
  [n s]
  (str/join "\n" (map (partial wrap-aux n) (str/split-lines s))))

(defn- ss
  "文字列 s の n 文字以降を切り詰めた文字列を返す。
   文字列長さが n に満たない時はそのまま。"
  [n s]
  (subs s 0 (min n (count s))))

(defn- date-time
  "timestamp 文字列から YYYY/MM/DD hh:mm:ss を抜き出す"
  [tm]
  (subs (str tm) 0 19))

;; markdown-clj to nextjournal.markdown
(defn md-to-html-string [s]
  (-> s md/parse md/->hiccup))

(defn page [& contents]
  [::response/ok
   (str (h/html
         [:head
          [:meta {:charset "utf-8"}]
          [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
          [:link
           {:href "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
            :rel  "stylesheet"
            :integrity "sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
            :crossorigin "anonymous"}]
          [:link
           {:rel "stylesheet"
            :type "text/css"
            :href "/css/styles.css"}]
          [:script {:type "text/javascript"}
           "function ok() {return window.confirm('OK?');}"]
          [:title "QA"]]
         [:body
          [:div {:class "container"}
           contents
           [:p]
           [:hr]
           "programmed by hkimura. " version]]))])

(defn about-page
  []
  (page
   [:h2 "QA:About"]
   [:p]
   [:img {:src "/images/odyssey.jpg"}]
   [:p "version: " version [:br]
    "update: " updated]))

(defn index-page [req]
  (page
   [:h2 "QA"]
   [:div.text-danger (:flash req)]
   [:form {:method :post :action "/login"}
    (h/raw (anti-forgery-field))
    [:input {:name "login" :placeholder "アカウント" :autocomplete "username"}]
    [:input {:name "password" :type "password" :placeholder "パスワード"
             :autocomplete "current-password"}]
    [:button "login"]]
   [:br]
   [:div {:class "row"}
    [:div {:class "col-3"}
     [:a {:href "https://www.youtube.com/watch?v=JktXHKx3r20"}
      [:img {:src "images/odyssey.jpg" :id "odyssey"}]] [:br]
     [:p {:class "sm"} "2001年宇宙の旅"]]
    [:div {:class "col-9"}
     [:p "聞いたことは忘れる。" [:br]
      "やったことは覚える。" [:br]
      "人に教えたことは身に付く。"]]]
   [:audio {:src "sounds/sorry-dave.mp3"
            :autoplay false
            :controls "controls"}]
   [:div
    [:ul
     [:li "回答しやすい質問をする。回答できる質問には回答する。"]
     [:li "質問はテキスト、回答は Markdown で。"]
     [:li "「👍」は一回答にひとり一回だけです。"]
     [:li "ログイン時の Invalid anti-forgery token は認証切れ。再読み込みで。"]]]))

(defn question-new-page []
  (page
   [:h2 "QA: Create a Question"]
   [:p "具体的な質問じゃないと回答つけにくい。"
    "短すぎる質問も長すぎる質問と同じく受信しない。"]
   [:form {:method :post
           :action "/q"
           :onsubmit "return confirm('その質問は具体的？')"}
    (h/raw (anti-forgery-field))
    [:textarea {:id "question"
                :name "question"
                :placeholder "マークダウン不可。1 行 60 文字以内に改行するように。"}]
    [:br]
    [:button.btn.btn-primary.btn-sm "submit"]]))

;; 回答がついてなかったら 0 を表示する。
(defn- answer-count
  [cs q_id]
  (:count (first (filter #(= (:q_id %) q_id) cs)) 0))

(defn- abbrev
  ([s] (abbrev 30 s))
  ([n s] (if (empty? s)
           s
           (apply str (take n s)))))

(defn questions-page [qs cs]
  (page
   [:h2 "QA: Questions"]
   [:p "すべての QA に目を通すのがルール。"]
   [:p
    [:a {:href "/recents" :class "btn btn-primary btn-sm"} "最近の回答"]
    " "
    [:a {:href "/goods" :class "btn btn-primary btn-sm"} "最近の👍"]
    " "
    [:a {:href "/q" :class "btn btn-primary btn-sm"} "new question"]
    " "
    [:a {:href "/about" :class "btn btn-primary btn-sm"} "About"]
    " "
    [:a {:href "/logout" :class "btn btn-warning btn-sm"} "logout"]]
   [:p [:a {:href "/readers/qs/0"} "readers"]]
   ;;id, login 👉 goods, first line (link to answers)
   (for [q qs]
     [:p
      (:id q)
      ", "
      [:a.link-underline-light {:href (str "/my-goods/" (:nick q))} (:nick q)]
      " "
      (str " 👉 " (answer-count cs (:id q)))
      ", "
      [:a {:href (str "/as/" (:id q))} (->> (:q q) (abbrev 30))]])
   [:p [:a {:href "/q" :class "btn btn-primary btn-sm"} "new question"]]))

;;👁️🚀✔️☑️➰➿⚯☞⍇⍈

(defn- goods
  [n]
  (repeat n "👍"))

(defn- my-escape-html [s]
  (-> (str/replace s #"<br>" "")
      h/raw))

(defn answers-page [q answers nick]
  (page
   [:h2 "QA: Answers"]
   [:div [:a {:href "/qs" :class "btn btn-success btn-sm"} "QA Top"]]
   [:h4 (:id q) ", " (:nick q) "さんの質問 " (date-time (:ts q)) ","]
   [:pre {:class "question"} (my-escape-html (wrap wrap-at (:q q)))]
   [:p [:a {:href (str "/readers/as/" (:id q))} "readers"]]
   [:hr]
   [:h4 "Answers"]
   (for [a answers]
     (let [goods (goods (:g a))]
       [:div
        [:p [:span {:class "nick"} (:nick a)] "'s answer " (date-time (:ts a)) ","]
        (md-to-html-string (:a a))
        [:p [:a.link-underline-light
             {:href (str "/good/" (:id q) "/" (:id a))}
             goods]
         (when (= nick "hkimura")
           [:a.link-underline-light
            {:href (str "/who-goods/" (:id a))}
            "   "])]]))
   [:p
    [:form {:method :post :action "/markdown-preview"}
     (h/raw (anti-forgery-field))
     [:input {:type "hidden" :name "q_id" :value (:id q)}]
     [:textarea {:id "answer" :name "answer"
                 :placeholder "your answer please. markdown OK"}]
     [:br]
     " "
     [:button.btn.btn-primary.btn-sm "preview"]
     [:p "自分のマークダウンを preview で確認して投稿する"]]]
   [:p]
   [:p [:a {:href "/qs" :class "btn btn-success btn-sm"} "QA Top"]]))

(defn admin-page []
  (page
   [:h2 "QA Admin"]
   [:p "who goods?"]
   [:form {:method :post :action "/admin/goods"}
    (h/raw (anti-forgery-field))
    "good " [:input {:id "n" :size 3 :name "n"}]
    " "
    [:button.btn.btn-primary.btn.sm "submit"]]))

(defn goods-page [goods]
  (page
   [:h2 "QA: goods"]
   [:table
    (for [g goods]
      [:tr
       [:td (:nick g)]
       [:td (date-time (:ts g))]])]))

(defn recents-page [answers]
  (page
   [:h2 "QA: recent answers"]
   [:p [:a {:href "/qs" :class "btn btn-success btn-sm"} "QA Top"]]
   (for [a answers]
     [:p
      (:q_id a)
      ", "
      (date-time (:ts a))
      " "
      [:a
       {:href (str "/as/" (:q_id a))}
       (h/raw (ss 28 (:a a)))]
      "... by " (:nick a)])
   [:p [:a {:href "/qs" :class "btn btn-success btn-sm"} "QA Top"]]))

(defn recent-goods-page [answers]
  (page
   [:h2 "QA: recent goods"]
   [:p [:a {:href "/qs" :class "btn btn-success btn-sm"} "QA Top"]]
   (for [a answers]
     [:p
      (:q_id a)
      ", "
      (date-time (:ts a))
      " "
      [:a
       {:href  (str "/as/" (:q_id a))}
       (ss 28 (:q a)) "..."]])))

(defn readers-page [readers since]
  (let [uniq-readers (->> (map :login readers)
                          distinct
                          #_(mapv (fn [user]
                                    [:a {:href (str "/my-goods/" user)} user])))]
    (page
     [:h2 "QA: Who read since " since]
     [:p "ほんと、みんな、QA 読まないんだな。点数稼ぎの 👍 は心が冷えるよ。"]
     [:p #_(->> uniq-readers
                (interpose " ")
                (apply str))
      (for [user uniq-readers]
        [:span [:a {:href (str "/my-goods/" user)} user] " "])
      "(合計 " (count readers) "回、" (count uniq-readers) "人)"])))

(defn points-page [name sid ret]
  (page
   [:h2 "Points " name " " sid]
   (for [item ret]
     [:p (str item)])))

(defn preview-page [{:strs [q_id answer]}]
  (page
   [:h2 "Check Your Markdown"]
   [:div {:class "preview"} (md-to-html-string answer)]
   [:form {:method :post :action "/a"}
    (h/raw (anti-forgery-field))
    [:input {:type "hidden" :id "q_id" :name "q_id" :value q_id}]
    [:input {:type "hidden" :id "answer" :name "answer" :value answer}]
    [:button.btn.btn-info.btn-sm "投稿"]]
   [:p "投稿ボタンを押さない限り、QA には反映しない。" [:br]
    "思ったとおりじゃない時はブラウザの「戻る」で修正後に投稿する。"]))

