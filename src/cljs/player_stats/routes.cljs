(ns player-stats.routes
  (:require [accountant.core :as accountant]
            [re-frame.core :as re-frame]
            [player-stats.events :as events]
            ))

(defn init-routes []
  (accountant/configure-navigation!
   {:nav-handler (fn [path]
                   (case path
                     "/" (accountant/navigate! "/dashboard")
                     "/dashboard" (re-frame/dispatch [::events/set-active-panel :dashboard-panel])
                     "/add-game" (re-frame/dispatch [::events/set-active-panel :add-game-panel])))
    :path-exists? (fn [path] true)})
  (accountant/dispatch-current!))
