(ns player-stats.add-scores.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [reagent-material-ui.core :as mui]
            [cljsjs.material-ui]
            [cljsjs.react]
            [cljsjs.react.dom]
            [player-stats.add-scores.subs :as subs]
            [player-stats.mui-helpers :as muih]
            [player-stats.add-scores.events :as events]
            [clojure.string :as str]
            ))

;; (defn todo-input [{:keys [title on-save on-stop]}]
;;   (let [val  (reagent/atom title)
;;         stop #(do (reset! val "")
;;                   (when on-stop (on-stop)))
;;         save #(let [v (-> @val str str/trim)]
;;                 (when (seq v) (on-save v))
;;                 (stop))]
;;     (fn [props]
;;       [:input (merge props
;;                      {:type        "text"
;;                       :value       @val
;;                       :auto-focus  true
;;                       :on-blur     save
;;                       :on-change   #(reset! val (-> % .-target .-value))
;;                       :on-key-down #(case (.-which %)
;;                                       13 (save)
;;                                       27 (stop)
;;                                       nil)})])))

(defn input [{:keys [name known-names]}]
  (let [val (reagent/atom name)]
    (fn [props]
      [mui/AutoComplete {:hintText "Enter name"
                         :searchText @val
                         :dataSource known-names
                         :filter (fn [search-text key]
                                   (str/starts-with? (str/lower-case key) (str/lower-case search-text)))
                         :onUpdateInput (fn [search-text _ _] (reset! val search-text) (println search-text))
                         }])))

(defn team [t]
  [input {:name "" :known-names ["Alice", "Bob", "Cecil"]}])

(defn result-indicator [r]
  [mui/IconButton {:tooltip "Change direction"
                   :onClick #(re-frame/dispatch [::events/change-result])}
   (case @(re-frame/subscribe [::subs/result])
     :team-a-won [muih/icon "keyboard_arrow_right"]
     :team-b-won [muih/icon "keyboard_arrow_left"]
     [muih/icon "drag_handle"])])

(defn add-scores-panel []
  [:div {:style {:display "flex" :flex-flow "row wrap" :justify-content "space-around"}}
   [team :a]
   [result-indicator]
   [team :b]])
