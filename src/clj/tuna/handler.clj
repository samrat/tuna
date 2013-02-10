(ns tuna.handler
  (:use compojure.core
        tuna.song
        tuna.index
        tuna.scan)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [cemerick.shoreleave.rpc :as rpc]))

(defroutes app-routes
  (GET "/" [] (song-list))
  (GET "/song/:id" [id] (song id))
  (GET "/scan" [] (add-songs))
  ;(GET "/play/:id" [id] (play id))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> #'app-routes
      rpc/wrap-rpc
      handler/site))
