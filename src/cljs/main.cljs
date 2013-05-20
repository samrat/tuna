(ns tuna.main
  (:use [tuna.controls :only (show-pause-icon
                              show-play-icon
                              show-song-title
                              show-song-artist)])
  (:require [dommy.template :as template]
            [enfocus.core :as ef]
            [shoreleave.remotes.http-rpc :as rpc])
  (:require-macros [enfocus.macros :as em]
                   [shoreleave.remotes.macros :as rpcm]))

(em/defaction add-to-body [stuff]
  ["#song-list"] (em/content stuff))

(defn song-list-hof [f]
  (rpc/remote-callback
   :songs []
   #(f %)))

(defn render-song-list [songs]
  (template/node [:table {:class "table table-hover"}
                   [:thead
                    [:th "Title"]
                    [:th "Album"]
                    [:th "Artist"]]
                   [:tbody {:class "selectable"}
                    (for [song songs]
                      [:tr {:onclick
                            (str "javascript:tuna.main.play_audio('"
                                 (:id song) "');")
                            :id (:id song)
                            :next (try
                                    (:id (nth songs (inc (:serial song))))
                                    (catch js/Object e ""))
                            :prev (try
                                    (:id (nth songs (dec (:serial song))))
                                    (catch js/Object e ""))}
                       [:td (:title song)]
                       [:td (:album song)]
                       [:td (:artist song)]])]]))

(defn search [query]
  (rpc/remote-callback
   :search [query]
   (fn [songs]
     (em/at js/document ["#song-list"] (add-to-body (render-song-list songs))))))

(defn add-song-list []
  (song-list-hof (fn [songs] (add-to-body (render-song-list songs)))))

(defn secs->mins [secs]
  (let [minutes (Math/floor (/ secs 60))
        seconds (.toString (Math/floor (- secs (* minutes 60))))]
    (str minutes
         ":"
         (if (= 1 (.-length seconds))
           (str "0" seconds)
           seconds))))

(em/defaction set-title [title]
  ["title"] (em/content title))

(em/defaction render-song-info [info]
  (do (show-song-title (:title info))
      (show-song-artist (:artist info))
      (set-title (str "▶ "
                      (:artist info)
                      " - "
                      (:title info)))))

(defn show-song-info [id]
  (rpc/remote-callback :song-info [id] #(render-song-info %)))

(em/defaction set-audio-src [id]
  ["#player"]
  (em/do-> (em/set-attr :src (str "/song/" id))
           (em/set-attr :songid id)))

(defn show-current-time [current duration]
  (do
    (em/at js/document ["#current"] (em/content (str (secs->mins current)
                                                     " / "
                                                     (secs->mins duration))))
    (em/at js/document [".bar"] (em/set-attr :style (str "width:"
                                                         (* (/ current duration) 100)
                                                         "%;")))
    (if (= current duration) ; song is complete
      (do (em/at js/document [".bar"] (em/delay 500 (em/set-attr :style "width: 0%;")))
          (show-play-icon)
          (show-song-title "&nbsp;")
          (show-song-artist "&nbsp;")
          (set-title "Tuna")
          (rpc/remote-callback :scrobble [(em/from (em/select ["#player"])
                                                   (em/get-attr :songid))]
                               nil)
          (em/at js/document ["#current"] (em/content "00:00 / 00:00"))))))

(defn set-next-song [id]
  (let [next-song (em/from (em/select [(str "#" id)])
                          (em/get-attr :next))]
        (em/at js/document ["#next-song"]
               (em/set-attr :onclick
                            (str "javascript:tuna.main.play_audio('"
                                 next-song
                                 "');")))))

(defn set-prev-song [id]
  (let [prev-song (em/from (em/select [(str "#" id)])
                          (em/get-attr :prev))]
        (em/at js/document ["#prev-song"]
               (em/set-attr :onclick
                            (str "javascript:tuna.main.play_audio('"
                                 prev-song
                                 "');")))))
(defn play-audio [id]
  (do (set-audio-src id)
      (show-song-info id)
      (.play (.getElementById js/document "player"))
      (show-pause-icon)
      (set-next-song id)
      (set-prev-song id)))

(defn toggle-play-pause []
  (if (= (em/from (em/select ["#play-pause"]) (em/get-attr :class)) "icon-play")
    (do (.play (.getElementById js/document "player"))
        (show-pause-icon))
    (do (.pause (.getElementById js/document "player"))
        (show-play-icon))))

(em/defaction setup-listeners []
  ["#play-pause"] (em/listen
                   :click
                   toggle-play-pause)
  [".search-query"] (em/listen
                     :keyup
                     #(search (.-value (.getElementById js/document
                                                        "query")))))

(em/defaction focus-search []
  ["#query"] (em/focus))

(defn bind-key [key f action]
  (.bind js/Mousetrap key f (name action)))

(defn seek [direction time]
  (let [player (.getElementById js/document "player")
        current (.-currentTime player)]
    (set! (.-currentTime player)
          (if (= direction :forward)
            (+ current time)
            (- current time)))))

(defn keybindings []
  (do (bind-key "/" focus-search :keyup)
      (bind-key "shift+p" toggle-play-pause :keydown)
      (bind-key "<" #(.click (.getElementById js/document "prev-song")) :keydown)
      (bind-key ">" #(.click (.getElementById js/document "next-song")) :keydown)
      (bind-key "f" #(seek :forward 5) :keydown)
      (bind-key "b" #(seek :backward 5) :keydown)))

(em/defaction start []
  (setup-listeners)
  (add-song-list)
  (keybindings))

(set! (.-onload js/window) start)
