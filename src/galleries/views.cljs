(ns galleries.views
  (:require [re-frame.core :refer [subscribe dispatch]]
            [cljs.pprint :refer [pprint]]))

(defn gallery-
  [layout]
  [:div
   (doall
     (map-indexed (fn [idx row]
                    ^{:key idx}
                    [:div
                     {:style {:overflow "hidden"}}
                     (map (fn [[id {:keys [width height]}]]
                            ^{:key id}
                            [:div
                             {:style {:width      width
                                      :height     height
                                      :padding    "0.25em"
                                      :float      "left"
                                      :box-sizing "border-box"}}
                             [:div
                              {:style {:width "100%"
                                       :height "100%"
                                       :border "1px solid black"}}
                              id]])
                          row)])
                  layout))])

(defn gallery
  []
  (let [scaled-layout (subscribe [:scaled-layout])]
    (fn []
      [:div
       [gallery- @scaled-layout]])))


