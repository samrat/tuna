(ns tuna.scan
  (:use [clojure.java.io :as io]
        tika
        tuna.db)
  (:require [crypto.random :as crypto])
  (:import [org.jaudiotagger.audio AudioFileIO]
           [org.jaudiotagger.tag FieldKey Tag TagField TagTextField]
           [org.jaudiotagger.tag.mp4 Mp4Tag Mp4FieldKey]))

(defn- regex-file-seq
  "Lazily filter a directory based on regex."
  [regex in-dir]
  (filter #(re-find regex (.getPath %)) (file-seq (io/file in-dir))))

(defn audio-files [source]
  "Return a seq of mp3 files from the source directory."
  (remove (existing-paths) (regex-file-seq #".*\.(mp3|flac|m4a)$" source)))

(defn- song-info [song-path]
  (let [audio (AudioFileIO/read song-path)
        tag (.getTag audio)
        header (.getAudioHeader audio)
        title (.getFirst tag FieldKey/TITLE)
        artist (.getFirst tag FieldKey/ARTIST)
        album (.getFirst tag FieldKey/ALBUM)
        mime (detect-mime-type song-path)]
    {:title title
     :artist artist
     :album album
     :length (.getTrackLength header)
     :id (crypto/url-part 8)
     :path song-path
     :mimetype mime}))

(defn add-songs [source]
  (do (map add-to-db (map song-info (audio-files source)))))
