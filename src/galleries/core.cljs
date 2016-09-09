(ns galleries.core
  (:require [reagent.core :as reagent]
            [re-frame.core :refer [dispatch dispatch-sync]]
            [galleries.db :as db]
            [galleries.subs :as subs]
            [galleries.events :as events]
            [galleries.views :as views]))

(enable-console-print!)

(defn hello-world
  []
  [views/gallery])

(dispatch-sync [:initialize-db {"foo.jpg" 0
                                "bar.jpg" 1
                                "car.jpg" 2}])

(dispatch [:window-resized])

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))