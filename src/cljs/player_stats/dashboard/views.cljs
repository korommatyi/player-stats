(ns player-stats.dashboard.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [player-stats.dashboard.chartjs-component :as chart]
            ))


(def data {:labels ["2012" "2013" "2014" "2015" "2016"]
           :datasets [{:data [5 10 15 20 25]
                       :label "Rev in MM"
                       :borderColor "#90EE90"
                       :backgroundColor "#90EE90"
                       :fill false}
                      {:data [3 6 9 12 15]
                       :label "Cost in MM"
                       :borderColor "#F08080"
                       :backgroundColor "#F08080"
                       :fill false}]})

(defn dashboard-panel []
  [:div {:style {:display "flex" :flex-direction "column" :align-content "space-between"}}
   [:div {:style {:display "flex" :flex-flow "row wrap" :align-content "space-between"}}
    [chart/chartjs-component "hallo" data]
    [chart/chartjs-component "bello" data]]
   [:div {:style {:display "flex" :flex-flow "row wrap" :align-content "space-between"}}
    [chart/chartjs-component "hallo2" data]
    [chart/chartjs-component "bello2" data]]])
