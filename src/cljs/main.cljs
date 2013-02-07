(ns tuna.main
  (:require [dommy.template :as template]
            [enfocus.core :as e])
  (:require-macros [enfocus.macros :as em]))

(defn audio-element [song-id]
  (template/node [:audio {:id "player"
                          :src (str "/song/" song-id)
                          :preload "auto"
                          :autoplay ""}]))

(em/defaction remove-audio []
  ())

(em/defaction append-audio [id]
  ["#player"]
  (em/set-attr :src (str "/song/" id)))

;(set! (.-onload js/window) ())

