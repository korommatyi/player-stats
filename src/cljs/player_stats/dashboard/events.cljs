(ns player-stats.dashboard.events
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-event-db
 ::set-axis
 (fn [db [_ axis k v]]
   (assoc-in db [:dashboard-data axis k] v)))
