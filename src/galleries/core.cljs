(ns galleries.core
  (:require [reagent.core :as reagent]
            [re-frame.core :refer [dispatch dispatch-sync]]
            [galleries.db :as db]
            [galleries.views :as views]
            ;; Need to include 'subs' and 'events' explicitely for Google Closure Compiler.
            [galleries.subs]
            [galleries.events]))

(enable-console-print!)

(defn hello-world
  []
  [views/gallery])

(dispatch-sync [:initialize-db {"p1.jpg" {:aspect 1.5}
                                "p2.jpg" {:aspect 1.5}
                                "p3.jpg" {:aspect 1.33}
                                "p4.jpg" {:aspect 0.5}
                                "p5.jpg" {:aspect 0.66}
                                "p6.jpg" {:aspect 0.66}
                                "p7.jpg" {:aspect 1}
                                "p1a.jpg" {:aspect 1.5}
                                "p2a.jpg" {:aspect 1.5}
                                "p3a.jpg" {:aspect 1.33}
                                "p4a.jpg" {:aspect 0.5}
                                "p5a.jpg" {:aspect 0.66}
                                "p6a.jpg" {:aspect 0.66}
                                "p7a.jpg" {:aspect 1}}])

(dispatch-sync [:window-resized])

(aset js/window "onresize" (fn[] (dispatch [:window-resized])))

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))