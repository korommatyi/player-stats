(ns player-stats.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [player-stats.subs :as subs]
            [player-stats.chartjs-component :as chart]
            [secretary.core :as secretary]
            [reagent-material-ui.core :as mui]
            [cljsjs.material-ui]
            [cljsjs.react]
            [cljsjs.react.dom]
            ))


(def el reagent/as-element)
(defn icon [nme] [mui/FontIcon {:className "material-icons"} nme])
(defn color [nme] (aget mui/colors nme))

(defonce theme-defaults {:muiTheme (mui/getMuiTheme
                                    (-> mui/darkBaseTheme
                                        (js->clj :keywordize-keys true)
                                        (update :palette merge {:primary1Color (color "amber500")
                                                                :primary2Color (color "amber700")})
                                        clj->js))})


(defn simple-nav []
  (let [is-open? (reagent/atom false)
        close #(reset! is-open? false)]
    (fn []
      [:div
       [mui/AppBar {:title "Statistics" :onLeftIconButtonTouchTap #(reset! is-open? true)}]
       [mui/Drawer {:open @is-open? :docked false}
        [mui/List
         [mui/ListItem {:leftIcon (el (icon "equalizer"))
                        :on-click (fn [] (close))
                        :href "#/"}
          "Statistics"]
         [mui/ListItem {:leftIcon (el (icon "add circle"))
                        :on-click (fn [] (close))
                        :href "#/add-scores"}
          "Add Scores"]]]])))

;; home

(defn home-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div (str "Hello from " @name ". This is the Home Page.")
     [:div [:a {:href "#/add-scores"} "go to Add-Scores Page"]]]))


;; add-scores

(defn add-scores-panel []
  [:div "This is the Add-Scores Page."
   [:div [:a {:href "#/"} "go to Home Page"]]])


;; main

(defn- panels [panel-name]
  (case panel-name
    :home-panel [home-panel]
    :add-scores-panel [add-scores-panel]
    [:div]))

(defn show-panel [panel-name]
  [panels panel-name])

(def data {:labels ["2012" "2013" "2014" "2015" "2016"]
           :datasets [{:data [5 10 15 20 25]
                       :label "Rev in MM"
                       :borderColor "#90EE90"
                       :fill false}
                      {:data [3 6 9 12 15]
                       :label "Cost in MM"
                       :borderColor "#F08080"
                       :fill false}]})

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    [mui/MuiThemeProvider theme-defaults
     [:div
      [simple-nav]
      [:div [show-panel @active-panel] [chart/chartjs-component "hallo" data]]]]))

;; (defn main-panel []
;;   (let [active-panel (re-frame/subscribe [::subs/active-panel])]
;;     [:div [show-panel @active-panel] [chart/chartjs-component "hallo" data]]))
