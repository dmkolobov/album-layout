(ns album-layout.util)

(defn selector
  [key-fn coll]
  (let [cache (atom (group-by key-fn coll))]
    (fn [k]
      (let [item (first (get @cache k))]
        (swap! cache update k rest)
        item))))

(defrecord Rect [width height])

(defn mk-rect [w h] (Rect. w h))

(defn node-dimensions
  "Given a DOM element, return a map containing the width
  and height of the element."
  [node]
  (let [rect (.getBoundingClientRect node)]
    (Rect. (js/parseInt
             (min (.-width rect)
                  document.documentElement.clientWidth))
           (.-innerHeight js/window))))
