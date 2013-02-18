(ns tuna.db
  (:require [clojure.java.jdbc :as sql]
            [clucy.core :as clucy]))

(def songs-db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "/tmp/songs.db"})

(def clucy-index (clucy/disk-index "/tmp/tuna"))

(defn init-db []
  (try
    (sql/with-connection songs-db
      (sql/create-table
       :songs
       [:title "varchar(300)"]
       [:artist "varchar(300)"]
       [:album "varchar(300)"]
       [:id "varchar(200)"]
       [:path "varchar(1000)"]
       [:mimetype "varchar(100)"]
       [:length "varchar(100)"]))
    (catch Exception ex
      (.getMessage (.getNextException ex)))))

(defn all-songs []
  (sql/with-connection songs-db
    (sql/with-query-results result
      ["SELECT title,artist,album,id,length from songs"]
      (vec result))))

(defn song-by-id [id]
  (first (sql/with-connection songs-db
           (sql/with-query-results result
             ["SELECT * from songs WHERE id = ?" id]
             (vec result)))))

(defn songs-by-artist [artist]
  (first (sql/with-connection songs-db
           (sql/with-query-results result
             ["SELECT * from songs WHERE artist = ?" artist]
             (vec result)))))

(defn existing-paths []
  (map clojure.java.io/file (map :path (sql/with-connection songs-db
                                         (sql/with-query-results result
                                           ["SELECT path from songs"]
                                           (set result))))))

(defn add-to-db [song]
  (do (sql/with-connection songs-db
        (sql/insert-records :songs song))
      (clucy/add clucy-index song)))

(defn search-song [query]
  (if-not (= query "")
    (map #(dissoc % :path :mimetype) (clucy/search clucy-index (str query "~") 30))
    (sort-by :artist (all-songs))))