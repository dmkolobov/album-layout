(ns color-gallery.core
  (:require [album-layout.core :refer [perfect-layout]]
            [reagent.core :as reagent :refer [atom]]
            [re-frame.core :refer [reg-sub reg-event-db dispatch subscribe]]))

(enable-console-print!)

(def common-ratios
  [1
   (/ 4 3)
   (/ 3 4)
   (/ 3 2)
   (/ 2 3)
   (/ 16 9)
   (/ 9 16)
   (/ 5 3)])

(defn gen-ratio [] (rand-nth common-ratios))

(def colors
  ["#444"
   "#333"
   "#222"])

(defn gen-color [] (rand-nth colors))

(defn gen-image [] {:aspect (gen-ratio) :color (gen-color)})

(defn generate-images [n] (for [x (range n)] [x (gen-image)]))

(reg-event-db :create-images (fn [] {:images (generate-images 64)}))

(reg-sub :images :images)

(dispatch [:create-images])

(defn render-image
  [id {:keys [color]}]
  [:div
   {:style {:width      "100%"
            :height     "100%"
            :background color}}
   id])

(defn color-gallery
  []
  [perfect-layout :items           (subscribe [:images])
                  :render-fn       render-image
                  :scale-increment 100])

(reagent/render-component [color-gallery]
                          (. js/document (getElementById "app")))