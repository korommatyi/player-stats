(ns player-stats.chartjs-component
  (:require [reagent.core :as reagent]
            [cljsjs.chartjs]
            ))

(defn show-line-chart
  [id data]
  (let [context (.getContext (.getElementById js/document id) "2d")
        chart-data {:type "line"
                    :data data}]
      (js/Chart. context (clj->js chart-data))))

(defn chartjs-component
  [id data]
  (reagent/create-class
    {:component-did-mount #(show-line-chart id data)
     :display-name "chartjs-component"
     :reagent-render (fn [id data]
                       [:canvas {:id id :width "700" :height "380"}])}))
