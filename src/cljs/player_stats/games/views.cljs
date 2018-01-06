(ns player-stats.games.views
  (:require [re-frame.core :as re-frame]
            [player-stats.subs :as subs]
            [reagent-material-ui.core :as mui]
            [player-stats.mui-helpers :as muih]
            [cljsjs.material-ui]
            [cljsjs.react]
            [cljsjs.react.dom]
            [clojure.string :as str]))

(defn result->icon [r]
  (case r
    "team-a-won" "keyboard_arrow_right"
    "team-b-won" "keyboard_arrow_left"
    "drag_handle"))

(defn game-entry [{:keys [team-a team-b result date]}]
  [mui/TableRow
   [mui/TableRowColumn date]
   [mui/TableRowColumn (str/join ", " team-a)]
   [mui/TableRowColumn [muih/icon (result->icon result)]]
   [mui/TableRowColumn (str/join ", " team-b)]])

(defn games-table []
  (let [games @(re-frame/subscribe [::subs/raw-data])]
    [mui/Table {:selectable false}
     [mui/TableHeader {:adjustForCheckbox false :displaySelectAll false}
      [mui/TableRow
       [mui/TableHeaderColumn "Date"]
       [mui/TableHeaderColumn "The Awesomes"]
       [mui/TableHeaderColumn]
       [mui/TableHeaderColumn "The Geniuses"]]]
     [mui/TableBody {:showRowHover true :displayRowCheckbox false}
      (for [game games]
        ^{:key (hash game)} [game-entry game])]]))

(defn games-panel []
  [:div {:style {:display "flex" :align-items "center"}}
   [:div {:style {:margin "auto 10%"}}
    [games-table]]])
