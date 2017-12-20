(ns player-stats.add-scores.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [reagent-material-ui.core :as mui]
            [cljsjs.material-ui]
            [cljsjs.react]
            [cljsjs.react.dom]
            [player-stats.add-scores.subs :as subs]
            [player-stats.mui-helpers :as muih]
            [player-stats.add-scores.events :as events]
            ))

(defn team [t]
  [:div "Here comes a team"])

(defn result [r]
  [mui/IconButton {:tooltip "Change direction"
                   :onClick #(re-frame/dispatch [::events/change-result])}
   (case @(re-frame/subscribe [::subs/result])
     :team-a-won [muih/icon "keyboard_arrow_right"]
     :team-b-won [muih/icon "keyboard_arrow_left"]
     [muih/icon "drag_handle"])])

(defn add-scores-panel []
  [:div {:style {:display "flex" :flex-flow "row wrap" :justify-content "space-around"}}
   [team :a]
   [result]
   [team :b]])
