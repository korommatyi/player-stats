(ns player-stats.mui-helpers
  (:require [cljsjs.material-ui]
            [cljsjs.react]
            [cljsjs.react.dom]
            [reagent-material-ui.core :as mui]
            ))

(defn icon [nme] [mui/FontIcon {:className "material-icons"} nme])
(defn color [nme] (aget mui/colors nme))
