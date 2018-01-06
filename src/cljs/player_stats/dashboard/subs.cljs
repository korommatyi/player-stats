(ns player-stats.dashboard.subs
  (:require [re-frame.core :as re-frame]
            [player-stats.subs :as core-subs]))

(re-frame/reg-sub
 ::data
 (fn [db] (:dashboard-data db)))

(re-frame/reg-sub
 ::game-frequency
 (fn [_ _] (re-frame/subscribe [::core-subs/raw-data]))
 (fn [d _ _]
   (let [all (count d)
         freq (frequencies (flatten (for [r d] [(:team-a r) (:team-b r)])))]
     (for [[k v] freq] [k (/ v all)]))))

(re-frame/reg-sub
 ::axis
 (fn [_ _] (re-frame/subscribe [::data]))
 (fn [d [_ axis] _] (axis d)))

(re-frame/reg-sub
 ::axis-setting
 (fn [[_ axis _] _] (re-frame/subscribe [::axis axis]))
 (fn [d [_ _ s] _] (s d)))

(def default-data {:labels ["2012" "2013" "2014" "2015" "2016"]
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

(def default-params {:type "line"
                     :data default-data
                     :options {:tooltips {:mode "index"}}})

(re-frame/reg-sub
 ::chart-data
 (fn [_ _]
   {:dashboard-data (re-frame/subscribe [::data])
    :raw-data (re-frame/subscribe [::core-subs/raw-data])})
 (fn [{:keys [dashboard-data raw-data]} _ _]
   default-params))
