(ns album-layout.core
  (:require [goog.events]
            [reagent.core :as reagent]
            [re-frame.core :refer [reg-sub reg-event-db subscribe dispatch dispatch-sync]]
            [album-layout.db :as db]
            [album-layout.views :as views]
            ;; Need to include 'subs' and 'events' explicitely for Google Closure Compiler.
            [album-layout.subs]
            [album-layout.events]
            [album-layout.util :refer [node-dimensions]]))

(enable-console-print!)

(defn resize-handler
  [layout-id node scale-increment]
  (fn []
    (dispatch [:album-layout/container-resized layout-id (node-dimensions node) scale-increment])))

(defn perfect-layout
  [& {:keys [items render-fn scale-increment]
      :or {scale-increment 100}}]
  (let [layout-id (hash items)
        layout    (subscribe [:album-layout/scaled-layout items])]
    (reagent/create-class
      {:component-did-mount
       (fn [owner]
         (let [node (reagent/dom-node owner)
               on-resize! (resize-handler layout-id node scale-increment)]
           (aset js/window "onresize" on-resize!)
           (.listen goog.events
                    js/window
                    (.-RESIZE (.-EventType goog.events))
                    on-resize!)
           (on-resize!)))
       :reagent-render
       (fn []
         [views/gallery :layout    layout
                        :render-fn render-fn])})))

(defn render-img
  [id data]
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
  [perfect-layout :items           (subscribe [:images])
                  :render-fn       render-img
                  :scale-increment 100])

(reg-sub :images (fn [db _] (:images db)))
(reg-event-db :set-images (fn [db [_ images]] (assoc db :images images)))
(dispatch-sync [:set-images images])

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))