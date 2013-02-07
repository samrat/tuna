(ns tuna.handler
  (:use compojure.core
        tuna.song
        tuna.index)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]))

(defroutes app-routes
  (GET "/" [] (song-list))
  (GET "/song/:id" [id] (song id))
  (GET "/play/:id" [id] (play id))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
