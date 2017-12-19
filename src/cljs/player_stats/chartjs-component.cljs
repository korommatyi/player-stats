(ns player-stats.chartjs-component
  (:require [reagent.core :as reagent]
            [cljsjs.chartjs]
            ))

(defn show-line-chart
  [id data]
  (let [context (.getContext (.getElementById js/document id) "2d")
        chart-data {:type "line"
                    :data data
                    :options {:tooltips {:mode "index"}}}]
      (js/Chart. context (clj->js chart-data))))

(defn chartjs-component
  [id data]
  (reagent/create-class
    {:component-did-mount #(show-line-chart id data)
     :display-name "chartjs-component"
     :reagent-render (fn [id data]
                       [:div {:class "chart-container"
                              :style {:position "relative"
                                      :width "100vw"
                                      :margin "auto"
                                      :min-width 200
                                      :flex 1}}
                        [:canvas {:id id}]])}))
