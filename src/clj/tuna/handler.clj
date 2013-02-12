(ns tuna.handler
  (:use compojure.core
        tuna.song
        tuna.index
        tuna.scan
        tuna.db)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [cemerick.shoreleave.rpc :as rpc]))

(rpc/defremote song-info [id]
  (song-by-id id))

(rpc/defremote songs []
  (all-songs))

(defroutes app-routes
  (GET "/" [] (song-list))
  (GET "/song/:id" [id] (song id))
  (GET "/scan" [] (add-songs "/vault/Music"))
  ;(GET "/play/:id" [id] (play id))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> #'app-routes
      rpc/wrap-rpc
      handler/site))
