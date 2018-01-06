(ns player-stats.dashboard.db)

(def dashboard-db
  {:y-setter {:only-vs? false
              :vs 3
              :metric :elo-point
              :window? false
              :window-size 5}
   :x-setter {:only-vs? false
              :vs 3
              :metric :time
              :window? false
              :window-size 5}})
