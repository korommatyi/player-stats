(ns player-stats.add-scores.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [reagent-material-ui.core :as mui]
            [cljsjs.material-ui]
            [cljsjs.react]
            [cljsjs.react.dom]
            [player-stats.add-scores.subs :as subs]
            [player-stats.mui-helpers :as muih]
            ))

(defn team [t]
  [:div "Here comes a team"])

(defn result []
  [mui/IconButton {:tooltip "Change direction"}
   (case @(re-frame/subscribe [::subs/result])
     :team-a-won [muih/icon "home"]
     [:div])])

(defn add-scores-panel []
  [:div {:style {:display "flex" :flex-flow "row wrap" :justify-content "space-around"}}
   [team :a]
   [result]
   [team :b]])
