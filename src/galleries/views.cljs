(ns galleries.views
  (:require [re-frame.core :refer [subscribe dispatch]]
            [cljs.pprint :refer [pprint]]
            [reagent.core :as reagent]))

(defn node-dimensions
  "Given a DOM element, return a map containing the width
  and height of the element."
  [node]
  (let [box (.getBoundingClientRect node)]
    {:width  (.-width box)
     :height (.-innerHeight js/window)}))

(defn resize-handler
  [gallery-id node]
  (fn []
    (dispatch [:window-resized gallery-id (node-dimensions node)])))

(defn gallery;;
  ""
  [& {:keys [items render-fn]}]
  (let [gallery-id (hash items)
        layout     (subscribe [:scaled-layout items])]
    (reagent/create-class
      {:component-did-mount
       (fn [owner]
         (let [handler (resize-handler gallery-id (reagent/dom-node owner))]
           (aset js/window "onresize" handler)
           (handler)))
       :reagent-render
       (fn []
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
                         @layout))])})))


