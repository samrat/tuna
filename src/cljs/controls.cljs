(ns tuna.controls
  (:require [enfocus.core :as ef])
  (:require-macros [enfocus.macros :as em]))

(em/defaction show-pause-icon []
  ["#play-pause"] (em/set-attr :class "icon-pause"))

(em/defaction show-play-icon []
  ["#play-pause"] (em/set-attr :class "icon-play"))

(defn toggle-play-pause []
  (if (= (em/from (em/select ["#play-pause"]) (em/get-attr :class)) "icon-play")
    (do (show-pause-icon)
        (.play (.getElementById js/document "player"))) 
    (do (show-play-icon)
        (.pause (.getElementById js/document "player")))))
