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

(def base-color 0x0E4E63)

(defn determine-color
  [x mask shift amt]
  (let [x (+ amt (bit-and (bit-shift-right x shift) mask))]
    (cond (> x 255) 255
          (< x 0)   0
          :default  x)))

(defn lighten-darken
  [x amt]
  (let [r   (determine-color x x 16 amt)
        b   (determine-color x 0x00ff 8 amt)
        g   (determine-color x 0x0000ff 0 amt)]
    (let [color (.toString (bit-or g
                                   (bit-shift-left b 8)
                                   (bit-shift-left r 16))
                            16)]
      (str "#"
           (reduce str (repeat (- 6 (.-length color)) "0"))
           color))))

(defn gen-color []
  (lighten-darken base-color
                  (* (if (> 0 (rand-int 2)) -1 1)
                     (rand-int 256))))

(defn gen-image [] {:aspect (gen-ratio) :color (gen-color)})

(defn generate-images [n] (for [x (range n)] [x (gen-image)]))

(reg-event-db :create-images (fn [] {:images (generate-images 64)}))

(reg-sub :images :images)

(dispatch [:create-images])

(defn render-image
  [id box {:keys [color]}]
  [:div
   {:style {:width      "100%"
            :height     "100%"
            :background color}}])

(defn color-gallery
  []
  [perfect-layout :items           (subscribe [:images])
                  :render-fn       render-image
                  :scale-increment 200])

(reagent/render-component [color-gallery]
                          (. js/document (getElementById "app")))