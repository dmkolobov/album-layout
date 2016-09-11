(ns album-layout.core
  (:require [reagent.core :as reagent]
            [re-frame.core :refer [reg-sub reg-event-db subscribe dispatch dispatch-sync]]
            [album-layout.db :as db]
            [album-layout.views :as views]
            ;; Need to include 'subs' and 'events' explicitely for Google Closure Compiler.
            [album-layout.subs]
            [album-layout.events]))

(enable-console-print!)

(defn render-img
  [id {:keys [width height]}]
  [:div
   {:style {:width  "100%"
            :height "100%"
            :border "1px solid black"}}
   (str id "!")])

(def images
  {"p1.jpg" {:aspect 1.5}
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
   "p7a.jpg" {:aspect 1}})

(defn hello-world
  []
  [views/gallery :items      (subscribe [:images])
                 :render-fn render-img])

(reg-sub :images (fn [db _] (:images db)))
(reg-event-db :set-images (fn [db [_ images]] (assoc db :images images)))
(dispatch-sync [:set-images images])

(dispatch-sync [:album-layout/initialize-db])

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))