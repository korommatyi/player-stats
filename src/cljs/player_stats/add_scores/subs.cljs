(ns player-stats.add-scores.subs
  (:require [re-frame.core :as re-frame]
            [clojure.string :as cstr]))

(re-frame/reg-sub
 ::data
 (fn [db]
   (:add-scores-data db)))

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
 (fn [_ _] (re-frame/subscribe [::data]))
 (fn [data _ _] (:known-names data)))

(re-frame/reg-sub
 ::next-id
 (fn [[_ team] _] (re-frame/subscribe [::team team]))
 (fn [entries [_ team]]
   (let [ks (keys entries)
        get-num #(int (last (cstr/split (name %) #"-")))
        nums (map get-num ks)
         next (if (seq nums) (+ 1 (apply max nums)) 1)]
     (println team " " next)
     (keyword (str (name team) "-" next)))))

(re-frame/reg-sub
 ::focus?
 (fn [_ _] (re-frame/subscribe [::data]))
 (fn [data [_ team]] (= (:last-edited data) team)))
