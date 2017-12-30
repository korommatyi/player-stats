(ns player-stats.add-scores.events
  (:require [re-frame.core :as re-frame]
            [player-stats.add-scores.db :as as-db]
            [clojure.string :as str]
            [cljsjs.firebase :as fb]))

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

(defn- save-player [db team id name]
  (assoc-in db [:add-scores-data team id] name))

(defn- set-last-edited [db team]
  (update db :add-scores-data assoc :last-edited team))

(re-frame/reg-event-fx
 ::add-to-team
 (fn [cfx [_ team n]]
   (let [db (:db cfx)
         db2 (set-last-edited db team)
         id (next-id (get-in db2 [:add-scores-data team]) team)]
     {:db (save-player db2 team id n)
      :focus (str "new-input-" (name team))})))

(re-frame/reg-event-db
 ::save-edited-player
 (fn [db [_ team id name]]
   (let [db2 (set-last-edited db team)]
     (save-player db2 team id name))))

(re-frame/reg-event-db
 ::delete
 (fn [db [_ team id]]
   (update-in db [:add-scores-data team] dissoc id)))

(re-frame/reg-event-db
 ::set-date
 (fn [db [_ date]]
   (assoc-in db [:add-scores-data :date] date)))

(re-frame/reg-fx
 :focus
 (fn [id]
   (.focus (.getElementById js/document id))))

(defn- normalize-data [data]
  (let [normalize-team #(sort (vals %))
        get-day (fn [d]
                  (let [day (.getDate d)]
                    (if (> day 9) (str day) (str "0" day))))
        normalize-date (fn [d] (str (.getFullYear d) "/" (+ 1 (.getMonth d)) "/" (get-day d)))]
    {:team-a (normalize-team (:team-a data))
     :team-b (normalize-team (:team-b data))
     :result (:result data)
     :date (normalize-date (:date data))}))

(re-frame/reg-fx
 :save-to-fb
 (fn [data]
   (let [fb-db (js/firebase.database)
         d (normalize-data data)
         k (hash d)
         v (clj->js d)]
     (.set (.ref fb-db (str "results/" k)) v))))

(re-frame/reg-event-fx
 ::save
 (fn [cfx _]
   (let [db (:db cfx)
         old-data (:add-scores-data db)]
     {:db (assoc db :add-scores-data as-db/add-scores-db)
      :save-to-fb old-data})))
