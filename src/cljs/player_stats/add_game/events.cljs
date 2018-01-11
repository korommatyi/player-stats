(ns player-stats.add-game.events
  (:require [re-frame.core :as re-frame]
            [player-stats.add-game.db :as ag-db]
            [clojure.string :as str]
            [cljsjs.firebase :as fb]))

(re-frame/reg-event-db
 ::change-result
 (fn [db _]
   (let [vals [:team-a-won :team-b-won :draw :team-a-won]
         key [:add-game-data :result]
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
  (assoc-in db [:add-game-data team id] name))

(defn- set-last-edited [db team]
  (update db :add-game-data assoc :last-edited team))

(re-frame/reg-event-fx
 ::add-to-team
 (fn [cfx [_ team n]]
   (let [db (:db cfx)
         db2 (set-last-edited db team)
         id (next-id (get-in db2 [:add-game-data team]) team)]
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
   (update-in db [:add-game-data team] dissoc id)))

(re-frame/reg-event-db
 ::set-date
 (fn [db [_ date]]
   (assoc-in db [:add-game-data :date] date)))

(defn- normalize-data [data]
  (let [normalize-team #(sort (vals %))
        str-pad (fn [d]
                  (if (> d 9) (str d) (str "0" d)))
        get-day (fn [d]
                  (let [day (.getDate d)]
                    (str-pad day)))
        get-month (fn [d]
                    (let [month (+ 1 (.getMonth d))]
                      (str-pad month)))
        normalize-date (fn [d] (str (.getFullYear d) "/" (get-month d) "/" (get-day d)))]
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
         old-data (:add-game-data db)]
     {:db (assoc db :add-game-data ag-db/add-game-db)
      :save-to-fb old-data})))
