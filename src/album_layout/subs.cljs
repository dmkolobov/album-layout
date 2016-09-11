(ns album-layout.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]
            [album-layout.util :refer [selector]]
            [album-layout.bundle]))

(reg-sub :debug (fn [db _] (str db)))
(reg-sub :window (fn [db [_ gallery-id]] (get-in db [:album-layout/containers gallery-id])))
(reg-sub :window-base (fn [db [_ gallery-id]] (get-in db [:album-layout/containers gallery-id :base])))

(defn item-id [[id _]] id);;
(defn item-aspect [[id {:keys [aspect]}]] aspect)

(defn total-width
  ""
  [target-height items]
  (reduce (fn [sum [_ {:keys [aspect]}]]
            (+ sum (* aspect target-height)))
          0
          items))

(defn compute-rows
  "Given the window dimensions and a sequence of item aspect ratios,
  return the ideal number of rows for the gallery layout."
  [{:keys [width height]} items]
  (.round js/Math
          (/ (total-width (/ height 2) items)
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

(defn fill-partitions
  "Replace each aspect ratio in the partition sequence with an item of the
   same aspect ratio. Each item is used only once."
  [partitions items]
  (map (partial map (selector item-weight items))
       partitions))

(defn compute-layout
  "Given a sequence of items and the window dimensions, return a sequence of
  sequences containing item entries laid out according to the partition
  algorithm."
  [items window-base]
  (let [aspects  (map item-aspect items)
        num-rows (compute-rows window-base items)]
    (fill-partitions (compute-partitions aspects num-rows)
                     items)))

(reg-sub
  :layout
  (fn [[_ items]]
    [items
     (subscribe [:window-base (hash items)])])
  (fn [[items window-base] _]
    (when window-base
      (let [start  (.now js/Date)
            layout (compute-layout items window-base)]
        (with-out-str (println layout))
        (println "time to layout:" (- (.now js/Date) start))
        layout))))

(defn row-scale-factor
  "Given the row width, ideal row height, and a sequence of items,
  return the scale factor s, where the actual row height is given
  by multiplying the ideal row height by s."
  [row-width row-height items]
  (/ row-width (total-width row-height items)))

(defn scale-row
  "Given the row dimensions and a sequence of items, return a new
  sequence of items which have :width, :height keys. These items
  are scaled so that the sum of their widths is equal to the the row width."
  [row-width row-height items]
  (let [factor (row-scale-factor row-width row-height items)]
    (map (fn [[id {:keys [aspect]}]]
           [id {:width  (* aspect row-height factor)
                :height (* row-height factor)}])
         items)))

(defn scale-layout
  "Return a layout which contains explicit dimensions for items."
  [{:keys [base scale] :as window} layout]
  (let [width        (* scale (:width base))
        height       (/ (:height base) 2)]
    (for [row layout] (scale-row width height row))))

(reg-sub
  :scaled-layout
  (fn [[_ items]]
    [(subscribe [:window (hash items)])
     (subscribe [:layout items])])
  (fn [[window layout] _]
    (scale-layout window layout)))