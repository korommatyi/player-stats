(ns player-stats.dashboard.metrics
  ;; (:require [re-frame.core :as re-frame]
  ;;           [player-stats.subs :as core-subs])
  )

(defn winners [game]
  (case (keyword (:result game))
    :team-a-won (:team-a game)
    :team-b-won (:team-b game)
    []))

(defn players [game]
  (into (:team-a game) (:team-b game)))

(defn create-windows [data window-size]
  (if (< (count data) window-size)
    [data]
    (partition window-size 1 data)))

(defn known-names [records]
  (sort (set (flatten (for [r records] (into (:team-a r) (:team-b r)))))))

(defn transpose [vec-of-dicts keys]
  (let [start (into {} (for [k keys] [k []]))
        mass-conj (fn [cum new]
                    (reduce #(let [[k new-val] %2]
                               (update %1 k conj new-val)) cum new))]
    (reduce mass-conj start vec-of-dicts)))

(defn compute-metric [metric-fn raw-data & {:keys [window-size filters series?]}]
  (let [filtered (if (seq filters) (reduce (fn [data f] (f data)) raw-data filters) raw-data)]
    (if window-size
      (if series?
        (let [names (known-names filtered)
              windows (create-windows filtered window-size)
              f #(metric-fn % names)]
          (transpose (map f windows) names))
        (let [last-window (last (create-windows filtered window-size))
              names (known-names last-window)]
          (metric-fn last-window names)))
      (metric-fn filtered (known-names filtered) :series? series?))))

(defn win-rates [dataset names & {:keys [series?]}]
  (let [start (for [n names] [n {:won 0 :played 0}])
        update (fn [cum new]
                 (let [w (set (winners new))
                       all (set (players new))]
                   (for [[n {:keys [won played]}] cum] [n {:won (if (w n) (+ 1 won) won)
                                                           :played (if (all n) (+ 1 played) played)}])))
        to-rate #(for [[n {:keys [won played]}] %] [n (if (> played 0) (/ won played) 0)])]
    (if series?
      (let [stats (drop 1 (reductions update start dataset))]
        (transpose (map to-rate stats) names))
      (let [stat (reduce update start dataset)]
        (to-rate stat)))))

(def metric-name-to-fn {:win-rate win-rates})

(defn xxx [raw-data metric & {:keys [vs window-size series?]}]
  (let [filters (if vs [#(filter (fn [game] (= vs (count (:team-a game)) (count (:team-b game)))) %)] [])
        metric-fn (metric metric-name-to-fn)]
    (compute-metric metric-fn raw-data :filters filters :series? series? :window-size window-size)))

(defn win-rate-datasets [raw-data]
  (let [series-by-name (win-rate-series-by-name raw-data)]
    (vec (for [[n series] series-by-name] {:data series
                                           :label n
                                           :fill false}))))

(def default-data {:labels ["2012" "2013" "2014" "2015" "2016"]
                   :datasets [{:data [5 10 15 20 25]
                               :label "Rev in MM"
                               :borderColor "#90EE90"
                               :backgroundColor "#90EE90"
                               :fill false}
                              {:data [3 6 9 12 15]
                               :label "Cost in MM"
                               :borderColor "#F08080"
                               :backgroundColor "#F08080"
                               :fill false}]})

(def default-params {:type "line"
                     :data default-data
                     :options {:tooltips {:mode "index"}}})

(defn metric-calculator [metric raw-data options]
  (let [data (filter-by-settings raw-data options)
        labels (for [d data] (:date d))
        out {:labels labels}]
    (case (keyword metric)
      :win-rate (assoc out :datasets (win-rate-datasets data))
      default-data)))
