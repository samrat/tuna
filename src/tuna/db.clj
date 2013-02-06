(ns tuna.db
  (:require [clojure.java.jdbc :as sql]))

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
       [:id "varchar(200)"]
       [:path "varchar(1000)"]))
    (catch Exception ex
      (.getMessage (.getNextException ex)))))

(defn all-songs []
  (sql/with-connection songs-db
        (sql/with-query-results result
          ["SELECT * from songs"]
          (into [] result))))

(defn song-by-id [id]
  (sql/with-connection songs-db
    (sql/with-query-results result
      ["SELECT * from songs WHERE id = ?" id]
      (into [] result))))

(defn add-to-db [song]
  (sql/with-connection songs-db
    (sql/insert-records :songs song)))