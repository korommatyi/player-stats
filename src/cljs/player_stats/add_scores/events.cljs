(ns player-stats.add-scores.events
  (:require [re-frame.core :as re-frame]
            [player-stats.add-scores.db :as as-db]
            [clojure.string :as str]))

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
   (assoc-in db [:add-scores-data :known-names] ["Alice" "Bob" "Cecil" "Dean"])))

(re-frame/reg-event-db
 ::change-result
 (fn [db _]
   (let [vals [:team-a-won :team-b-won :draw :team-a-won]
         key [:add-scores-data :result]
         current-value (get-in db key)
         new-value (vals (+ 1 (.indexOf vals current-value)))]
     (assoc-in db key new-value))))

(defn- next-id [entries prefix]
  (let [ks (keys entries)
        get-num #(int (last (str/split (name %) #"-")))
        nums (map get-num ks)
        next (if (seq nums) (+ 1 (apply max nums)) 1)]
    (keyword (str (name prefix) "-" next))))

(defn- save [db team id name]
  (update-in db [:add-scores-data team] assoc id name))

(re-frame/reg-event-db
 ::add-to-team
 (fn [db [_ team name]]
   (let [db2 (update-in db [:add-scores-data :last-edited] team)
         id (next-id (get-in db2 [:add-scores-data team]) team)]
     (save db2 team id name))))

(re-frame/reg-event-db
 ::save
 (fn [db [_ team id name]]
   (let [db2 (update-in db [:add-scores-data :last-edited] nil)]
     (save db2 team id name))))

(re-frame/reg-event-db
 ::delete
 (fn [db [_ team id]]
   (update-in db [:add-scores-data team] dissoc id)))