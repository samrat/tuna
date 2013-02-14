(ns tuna.controls
  (:require [enfocus.core :as ef])
  (:require-macros [enfocus.macros :as em]))

(em/defaction show-pause-icon []
  ["#play-pause"] (em/set-attr :class "icon-pause"))

(em/defaction show-play-icon []
  ["#play-pause"] (em/set-attr :class "icon-play"))
