(defproject qa "3.2.0"
  :description "qa system for my literacy classes"
  :url "https://qa.melt.kyutech.ac.jp"
  :min-lein-version "2.0.0"

  :dependencies
  [[buddy/buddy-auth "3.0.323"]
   [buddy/buddy-hashers "2.0.167"]
   [clojure.java-time/clojure.java-time "1.4.3"]
   [com.github.seancorfield/next.jdbc "1.3.1093"]
   [com.fasterxml.jackson.core/jackson-core "2.21.2"]
   [duct/core "0.8.1"]
   [duct/module.ataraxy "0.3.0"]
   [duct/module.logging "0.5.0"]
   [duct/module.sql "0.6.1"]
   [duct/module.web "0.7.4"]
   [environ/environ "1.2.0"]
   [hiccup/hiccup "2.0.0"]
   [io.github.nextjournal/markdown "0.7.225"]
   [org.clojure/clojure "1.12.4"]
   [org.postgresql/postgresql "42.7.10"]
   [http-kit/http-kit "2.8.1"]
   [ring/ring-anti-forgery "1.4.0"]
   [ring/ring-core "1.15.3"]
   [ring/ring-defaults "0.7.0"]
   [ring/ring-jetty-adapter "1.15.3"]
   ; [dev.weavejester/medley "1.9.0"]
   [com.taoensso/timbre "6.5.0"]] ; no 6.8.0. should stay 6.5.0.

  :plugins [[duct/lein-duct "0.12.3"]]
  :main ^:skip-aot qa.main
  :resource-paths ["resources" "target/resources"]
  :prep-tasks     ["javac" "compile" ["run" ":duct/compiler"]]
  :middleware     [lein-duct.plugin/middleware]
  :profiles
  {:dev  [:project/dev :profiles/dev]
   :repl {:prep-tasks   ^:replace ["javac" "compile"]
          :repl-options {:init-ns user}}
   :uberjar {:aot :all}
   :profiles/dev {}
   :project/dev  {:source-paths   ["dev/src"]
                  :resource-paths ["dev/resources"]
                  :dependencies   [[integrant/repl "0.5.1"]; 0.3.3
                                   [hawk "0.2.11"]
                                   [eftest "0.6.0"]
                                   [kerodon "0.9.1"]]}})
