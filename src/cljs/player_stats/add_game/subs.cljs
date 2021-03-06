(ns player-stats.add-game.subs
  (:require [re-frame.core :as re-frame]
            [clojure.string :as str]
            [player-stats.subs :as base-subs]))

(re-frame/reg-sub
 ::data
 (fn [db]
   (:add-game-data db)))

(re-frame/reg-sub
 ::team
 (fn [_ _] (re-frame/subscribe [::data]))
 (fn [data [_ team] _]
   (team data)))

(re-frame/reg-sub
 ::result
 (fn [_ _] (re-frame/subscribe [::data]))
 (fn [data _ _] (:result data)))

(re-frame/reg-sub
 ::known-names
 (fn [_ _] (re-frame/subscribe [::base-subs/raw-data]))
 (fn [data _ _]
   (sort (set (flatten (for [r data] [(:team-a r) (:team-b r)]))))))

(re-frame/reg-sub
 ::next-id
 (fn [[_ team] _] (re-frame/subscribe [::team team]))
 (fn [entries [_ team]]
   (let [ks (keys entries)
        get-num #(int (last (str/split (name %) #"-")))
        nums (map get-num ks)
         next (if (seq nums) (+ 1 (apply max nums)) 1)]
     (keyword (str (name team) "-" next)))))

(re-frame/reg-sub
 ::focus?
 (fn [_ _] (re-frame/subscribe [::data]))
 (fn [data [_ team]] (= (:last-edited data) team)))

(re-frame/reg-sub
 ::date
 (fn [_ _] (re-frame/subscribe [::data]))
 (fn [data _] (:date data)))

(re-frame/reg-sub
 ::valid?
 (fn [_ _]
   {:team-a (re-frame/subscribe [::team :team-a])
    :team-b (re-frame/subscribe [::team :team-b])
    :date (re-frame/subscribe [::date])})
 (fn [data _]
   (let [a (:team-a data)
         b (:team-b data)
         d (:date data)]
     (and (seq a) (seq b) (boolean d)))))
