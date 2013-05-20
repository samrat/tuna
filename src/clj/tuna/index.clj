(ns tuna.index
  (:use hiccup.core
        hiccup.page
        tuna.db))

(defn song-list []
  (html5 (html [:head
                (include-css "/bootstrap/css/bootstrap.css")
                [:script {:src "/js/main.js" :async ""}]
                (include-css "/css/font-awesome.min.css")
                (include-js "/js/mousetrap.min.js")
                [:title "Tuna"]
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0\">"
                "<meta name=\"apple-mobile-web-app-capable\" content=\"yes\">"]
               [:body
                [:div {:class "navbar navbar-fixed-bottom"}
                 [:div {:style "text-align:center;" :class "navbar-inner"}
                  [:div
                   [:i {:id "prev-song" :class "icon-step-backward" :style "font-size:3em;padding:5px;"}]
                   [:i {:id "play-pause" :class "icon-play" :style "font-size:3em;padding:5px;"}]
                   [:i {:id "next-song" :class "icon-step-forward" :style "font-size:3em;padding:5px;"}]]
                  [:div {:class "title"} "&nbsp;"]
                  [:div {:class "artist"} "&nbsp;"]
                  [:div [:div {:class "progress progress-striped"}
                         [:div {:class "bar"}]
                         [:div {:id "current" :style "text-align:right;"}
                          "00:00 / 00:00"]]]]]
                (if (empty? (try (slurp "/home/samrat/.cache/tuna")
                                 (catch Exception e nil)))
                  [:div [:a {:href "/login" :target "blank"} "Login to Last.fm"]
                   [:br]
                   [:a {:href "/auth"} "Click here after authorizing tuna."]])
                [:div {:class "container"} [:input {:type "text"
                                                    :style "height:30px;"
                                                    :id "query"
                                                    :class "input-xxlarge search-query"
                                                    :placeholder "Search"}]]
                [:div {:class "container" :id "song-list"}]
                [:audio {:id "player"
                         :preload "auto"
                         :ontimeupdate "tuna.main.show_current_time(this.currentTime,this.duration);"}]]
                )))
