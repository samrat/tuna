(ns tuna.index
  (:use hiccup.core
        hiccup.page
        tuna.db))

(defn song-list []
  (html5 [:head (include-css "/bootstrap/css/bootstrap.css")
          "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"">]
         [:body [:h1 "hello"]
          (html [:table {:class "table table-striped table-hover table-condensed"}
                 [:thead
                  [:th "Title"]
                  [:th "Artist"]]
                 [:tbody {:class "selectable"} (for [song (sort-by :artist (all-songs))]
                                                 [:tr
                                                  [:th [:a {:href (str "/play/" (:id song))}
                                                        (:title song)]]
                                                  [:th (:artist song)]])]])]))

(defn play [id]
  (html5 (html
          [:head [:title (:title (song-by-id id))]]
          [:body [:audio {:src (str "/song/" id)
                               :preload "auto"
                               :controls ""}]])))