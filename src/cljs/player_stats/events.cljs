(ns player-stats.events
  (:require [re-frame.core :as re-frame]
            [player-stats.db :as db]
            [player-stats.add-scores.events :as as-events]
            [cljsjs.firebase :as firebase]))

(re-frame/reg-event-fx
 ::initialize
 (fn  [_ _]
   {:db db/default-db
    ::login nil}))

(defn init-panel [panel-name]
  (case panel-name
    :add-scores-panel [::as-events/init-if-needed]
    [::do-nothing]))

(re-frame/reg-event-fx
 ::set-active-panel
 (fn [cfx [_ active-panel]]
   {:db (assoc (:db cfx) :active-panel active-panel)
    :dispatch (init-panel active-panel)}))

(re-frame/reg-event-db
 ::do-nothing
 (fn [db _] db))

(def provider (js/firebase.auth.GoogleAuthProvider.))

(re-frame/reg-fx
 ::login
 (fn [_]
   (let [auth (js/firebase.auth)
         current-user (.-currentUser auth)]
     (if current-user
       (re-frame/dispatch [::login-user (.-email current-user)])
       (-> auth
           (.signInWithPopup provider)
           (.then (fn [result]
                    (let [user (.-user result)
                          email (.-email user)]
                      (re-frame/dispatch [::login-user email])))))))))

(re-frame/reg-event-db
 ::login-user
 (fn [db [_ email]]
   (assoc db :user-id email)))
