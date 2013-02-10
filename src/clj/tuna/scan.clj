(ns tuna.scan
  (:use [clojure.java.io :as io]
        tika
        tuna.db)
  (:require [clojure.data.codec.base64 :as b64]))

(defn- regex-file-seq
  "Lazily filter a directory based on regex."
  [regex in-dir]
  (filter #(re-find regex (.getPath %)) (file-seq (io/file in-dir))))

(defn mp3-files [source]
  "Return a seq of mp3 files from the source directory."
  (regex-file-seq #".*\.mp3$" source))

(defn song-info [song-path]
  (let [metadata (try (parse song-path)
                      (catch Exception e {:title "Untitled track"
                                          :xmpdm:artist "Unknown Artist"
                                          :path song-path}))]
    {:title (first (:title metadata))
     :artist (first (:xmpdm:artist metadata))
     :id (String. (b64/encode (.getBytes (str (first (:title metadata))
                                              (first (:xmpdm:artist metadata))))))
     :path song-path}))

(defn add-songs [source]
  (do (init-db)
      (map add-to-db (map song-info (mp3-files source)))))
