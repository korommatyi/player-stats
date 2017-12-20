(ns player-stats.add-scores.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            ))

(defn add-scores-panel []
  [:div "This is the Add-Scores Page."
   [:div [:a {:href "#/"} "go to Statistics Page"]]])
