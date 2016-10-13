(ns album-layout.core
  (:require [goog.events]
            [reagent.core :as reagent]
            [re-frame.core :refer [reg-sub reg-event-db subscribe dispatch dispatch-sync]]
            [album-layout.views :as views]
            ;; Need to include 'subs' and 'events' explicitely for Google Closure Compiler.
            [album-layout.subs]
            [album-layout.events]
            [album-layout.util :refer [node-dimensions]]
            [bundle]))

(enable-console-print!)

(defn resize-handler
  [layout-id node scale-increment]
  #(dispatch [:album-layout/container-resized layout-id (node-dimensions node) scale-increment]))

(defn perfect-layout
  [& {:keys [items
             gallery-fn
             item-fn
             scale-increment]
      :or {scale-increment 100}}]
  (let [layout-id (hash items)
        layout    (subscribe [:album-layout/scaled-layout items])]
    (reagent/create-class
      {:component-did-mount
       (fn [owner]
         (let [node       (reagent/dom-node owner)
               on-resize! (resize-handler layout-id node scale-increment)]
           (.listen goog.events
                    js/window
                    (.-RESIZE (.-EventType goog.events))
                    on-resize!)
           (on-resize!)))
       :reagent-render
       (fn []
         [:div
          {:style {:width "100%"}}
          [gallery-fn
          (views/gallery :render-fn item-fn
                         :layout    layout)]])})))