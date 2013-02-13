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

(rpc/defremote search [query]
  (search-song query))

(defroutes app-routes
  (GET "/" [] (song-list))
  (GET "/song/:id" [id] (song id))
  (GET "/init" [] (do (init-db)
                         {:status 302
                          :headers {"Location"
                                    "/scan"}}))
  (GET "/scan" [] (add-songs "/vault/Music"))
  (GET "/search/:q" [q] (search-song q))
  (GET "/last-login" [] {:status 302
                         :headers {"Location" "http://www.last.fm/api/auth/?api_key=c74adbe5d929bc487cf2f6928a3d494e&cb=http://localhost:3000/last-auth"}})
  (GET "/last-auth" [token] token)
  ;(GET "/play/:id" [id] (play id))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (-> #'app-routes
      rpc/wrap-rpc
      handler/site))
