(ns tuna.index
  (:use hiccup.core
        hiccup.page
        tuna.db))

(defn song-list []
  (html5 [:head
          (include-css "/bootstrap/css/bootstrap.css")
          (include-js "/js/main.js")
          "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"">]
         [:body
          [:h1 "Tuna"]
          (html
           "<audio id='player' autoplay preload='auto' controls></audio>"
           [:span {:class "title"}])]))
