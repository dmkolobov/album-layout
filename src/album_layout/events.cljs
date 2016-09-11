(ns album-layout.events
  (:require [album-layout.db :refer [default-value]]
            [re-frame.core :refer [reg-cofx reg-event-db reg-event-fx inject-cofx trim-v]]
            [album-layout.db :as db]))

(defrecord LayoutContainer [base-box box])

(defn handle-container-resized
  [{:keys [db]} [layout-id new-base scale-increment]]
  {:db
   (update-in db
              [:album-layout/containers layout-id]
              (fn [{:keys [base-box] :as container}]
                (if (< (.abs js/Math
                             (- (:width base-box)
                                (:width new-base)))
                       scale-increment)
                  (assoc container :box new-base)
                  (LayoutContainer. new-base new-base))))})

(reg-event-fx :album-layout/container-resized
              [trim-v]
              handle-container-resized)
;;