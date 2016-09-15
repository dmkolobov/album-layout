(ns album-layout.views
  (:require [re-frame.core :refer [subscribe dispatch]]
            [cljs.pprint :refer [pprint]]
            [reagent.core :as reagent]))

(defn gallery
  ""
  [& {:keys [layout render-fn]}]
  [:div
  (doall
    (map-indexed (fn [idx row]
                   ^{:key idx}
                   [:div
                    {:style {:overflow "hidden"}}
                    (map (fn [[id {:keys [width height] :as box} data]]
                           ^{:key id}
                           [:div
                            ;; BUG: Usage of parseInt results in rows which vary slightly
                            ;;      in width. Needed to fix row overflow in Firefox.
                            {:style {:width      (js/parseInt width)
                                     :height     (js/parseInt height)
                                     :padding    "0.25em"
                                     :float      "left"
                                     :box-sizing "border-box"}}
                            [render-fn id box data]])
                         row)])
                 @layout))])

