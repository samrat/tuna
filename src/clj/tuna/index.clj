(ns tuna.index
  (:use hiccup.core
        hiccup.page
        tuna.db))

(defn song-list []
  (html5 (html [:head
                (include-css "/bootstrap/css/bootstrap.css")
                (include-css "/css/font-awesome.min.css")
                (include-js "/js/main.js")
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                "<meta name=\"apple-mobile-web-app-capable\" content=\"yes\">"]
               [:body
                [:div {:class "navbar navbar-fixed-bottom"}
                 [:div {:class "navbar-inner"}
                   [:input {:type "text"
                            :id "query"
                            :class "navbar-search pull-right search-query"
                            :placeholder "Search"}]
                  [:i {:id "prev-song" :class "icon-step-backward" :style "font-size:2em;padding:5px;"}]
                  [:i {:id "play-pause" :class "icon-play" :style "font-size:2em;padding:5px;"}]
                  [:i {:id "next-song" :class "icon-step-forward" :style "font-size:2em;padding:5px;"}]
                  [:div [:span {:class "title"}]
                   [:span {:id "current"} "00:00 / 00:00"]
                   ]
                  [:div {:class "progress progress-striped"}
                   [:div {:class "bar"}]]]] 
                [:audio {:id "player"
                         :preload "auto"
                         :ontimeupdate "tuna.main.show_current_time(this.currentTime,this.duration);"}]]
                )))
