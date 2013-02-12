(ns tuna.db
  (:require [clojure.java.jdbc :as sql]
            [clucy.core :as clucy]))

(def songs-db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "/tmp/songs.db"
   })

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
          ["SELECT * from songs"]
          (into [] result))))

(defn song-by-id [id]
  (first (sql/with-connection songs-db
           (sql/with-query-results result
             ["SELECT * from songs WHERE id = ?" id]
             (into [] result)))))

(defn songs-by-artist [artist]
  (first (sql/with-connection songs-db
           (sql/with-query-results result
             ["SELECT * from songs WHERE artist = ?" artist]
             (into [] result)))))

(def clucy-index (clucy/disk-index "/tmp/tuna"))

(defn add-to-db [song]
  (do (sql/with-connection songs-db
        (sql/insert-records :songs song))
      (clucy/add clucy-index song)))

(defn search-song [query]
  (if-not (= query "")
    (clucy/search clucy-index query 10)
    (all-songs)))