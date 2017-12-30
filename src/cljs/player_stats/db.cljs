(ns player-stats.db
  (:require [player-stats.add-scores.db :as as-db]))

(def default-db
  {:active-panel :dashboard-panel
   :raw-data []
   :add-scores-data as-db/add-scores-db})
