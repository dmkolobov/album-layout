(ns galleries.util)

(defn selector
  [key-fn coll]
  (let [cache (atom (group-by key-fn coll))]
    (fn [k]
      (let [item (first (get @cache k))]
        (swap! cache update k rest)
        item))))