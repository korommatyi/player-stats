(defproject player-stats "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.238"]
                 [reagent "0.8.1" :exclusions [cljsjs/react-dom cljsjs/react]]
                 [re-frame "0.10.5"]
                 [venantius/accountant "0.2.4"]
                 [reagent-material-ui "0.2.5"]
                 [cljsjs/firebase "5.0.4-1"]
                 [cljsjs/react-chartjs-2 "2.7.0-0" :exclusions [cljsjs/react]]
                 [cljsjs/chartjs "2.7.0-0"]]

  :plugins [[lein-cljsbuild "1.1.5"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :figwheel {:css-dirs ["resources/public/css"]}

  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.9.4"]
                   [figwheel-sidecar "0.5.13"]
                   [com.cemerick/piggieback "0.2.2"]]

    :plugins      [[lein-figwheel "0.5.13"]]}}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "player-stats.core/mount-root"}
     :compiler     {:main                 player-stats.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true
                    :preloads             [devtools.preload]
                    :external-config      {:devtools/config {:features-to-install :all}}
                    }}

    {:id           "min"
     :source-paths ["src/cljs"]
     :compiler     {:main            player-stats.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}


    ]}

  )
