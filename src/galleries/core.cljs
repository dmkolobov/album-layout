(ns galleries.core
  (:require [reagent.core :as reagent]
            [re-frame.core :refer [dispatch-sync]]
            [galleries.db :as db]
            [galleries.subs :as subs]
            [galleries.events :as events]
            [galleries.views :as views]))

(enable-console-print!)

(defn hello-world
  []
  [views/gallery])

(dispatch-sync [:initialize-db])
(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))