(ns album-layout.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]
            [album-layout.util :refer [selector]]))

(reg-sub :album-layout/window (fn [db [_ gallery-id]] (get-in db [:album-layout/containers gallery-id])))
(reg-sub :album-layout/window-base (fn [db [_ gallery-id]] (get-in db [:album-layout/containers gallery-id :base-box])))

(defn item-aspect [[id {:keys [aspect]}]] aspect)

(defn sum-aspects [sum [_ {:keys [aspect]}]] (+ sum aspect))

(defn calc-aspect-sum [items] (reduce sum-aspects 0 items))

(defn compute-rows
  "Given the window dimensions and a sequence of item aspect ratios,
  return the ideal number of rows for the gallery layout."
  [{:keys [width height]} items]
  (.round js/Math
          (/ (* (/ height 2)
                (calc-aspect-sum items))
             width)))

(defn aspect-weight [a] (* a 100))

(defn compute-partitions
  "Given a sequence of item aspects and the number of rows, return
  a sequence of rows, each of which is a sequence of item aspects
  whose sum is as equal as possible to the sums of other rows."
  [aspects num-rows]
  (js->clj
    (js/lpartition (clj->js (map aspect-weight aspects))
                   num-rows)))

(defn item-weight [i] (* 100 (item-aspect i)))

(defn compute-layout
  "Given a sequence of items and the window dimensions, return a sequence of
  sequences containing item entries laid out according to the partition
  algorithm."
  [items window-base]
  (let [aspects  (map item-aspect items)
        num-rows (compute-rows window-base items)]
    (map (partial map (selector item-weight items))
         (compute-partitions aspects num-rows))))

(reg-sub
  :album-layout/layout
  (fn [[_ items]]
    [items
     (subscribe [:album-layout/window-base (hash items)])])
  (fn [[items window-base] _]
    (when window-base
      (compute-layout items window-base))))

(defn item-scale-pair [items] [(calc-aspect-sum items) items])

(reg-sub
  :album-layout/summed-layout
  (fn [[_ items]] (subscribe [:album-layout/layout items]))
  (fn [layout] (map item-scale-pair layout)))

(defn scale-layout
  "Return a layout which contains explicit dimensions for items."
  [{:keys [box] :as window} layout]
  (let [width (:width box)]
    (map (fn [[aspect-sum items]]
           (map (fn [[id {:keys [aspect] :as data}]]
                  (let [height (/ width aspect-sum)]
                    [id (assoc data
                          :width  (* aspect height)
                          :height height)]))
                items))
         layout)))

(reg-sub
  :album-layout/scaled-layout
  (fn [[_ items]]
    [(subscribe [:album-layout/window (hash items)])
     (subscribe [:album-layout/summed-layout items])])
  (fn [[window layout] _]
    (scale-layout window layout)))