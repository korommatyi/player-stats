(ns player-stats.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [player-stats.subs :as subs]
            [player-stats.dashboard.views :as dash]
            [player-stats.add-game.views :as ag]
            [player-stats.games.views :as games]
            [reagent-material-ui.core :as mui]
            [cljsjs.material-ui]
            [cljsjs.react]
            [cljsjs.react.dom]
            [player-stats.mui-helpers :as muih]
            ))

(defonce theme-defaults {:muiTheme (mui/getMuiTheme
                                    (-> mui/lightBaseTheme
                                        (js->clj :keywordize-keys true)
                                        (update :palette merge {:primary1Color (muih/color "blueGrey500")
                                                                :primary2Color (muih/color "blueGrey700")})
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
         [mui/ListItem {:leftIcon (muih/el (muih/icon "equalizer"))
                        :on-click (fn [] (close))
                        :href "/dashboard"}
          "Dashboard"]
         [mui/ListItem {:leftIcon (muih/el (muih/icon "add circle"))
                        :on-click (fn [] (close))
                        :href "/add-game"}
          "Add Game"]
         [mui/ListItem {:leftIcon (muih/el (muih/icon "list"))
                        :on-click (fn [] (close))
                        :href "/games"}
          "Games"]]]])))

;; main

(defn- panels [panel-name]
  (case panel-name
    :dashboard-panel [dash/dashboard-panel]
    :add-game-panel [ag/add-game-panel]
    :games-panel [games/games-panel]
    [:div]))

(defn show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    [mui/MuiThemeProvider theme-defaults
     [:div
      [simple-nav]
      [show-panel @active-panel]]]))
