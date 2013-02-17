(ns tuna.controls
  (:require [enfocus.core :as ef])
  (:require-macros [enfocus.macros :as em]))

(em/defaction show-pause-icon []
  ["#play-pause"] (em/set-attr :class "icon-pause"))

(em/defaction show-play-icon []
  ["#play-pause"] (em/set-attr :class "icon-play"))

(em/defaction show-song-title [title]
  [".title"] (em/content title))

(em/defaction show-song-artist [artist]
  [".artist"] (em/content artist))
