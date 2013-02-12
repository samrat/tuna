(defproject tuna "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :source-paths ["src/clj"]
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [compojure "1.1.5"]
                 [hiccup "1.0.2"]
                 [prismatic/dommy "0.0.1"]
                 [enfocus "1.0.0-beta3"]
                 [com.cemerick/shoreleave-remote-ring "0.0.2"]
                 [shoreleave/shoreleave-remote "0.2.2"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [org.xerial/sqlite-jdbc "3.7.2"]
                 [clj-tika "1.2.0"]
                 [de.u-mass/lastfm-java "0.1.2"]
                 [org/jaudiotagger "2.0.3"]
                 [clucy "0.3.0"]
                 [crypto-random "1.1.0"]]
  :plugins [[lein-ring "0.8.2"]
            [lein-cljsbuild "0.3.0"]]
  :ring {:handler tuna.handler/app}

  :cljsbuild {
              :builds [{
                        :incremental false
                        :source-paths ["src/cljs"]
                        :compiler {
                                   :output-to "resources/public/js/main.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.3"]
                        [ring-server "0.2.7"]]}})
