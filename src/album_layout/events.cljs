(ns album-layout.events
  (:require [album-layout.db :refer [default-value]]
            [re-frame.core :refer [reg-cofx reg-event-db reg-event-fx inject-cofx trim-v]]
            [album-layout.db :as db]))

(defn handle-container-resized
  [{:keys [db]} [layout-id new-base scale-increment]]
  {:db
   (update-in db
              [:album-layout/containers layout-id]
              (fn [{:keys [base] :as container}]
                (if (< (.abs js/Math
                             (- (:width base)
                                (:width new-base)))
                       scale-increment)
                  (assoc container
                    :scale (/ (:width new-base)
                              (:width base)))
                  {:base new-base :scale 1.0})))})

(reg-event-fx :album-layout/container-resized
              [trim-v]
              handle-container-resized)
