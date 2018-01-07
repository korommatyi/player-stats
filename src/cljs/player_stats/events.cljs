(ns player-stats.events
  (:require [re-frame.core :as re-frame]
            [player-stats.db :as db]
            [cljsjs.firebase :as firebase]))

(re-frame/reg-event-db
 ::init-db
 (fn [_ _] db/default-db))

(re-frame/reg-event-fx
 ::init-firebase-connection
 (fn [_ _] {:login-and-start-listening-to-data nil}))

(re-frame/reg-fx
 :login-and-start-listening-to-data
 (fn [_]
   (let [auth (js/firebase.auth)
         provider (js/firebase.auth.GoogleAuthProvider.)]
     (.onAuthStateChanged
      auth
      (fn [user]
        (if user
          (re-frame/dispatch [::register-logged-in-user (.-email user)])
          (-> auth
              (.signInWithPopup provider)
              (.then (fn [result]
                       (let [user (.-user result)
                             email (.-email user)]
                         (re-frame/dispatch [::register-logged-in-user email])))))))))))

(re-frame/reg-event-fx
 ::register-logged-in-user
 (fn [{:keys [db]} [_ email]]
   {:db (assoc db :user-id email)
    :start-listening-to-firebase nil}))

(re-frame/reg-fx
 :start-listening-to-firebase
 (fn [_]
   (let [db (js/firebase.database)
         results (.ref db "/results")]
     (.on results "value"
          (fn [snapshot] (re-frame/dispatch [::update-raw-data (.val snapshot)]))))))

(re-frame/reg-event-db
 ::update-raw-data
 (fn [db [_ data]]
   (let [d (js->clj data :keywordize-keys true)
         v (vals d)
         raw-data (sort-by :date v)]
     (assoc db :raw-data raw-data))))

(re-frame/reg-event-db
 ::set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

(re-frame/reg-fx
 :focus
 (fn [id]
   (.focus (.getElementById js/document id))))
