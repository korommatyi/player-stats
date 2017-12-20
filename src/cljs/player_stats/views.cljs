(ns player-stats.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [player-stats.subs :as subs]
            [player-stats.dashboard.views :as dash]
            [player-stats.add-scores.views :as a-s]
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
        close #(reset! is-open? false)
        title (re-frame/subscribe [::subs/active-panel-human-friendly])]
    (fn []
      [:div
       [mui/AppBar {:title @title :onLeftIconButtonTouchTap #(reset! is-open? true)}]
       [mui/Drawer {:open @is-open? :docked false}
        [mui/List
         [mui/ListItem {:leftIcon (el (icon "equalizer"))
                        :on-click (fn [] (close))
                        :href "#/"}
          "Dashboard"]
         [mui/ListItem {:leftIcon (el (icon "add circle"))
                        :on-click (fn [] (close))
                        :href "#/add-scores"}
          "Add Scores"]]]])))

;; main

(defn- panels [panel-name]
  (case panel-name
    :dashboard-panel [dash/dashboard-panel]
    :add-scores-panel [a-s/add-scores-panel]
    [:div]))

(defn show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    [mui/MuiThemeProvider theme-defaults
     [:div
      [simple-nav]
      [show-panel @active-panel]]]))
