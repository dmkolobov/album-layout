(ns galleries.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]
            [galleries.util :refer [selector]]))

(reg-sub :debug (fn [db _] (str db)))
(reg-sub :images (fn [db _] (:images db)))
(reg-sub :window (fn [db _] (:window db)))
(reg-sub :window-scale (fn [db _] (:window-scale db)))

(defn image-id [[id _]] id)
(defn image-aspect [[id {:keys [aspect]}]] aspect)

(defn compute-rows
  "Given the window dimensions and a sequence of image aspect ratios,
  return the ideal number of rows for the gallery layout."
  [{:keys [width height]} aspects]
  (let [ideal-height (/ height 2)]
    (.round js/Math
            (/ (reduce (fn [total aspect] (+ total (* aspect ideal-height)))
                         0
                         aspects)
                 width))))

(defn compute-partitions
  "Given a sequence of image aspects and the number of rows, return
  a sequence of rows, each of which is a sequence of image aspects
  whose sum is as equal as possible to the sums of other rows."
  [aspects num-rows]
  (js->clj
    (.exports js/module
              (clj->js (map (partial * 100) aspects))
              num-rows)))

(defn image-weight [i] (* 100 (image-aspect i)))

(defn fill-partitions
  "Replace each aspect ratio in the partition sequence with an image of the
   same aspect ratio. Each image is used only once."
  [partitions images]
  (map (partial map (selector image-weight images))
       partitions))

(defn compute-layout
  "Given a sequence of images and the window dimensions, return a sequence of
  sequences containing image entries laid out according to the partition
  algorithm."
  [images window]
  (let [aspects  (map image-aspect images)
        num-rows (compute-rows window aspects)]
    (fill-partitions (compute-partitions aspects num-rows)
                     images)))

(reg-sub
  :layout
  (fn [_]
    [(subscribe [:images])
     (subscribe [:window])])
  (fn [[images window] _]
    (compute-layout images window)))

(defn row-scale-factor
  "Given the row width, ideal row height, and a sequence of images,
  return the scale factor s, where the actual row height is given
  by multiplying the ideal row height by s."
  [row-width row-height images]
  (/ row-width
     (reduce (fn [sum [_ {:keys [aspect]}]]
               (+ sum (* aspect row-height)))
             0
             images)))

(defn scale-row
  "Given the row dimensions and a sequence of images, return a new
  sequence of images which have :width, :height keys. These images
  are scaled so that the sum of their widths is equal to the the row width."
  [row-width row-height images]
  (let [factor (row-scale-factor row-width row-height images)]
    (map (fn [[id {:keys [aspect]}]]
           [id {:width  (* aspect row-height factor)
                :height (* row-height factor)}])
         images)))

(defn scale-layout
  "Return a layout which contains explicit dimensions for images."
  [window window-scale layout]
  (let [width        (* window-scale (:width window))
        height       (:height window)]
    (map (partial scale-row width (/ height 2)) layout)))

(reg-sub
  :scaled-layout
  (fn [_]
    [(subscribe [:window])
     (subscribe [:window-scale])
     (subscribe [:layout])])
  (fn [[window window-scale layout] _]
    (scale-layout window window-scale layout)))