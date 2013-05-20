(ns tuna.handler
  (:use compojure.core
        tuna.song
        tuna.index
        tuna.scan
        tuna.db
        sandbar.stateful-session)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [cemerick.shoreleave.rpc :as rpc]
            [me.raynes.least :as least]
            [me.raynes.least.authorize :as lauth]
            [clj-http.client :as client]))

(def config (read-string (slurp "config.clj")))

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

(rpc/defremote scrobble
  [id]
  (let [info (song-by-id id)]
    (least/write "track.scrobble"
                 (:last-key config)
                 {:secret (:last-secret config)
                  :sk (slurp "/home/samrat/.cache/tuna")
                  :timestamp (int (/ (System/currentTimeMillis) 1000))
                  :track (:title info)
                  :artist (:artist info)})))

(defroutes app-routes
  (GET "/" [] (song-list))
  (GET "/song/:id" [id] (song id))
  (GET "/init" [] (do (init-db)
                      {:status 302
                       :headers {"Location" "/scan"}}))
  (GET "/scan" [] (do (doall (add-songs "/vault/Music"))
                      {:status 302
                       :headers {"Location" "/"}}))
  (GET "/login" [] (do
                     (session-put! :token
                                   (lauth/get-token (:last-key config)
                                                    (:last-secret config)))
                     {:status 302
                      :headers {"Location" (:url (session-get :token))}}))
  (GET "/auth" [] (do (spit "/home/samrat/.cache/tuna"
                            (:key (lauth/get-session (:last-key config)
                                                     (:last-secret config)
                                                     (:token (session-get :token)))))
                      {:status 302
                       :headers {"Location" "/"}}))
  (GET "/search/:q" [q] (search-song q))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> #'app-routes
      wrap-stateful-session
      rpc/wrap-rpc
      handler/site))
