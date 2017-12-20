(ns player-stats.events
  (:require [re-frame.core :as re-frame]
            [player-stats.db :as db]
            [player-stats.add-scores.events :as as-events]))

(re-frame/reg-event-db
 ::initialize-db
 (fn  [_ _]
   db/default-db))

(defn init-panel [panel-name]
  (case panel-name
    :add-scores-panel [::as-events/init-if-needed]
    [::do-nothing]))

(re-frame/reg-event-fx
 ::set-active-panel
 (fn [cfx [_ active-panel]]
   {:db (assoc (:db cfx) :active-panel active-panel)
    :dispatch (init-panel active-panel)}))

(re-frame/reg-event-db
 ::do-nothing
 (fn [db _] db))
