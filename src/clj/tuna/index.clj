(ns tuna.index
  (:use hiccup.core
        hiccup.page
        tuna.db))

(defn song-list []
  (html5 [:head
          (include-css "/bootstrap/css/bootstrap.css")
          (include-css "/css/font-awesome.min.css")
          (include-js "/js/main.js")
          "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"">]
         [:body
          [:h1 "Tuna"]
          [:i {:id "play-pause" :class "icon-play"}]
          (html
           "<audio id='player' preload='auto'></audio>"
           [:div [:span {:class "title"}]]
           ;[:div {:id "search"} [:input {:type "text" :id "query"}]]
           )]))
