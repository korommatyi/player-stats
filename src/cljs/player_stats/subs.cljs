(ns player-stats.subs
  (:require [re-frame.core :as re-frame]
            [clojure.string :as str]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::active-panel
 (fn [db _]
   (:active-panel db)))

(re-frame/reg-sub
 ::active-panel-human-friendly
 (fn [query-v _]
    (re-frame/subscribe [::active-panel]))
 (fn [panel-keyword query-v _]
   (let [raw (name panel-keyword)
         wo-panel (subs raw 0 (- (count raw) 6))]
     (str/join " " (map str/capitalize (str/split wo-panel #"-"))))))
