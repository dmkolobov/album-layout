(ns galleries.views
  (:require [re-frame.core :refer [subscribe dispatch]]
            [cljs.pprint :refer [pprint]]))

(defn gallery-
  [layout & {:keys [render-fn]}]
  [:div
   (doall
     (map-indexed (fn [idx row]
                    ^{:key idx}
                    [:div
                     {:style {:overflow "hidden"}}
                     (map (fn [[id {:keys [width height] :as data}]]
                            ^{:key id}
                            [:div
                             {:style {:width      width
                                      :height     height
                                      :padding    "0.25em"
                                      :float      "left"
                                      :box-sizing "border-box"}}
                             [render-fn id data]])
                          row)])
                  layout))])

(defn gallery
  [& {:keys [render-fn]}]
  (let [scaled-layout (subscribe [:scaled-layout])]
    (fn []
      [:div
       [gallery- @scaled-layout :render-fn render-fn]])))


