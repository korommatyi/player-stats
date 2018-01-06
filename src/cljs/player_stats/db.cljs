(ns player-stats.db
  (:require [player-stats.add-game.db :as ag-db]
            [player-stats.dashboard.db :as dash]))

(def default-db
  {:active-panel :dashboard-panel
   :raw-data []
   :add-game-data ag-db/add-game-db
   :dashboard-data dash/dashboard-db})
