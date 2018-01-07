(ns player-stats.dashboard.subs
  (:require [re-frame.core :as re-frame]
            [player-stats.subs :as core-subs]
            [player-stats.dashboard.metrics :as metrics]))

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

(re-frame/reg-sub
 ::chart-data
 (fn [_ _]
   {:dashboard-data (re-frame/subscribe [::data])
    :raw-data (re-frame/subscribe [::core-subs/raw-data])})
 (fn [{:keys [dashboard-data raw-data]} _ _]
   (metrics/chart-data (:x-setter dashboard-data)
                       (:y-setter dashboard-data)
                       raw-data)))
