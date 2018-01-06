(ns player-stats.dashboard.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [reagent-material-ui.core :as mui]
            [player-stats.mui-helpers :as muih]
            [player-stats.dashboard.subs :as subs]
            [player-stats.dashboard.events :as events]
            [cljsjs.material-ui]
            [cljsjs.react]
            [cljsjs.react.dom]
            [cljsjs.chartjs]
            ))

(defn flex-style [dir & {:or {} :as other}]
  (let [s {:display "flex" :flex-direction dir :justify-content "space-between"}]
    {:style (merge s other)}))

(defn show-chart
  [id params]
  (let [context (.getContext (.getElementById js/document id) "2d")
        chart-data params]
      (js/Chart. context (clj->js chart-data))))

(defn chartjs-component
  [id data]
  (reagent/create-class
    {:component-did-mount #(show-chart id data)
     :display-name "chartjs-component"
     :reagent-render (fn [id data]
                       [:div {:class "chart-container"
                              :style {:position "relative"
                                      :width "100vw"
                                      :margin "auto"
                                      :min-width 200
                                      :flex 1}}
                        [:canvas {:id id}]])}))


(defn dashboard []
  (let [data @(re-frame/subscribe [::subs/chart-data])]
    [chartjs-component "hello" data]))

(defn num-field [{:keys [default]}]
  (let [val (reagent/atom (str default))
        valid? #(re-matches #"\d+" %)]
    (fn [{:keys [on-save id disabled max-width]}]
      [mui/TextField {:style (if max-width {:maxWidth max-width})
                      :fullWidth (not max-width)
                      :id id
                      :value @val
                      :onChange (fn [_ new-val]
                                  (reset! val new-val)
                                  (if (valid? new-val)
                                    (on-save (js/Number. new-val))))
                      :errorText (if (not (or (valid? @val) disabled))
                                   "Very funny... Now enter a valid integer." "")
                      :disabled disabled
                      }])))

(defn vs-toggle [{:keys [on-toggle toggle-state on-save id vs-subs]}]
  [:div (flex-style "row" :align-items "center" :justify-content "flex-start")
   [mui/Toggle {:label "Just"
                :labelPosition "right"
                :toggled toggle-state
                :onToggle #(on-toggle  %2)
                :style {:max-width "5em" :margin-right "1em"}}]
   [num-field {:default vs-subs :id id :max-width "8em" :disabled (not toggle-state)
               :on-save on-save}]
   [:label {:style {:min-width "8em" :margin-left "1em"}} (str "vs " vs-subs " games.")]])

(defn window-toggle [{:keys [on-toggle toggle-state on-save id default]}]
  [:div (flex-style "row" :align-items "center" :justify-content "flex-start")
   [mui/Toggle {:label "Use a sliding window of size"
                :labelPosition "right"
                :toggled toggle-state
                :onToggle #(on-toggle  %2)
                :style {:margin-right "1em"
                        :max-width "16em"}}]
   [num-field {:default default :id id :max-width "8em" :disabled (not toggle-state)
               :on-save on-save}]])

(defn settings [axis]
  (let [vs-subs @(re-frame/subscribe [::subs/axis-setting axis :vs])]
    [:div
     [window-toggle {:on-toggle #(re-frame/dispatch [::events/set-axis axis :window? %])
                     :toggle-state @(re-frame/subscribe [::subs/axis-setting axis :window?])
                     :on-save #(re-frame/dispatch [::events/set-axis axis :window-size %])
                     :id (str axis "-length")
                     :default @(re-frame/subscribe [::subs/axis-setting axis :window-size])}]
     [vs-toggle {:on-toggle #(re-frame/dispatch [::events/set-axis axis :only-vs? %])
                 :toggle-state @(re-frame/subscribe [::subs/axis-setting axis :only-vs?])
                 :on-save #(re-frame/dispatch [::events/set-axis axis :vs %])
                 :id (str axis "-vs")
                 :vs-subs vs-subs}]]))
  
(defn metric-selector [options axis]
  (let [val @(re-frame/subscribe [::subs/axis-setting axis :metric])]
    [mui/SelectField {:value val
                      :onChange #(re-frame/dispatch [::events/set-axis axis :metric (keyword %3)])}
     (for [[k v] options]
       ^{:key (str k v axis)} [mui/MenuItem {:value k :primaryText v}])]))


(defn axis-setter [axis title metrics]
  (let [metric @(re-frame/subscribe [::subs/axis-setting axis :metric])]
    [mui/Card {:style {:margin-top "2%"}}
     [mui/CardHeader {:title title
                      :avatar (muih/el (muih/icon "tune"))}]
     [mui/CardText
      [metric-selector metrics axis]
      (if (not (= metric :time)) [settings axis])]]))

(defn dashboard-panel []
  [:div (flex-style "row" :margin "2% auto")
   [dashboard]
   [:div (flex-style "column" :margin "0 2%" :justify-content "flex-start")
    [axis-setter :y-setter "Y-axis" [[:elo-point "Élő point"] [:win-rate "Win rate"]]]
    [axis-setter :x-setter "X-axis" [[:time "Time"] [:elo-point "Élő point"] [:win-rate "Win rate"]]]]])
