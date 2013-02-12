(ns tuna.main
  (:use [tuna.controls :only (show-pause-icon show-play-icon toggle-play-pause)])
  (:require [dommy.template :as template]
            [enfocus.core :as ef]
            [shoreleave.remotes.http-rpc :as rpc])
  (:require-macros [enfocus.macros :as em]
                   [shoreleave.remotes.macros :as rpcm]))

(em/defaction add-to-body [stuff]
  ["body"] (em/append stuff))

(defn song-list-hof [f]
  (rpc/remote-callback
   :songs []
   #(f %)))

(defn render-song-list [songs]
  (template/node [:div {:id "song-list"}
                  [:table {:class "table table-hover table-condensed"}
                   [:thead
                    [:th "Title"]
                    [:th "Album"]
                    [:th "Artist"]]
                   [:tbody {:class "selectable"}
                    (for [song (sort-by :artist songs)]
                      [:tr {:onclick
                            (str "javascript:tuna.main.play_audio('"
                                 (:id song) "');")}
                       [:td (:title song)]
                       [:td (:album song)]
                       [:td (:artist song)]])]]]))

(defn search [query]
  (rpc/remote-callback
   :search [query]
   (fn [songs]
     (em/at js/document ["#song-list"] (em/substitute (render-song-list songs))))))

(defn add-song-list []
  (song-list-hof (fn [songs] (add-to-body (render-song-list songs)))))

(em/defaction show-song-title [title]
  [".title"] (em/content title))

(defn secs->mins [secs]
  (let [minutes (Math/floor (/ secs 60))
        seconds (.toString (Math/floor (- secs (* minutes 60))))]
    (str minutes
         ":"
         (if (= 1 (.-length seconds))
           (str "0" seconds)
           seconds))))

(em/defaction show-song-length [length]
  [".length"] (em/content (secs->mins length)))

(em/defaction render-song-info [info]
  (em/do-> (show-song-title (:title info))
           (show-song-length (:length info))))

(defn show-song-info [id]
  (rpc/remote-callback :song-info [id] #(render-song-info %)))

(em/defaction set-audio-src [id]
  ["#player"]
  (em/do-> (em/set-attr :src (str "/song/" id))
           (em/set-attr :songid id)))

(em/defaction show-current-time [current duration]
  ["#current"] (em/content (str (secs->mins current)
                                " / "
                                (secs->mins duration)))
  [".bar"] (em/set-attr :style (str "width:"
                                    (* (/ current duration) 100)
                                    "%;")))

(defn play-audio [id]
  (do (set-audio-src id)
      (show-song-info id)
      (.play (.getElementById js/document "player"))
      (show-pause-icon)))

(em/defaction setup-listeners []
  ["#play-pause"] (em/listen
                   :click
                   toggle-play-pause)
  [".search-query"] (em/listen
                     :keyup
                     #(search (.-value (.getElementById js/document
                                                        "query")))))

(em/defaction start []
  (setup-listeners)
  (add-song-list))

(set! (.-onload js/window) start)
