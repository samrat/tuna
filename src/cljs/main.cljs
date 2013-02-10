(ns tuna.main
  (:require [dommy.template :as template]
            [enfocus.core :as e]
            [shoreleave.remotes.http-rpc :as rpc]
            )
  (:require-macros [enfocus.macros :as em]
                   [shoreleave.remotes.macros :as rpcm]
                   ))

(em/defaction add-to-body [stuff]
  ["body"] (em/append stuff))

(defn add-song-list []
  (rpc/remote-callback
   :songs []
   (fn [songs]
     (add-to-body (template/node
                   [:div {:id "song-list"}
                    [:table {:class "table table-striped table-hover table-condensed"}
                     [:thead
                      [:th "Title"]
                      [:th "Artist"]]
                     [:tbody {:class "selectable"}
                      (for [song (sort-by :artist songs)]
                        [:tr
                         [:td {:onclick (str "javascript:tuna.main.append_audio('"
                                             (:id song)
                                             "'); tuna.main.show_song_info('"
                                             (:id song)
                                             "'); player.play(); tuna.main.show_pause_icon();")}
                          (:title song)]
                         [:td (:artist song)]])]]])))))

(em/defaction render-song-info [info]
   [".title"] (em/content (:title info)))

(defn show-song-info [id]
  (rpc/remote-callback :song-info [id] #(render-song-info %)))

(em/defaction append-audio [id]
  ["#player"]
  (em/set-attr :src (str "/song/" id)))

(em/defaction show-pause-icon []
  ["#play-pause"] (em/set-attr :class "icon-pause"))

(defn toggle-play-pause []
  (if (= (em/from (em/select ["#play-pause"]) (em/get-attr :class)) "icon-play")
    (do (em/at js/document ["#play-pause"] (em/set-attr :class "icon-pause"))
        (.play (.getElementById js/document "player"))) 
    (do (em/at js/document ["#play-pause"] (em/set-attr :class "icon-play"))
        (.pause (.getElementById js/document "player")))))

(em/defaction setup-listeners []
  ["#play-pause"] (em/listen
                   :click
                   toggle-play-pause))

(em/defaction start []
  (setup-listeners)
  (add-song-list))

(set! (.-onload js/window) start)