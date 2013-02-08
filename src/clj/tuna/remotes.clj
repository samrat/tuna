(ns tuna.remotes
  (:require [cemerick.shoreleave.rpc :refer (defremote)])
  (:use tuna.db))

(defremote song-info [id]
  (song-by-id id))

(defremote songs []
  (all-songs))