(ns album-layout.util)

(defn selector
  [key-fn coll]
  (let [cache (atom (group-by key-fn coll))]
    (fn [k]
      (let [item (first (get @cache k))]
        (swap! cache update k rest)
        item))))

(defn node-dimensions
  "Given a DOM element, return a map containing the width
  and height of the element."
  [node]
  (let [box (.getBoundingClientRect node)]
    {:width  (.-width box)
     :height (.-innerHeight js/window)}))
