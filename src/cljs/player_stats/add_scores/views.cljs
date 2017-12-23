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

(defn input [{:keys [name on-save on-stop]}]
  (let [val (reagent/atom name)
        stop #(do (reset! val "")
                  (when on-stop (on-stop)))
        save #(let [v (-> @val str str/trim)]
                (when (seq v) (on-save v))
                (stop))]
    (fn [{:keys [known-names focus id]}]
      [mui/AutoComplete {:id id
                         :hintText "Enter a name"
                         :searchText @val
                         :dataSource known-names
                         :filter (fn [st key]
                                   (str/starts-with? (str/lower-case key) (str/lower-case st)))
                         :onUpdateInput (fn [search-text _ p]
                                          (let [source (-> p
                                                           (js->clj :keywordize-keys true)
                                                           :source)]
                                            (reset! val search-text)
                                            (if (= source "click") (save))))
                         :onKeyDown #(case (.-which %)
                                       13 (save)
                                       27 (stop)
                                       nil)
                         :autoFocus focus}])))

(defn name-item [{:keys [team]}]
  (let [editing (reagent/atom false)]
    (fn [{:keys [id name known-names]}]
      [mui/ListItem {:primaryText name
                     :rightIconButton (muih/el [mui/IconButton
                                          {:tooltip "Delete"
                                           :onClick #(re-frame/dispatch [::events/delete team id])}
                                          [muih/icon "delete"]])
                     :onClick #(reset! editing true)}
       (when @editing
         [input {:name name :known-names known-names :focus true
                 :on-save #(re-frame/dispatch [::events/save team id %])
                 :on-stop #(reset! editing false)
                 :id id
                 }])])))

(defn name-entry [team]
  (let [known-names @(re-frame/subscribe [::subs/known-names])
        focus? @(re-frame/subscribe [::subs/focus? team])]
    [input {:id (str "new-input-" (name team))
            :on-save #(re-frame/dispatch [::events/add-to-team team %])
            :known-names known-names
            :focus focus?}]))

(defn team [t]
  (let [entries @(re-frame/subscribe [::subs/team t])
        known-names @(re-frame/subscribe [::subs/known-names])]
    [:div
     [name-entry t]
     [mui/List
      (for [[id name] entries]
        ^{:key id} [name-item {:name name :id id :known-names known-names :team t}])]]))

(defn result-indicator [r]
  [:div {:style {:display "flex" :flex-direction "column" :justify-content "center"}}
   [mui/IconButton {:tooltip "Change direction"
                    :onClick #(re-frame/dispatch [::events/change-result])}
    (case @(re-frame/subscribe [::subs/result])
      :team-a-won [muih/icon "keyboard_arrow_right"]
      :team-b-won [muih/icon "keyboard_arrow_left"]
      [muih/icon "drag_handle"])]])

(defn add-scores-panel []
  [:div {:style {:display "flex" :flex-flow "row wrap" :justify-content "space-around"}}
   [:div
    [:h1 "The awesomes:"]
     [team :team-a]]
   [result-indicator]
   [:div
    [:h1 "The geniuses:"]
    [team :team-b]]])
