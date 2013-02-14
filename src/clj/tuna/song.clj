(ns tuna.song
  (:use tuna.db))

(defn song [id]
  (let [s (song-by-id id)
        path (:path s)]
    {:status 200
     :headers {"Content-Type" (:mimetype s)
               "Content-Disposition" "attachment"
               "X-Content-Duration" (:length s)
               "Content-Length" (str (.length (clojure.java.io/file path)))
               "Accept-Ranges" "bytes"}
     :body (clojure.java.io/input-stream path)}))
