(defproject tuna "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.4.0"]
                 [ring-server "0.2.7"]
                 [compojure "1.1.5"]
                 [hiccup "1.0.2"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [org.clojure/data.codec "0.1.0"]
                 [org.xerial/sqlite-jdbc "3.7.2"]
                 [clj-tika "1.2.0"]]
  :plugins [[lein-ring "0.8.2"]]
  :ring {:handler tuna.handler/app}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.3"]]}})
