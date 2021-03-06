(ns player-stats.dashboard.metrics
  ;; (:require [re-frame.core :as re-frame]
  ;;           [player-stats.subs :as core-subs])
  )

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

(defn vs-filter [raw-data vs]
  (filter (fn [game] (= vs (count (:team-a game)) (count (:team-b game)))) raw-data))

(defn compute-metric [metric-fn raw-data & {:keys [window-size vs series?]}]
  (let [filtered (if vs (vs-filter raw-data vs) raw-data)]
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
                 (let [gain-a (case (keyword (:result new))
                                :team-a-won 1
                                :team-b-won 0
                                0.5)
                       gain-b (- 1 gain-a)
                       gain (into (zipmap (:team-a new) (repeat gain-a)) (zipmap (:team-b new) (repeat gain-b)))]
                   (for [[n {:keys [won played] :as old}] cum] [n (if (contains? gain n)
                                                            {:won (+ (gain n) won) :played (+ 1 played)}
                                                            old)])))
        to-rate #(for [[n {:keys [won played]}] %] [n (if (> played 0) (/ won played) 0)])]
    (if series?
      (let [stats (drop 1 (reductions update start dataset))]
        (transpose (map to-rate stats) names))
      (let [stat (reduce update start dataset)]
        (to-rate stat)))))

(defn team-rating [player-ratings team]
  (apply + (for [n team] (player-ratings n))))

(defn elo-rating [dataset names & {:keys [series?]}]
  (let [start (into {} (for [n names] [n 1500]))
        update (fn [cum new]
                 (let [team-a (:team-a new)
                       team-b (:team-b new)
                       avg-team-size (/ (+ (count team-a) (count team-b)) 2)
                       team-a-rating (team-rating cum team-a)
                       team-b-rating (team-rating cum team-b)
                       e-a (/ 1 (+ 1 (Math/pow 10 (/ (- team-b-rating team-a-rating) 400))))
                       e-b (- 1 e-a)
                       s-a (case (keyword (:result new))
                             :team-a-won 1
                             :team-b-won 0
                             0.5)
                       s-b (- 1 s-a)
                       k (* 32 avg-team-size)
                       a-gain (/ (* k (- s-a e-a)) (count team-a))
                       b-gain (/ (* k (- s-b e-b)) (count team-b))
                       a-updated (reduce #(update %1 %2 + a-gain) cum team-a)]
                   (reduce #(update %1 %2 + b-gain) a-updated team-b)))]
    (if series?
      (let [ratings (drop 1 (reductions update start dataset))]
        (transpose ratings names))
      (reduce update start dataset))))

(def metric-name-to-fn {:win-rate win-rates
                        :elo-point elo-rating})

(defn metric-data [metric raw-data & {:keys [window-size vs series?]}]
  (let [metric-fn (metric metric-name-to-fn)
        data (compute-metric metric-fn raw-data
                             :window-size window-size
                             :vs vs :series? series?)]
    (sort-by :label (for [[n d] data] {:data d :label n :fill false}))))

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

(def default-params {:type :line
                     :data default-data
                     :options {:tooltips {:mode "index"}}})

(def linechart-options
  {:tooltips {:mode "index"
              :itemSort (fn [a b _] (- (.-yLabel b) (.-yLabel a)))}})

(def bubblechart-options
  {:showlines false
   :tooltips {:callbacks {:label (fn [_ d]
                                   (str (:label d)))}}})

(defn labels [raw-data & {:keys [window-size vs]}]
  (let [vs-filtered (if vs (vs-filter raw-data vs) raw-data)
        dates (for [r vs-filtered] (:date r))]
    (if window-size
      (map last (create-windows dates window-size))
      dates)))

(def colors ["aquamarine" "blanchedalmond" "blue" "blueviolet" "brown"
             "cadetblue" "chocolate" "cornflowerblue" "crimson" "cyan" "gold" "orange"])

(defn add-color [datasets]
  (map (fn [dataset color] (into dataset {:borderColor color :backgroundColor color})) datasets colors))

(defn bubble-plot-data [x-data y-data]
  (let [names (clojure.set/intersection (set (for [d x-data] (:label d)))
                                        (set (for [d y-data] (:label d))))
        x-common (filter #(names (:label %)) x-data)
        y-common (filter #(names (:label %)) y-data)]
    (map (fn [x y] {:label (:label x) :data [{:x (:data x) :y (:data y) :r 5}]}) x-common y-common)))

(defn chart-data [metric-x metric-y raw-data]
  (let [f (fn [metric-opts raw-data series?]
            (let [{:keys [window? window-size only-vs? vs metric]} metric-opts]
              (metric-data metric raw-data
                           :window-size (if window? window-size)
                           :vs (if only-vs? vs)
                           :series? series?)))]
    (if (= :time (:metric metric-x))
      (let [datasets (add-color (f metric-y raw-data true))
            {:keys [window? window-size only-vs? vs]} metric-y
            labels (labels raw-data :window-size (if window? window-size)
                           :vs (if only-vs? vs))]
        {:type :line :data {:labels labels :datasets datasets} :options linechart-options})
      (let [x-data (f metric-x raw-data false)
            y-data (f metric-y raw-data false)
            datasets (add-color (bubble-plot-data x-data y-data))]
        {:type :bubble :data {:datasets datasets}}))))
