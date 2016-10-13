(ns album-layout.views
  (:require [re-frame.core :refer [subscribe dispatch]]
            [cljs.pprint :refer [pprint]]
            [reagent.core :as reagent]))

(defn gallery
  ""
  [& {:keys [layout render-fn]}]
  (doall
    (map-indexed (fn [row-idx row]
                   ^{:key row-idx}
                   [:div {:style {:position "relative"
                                  :height   (:height (second (first row)))}}
                    (seq
                      (first
                        (reduce (fn [[row x] [id {:keys [width height] :as rect} data]]
                                  [(conj row
                                         ^{:key id}
                                         [:div {:style {:position "absolute"
                                                        :width    width
                                                        :height   height
                                                        :top      0
                                                        :left     x}}
                                          [render-fn id rect data]])
                                   (+ x width)])
                                [[] 0]
                                row)))])
                  @layout)))

