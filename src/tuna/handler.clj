(ns tuna.handler
  (:use compojure.core
        tuna.song)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]))

(defroutes app-routes
  (GET "/song/:id.mp3" [id] (song id))
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
