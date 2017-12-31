(ns player-stats.db
  (:require [player-stats.add-game.db :as ag-db]))

(def default-db
  {:active-panel :dashboard-panel
   :raw-data []
   :add-game-data ag-db/add-game-db})
