(ns galleries.views
  (:require [re-frame.core :refer [subscribe dispatch]]))

(defn gallery
  []
  (let [debug (subscribe [:debug])]
    (fn []
      [:pre @debug])))


