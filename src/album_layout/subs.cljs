(ns album-layout.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]
            [album-layout.util :refer [selector mk-rect]]))

(reg-sub :album-layout/window (fn [db [_ gallery-id]] (get-in db [:album-layout/containers gallery-id])))
(reg-sub :album-layout/window-base (fn [db [_ gallery-id]] (get-in db [:album-layout/containers gallery-id :base-box])))

(defn item-aspect [[id {:keys [aspect]}]] aspect)

(defn compute-rows
  "Given the window dimensions and a sequence of item aspect ratios,
  return the ideal number of rows for the gallery layout."
  [{:keys [width height]} aspects]
  (.round js/Math
          (/ (* (/ height 2) (reduce + aspects))
             width)))

(defn aspect-weight [a] (* a 100))

(defn compute-partitions
  "Given a sequence of item aspects and the number of rows, return
  a sequence of rows, each of which is a sequence of item aspects
  whose sum is as equal as possible to the sums of other rows."
  [aspects num-rows]
  (js/lpartition (clj->js (map aspect-weight aspects))
                 num-rows))

(defn item-weight [i] (* 100 (item-aspect i)))

(defn compute-layout
  "Given a sequence of items and the window dimensions, return a sequence of
  sequences containing item entries laid out according to the partition
  algorithm."
  [items window-base]
  (let [aspects  (map item-aspect items)
        num-rows (compute-rows window-base aspects)]
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

(defn row-aspect-map
  [layout]
  (map (fn [items] [(reduce + (map item-aspect items)) items])
       layout))

(reg-sub
  :album-layout/summed-layout
  (fn [[_ items]] (subscribe [:album-layout/layout items]))
  (fn [layout] (row-aspect-map layout)))

(defn scale-layout
  "Return a layout which contains explicit dimensions for items."
  [{:keys [box] :as window} layout]
  (let [width (:width box)]
    (map-indexed (fn [row-idx [aspect-sum items]]
                   (map-indexed (fn [col-idx [id {:keys [aspect] :as data}]]
                                  (let [height  (/ width aspect-sum)
                                        new-box (mk-rect (* aspect height) height)]
                                    [id new-box {:row row-idx :col col-idx}]))
                                items))
                 layout)))

(reg-sub
  :album-layout/scaled-layout
  (fn [[_ items]]
    [(subscribe [:album-layout/window (hash items)])
     (subscribe [:album-layout/summed-layout items])])
  (fn [[window layout] _]
    (scale-layout window layout)))