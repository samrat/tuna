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
  (let [songs (sort-by :artist (all-songs))]
    (map (fn [song]
               (assoc song :serial (.indexOf songs song)))
             songs)))

(rpc/defremote search [query]
  (let [songs (search-song query)]
    (map (fn [song]
               (assoc song :serial (.indexOf songs song)))
             songs)))

(defroutes app-routes
  (GET "/" [] (song-list))
  (GET "/song/:id" [id] (song id))
  (GET "/init" [] (do (init-db)
                      {:status 302
                       :headers {"Location" "/scan"}}))
  (GET "/scan" [] (do (add-songs "/vault/Music")
                      {:status 302
                       :headers {"Location" "/"}}))
  (GET "/search/:q" [q] (search-song q))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> #'app-routes
      rpc/wrap-rpc
      handler/site))
