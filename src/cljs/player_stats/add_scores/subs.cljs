(ns player-stats.add-scores.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::data
 (fn [db]
   (:add-scores-data db)))

(re-frame/reg-sub
 ::team
 (fn [_ _] (re-frame/subscribe [::data]))
 (fn [data [_ party] _]
   (case party
     "a" (:team-a data)
     "b" (:team-b data)
     nil)))

(re-frame/reg-sub
 ::result
 (fn [_ _] (re-frame/subscribe [::data]))
 (fn [data _ _] (:result data)))

(re-frame/reg-sub
 ::known-names
 (fn [_ _] (re-frame/subscribe [::data]))
 (fn [data _ _] (:known-names data)))
