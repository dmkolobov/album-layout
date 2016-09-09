(ns galleries.events
  (:require [galleries.db :refer [default-value]]
            [re-frame.core :refer [reg-event-db reg-event-fx]]))

(defn window-resized-handler
  "Get the inner width and inner height of the browser window for computing
  gallery layout."
  [{:keys [db]} _]
  {:db (assoc db
              :window
              {:width  (.-innerWidth js/window)
               :height (.-innerHeight js/window)})})

(reg-event-fx
  :window-resized
  window-resized-handler)

(reg-event-db
  :initialize-db
  (fn [db [_ images]]
    (merge db
           default-value
           {:images images})))