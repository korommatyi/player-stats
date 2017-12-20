(ns player-stats.add-scores.events
  (:require [re-frame.core :as re-frame]
            [player-stats.add-scores.db :as as-db]))

(re-frame/reg-event-fx
 ::init-if-needed
 (fn [cfx _]
   (let [db (:db cfx)
         needs-init? (not (contains? db :add-scores-data))]
     (if needs-init?
       {:db (assoc db :add-scores-data as-db/add-scores-db)
        :dispatch [::init-known-names]}
       {:db db}))))

(re-frame/reg-event-db
 ::init-known-names
 (fn [db _]
   (assoc-in db [:add-scores-data :known-names] ["Aaaa" "Bbbb" "Cccc"])))
