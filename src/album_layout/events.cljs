(ns album-layout.events
  (:require [album-layout.db :refer [default-value]]
            [re-frame.core :refer [reg-cofx reg-event-db reg-event-fx inject-cofx trim-v]]
            [album-layout.db :as db]))

(defrecord LayoutContainer [base-box box])

(defn should-layout?
  [delta rect new-rect]
  (> (.abs js/Math
           (- (:width rect)
              (:width new-rect)))
     delta))

(defn handle-container-resized
  [{:keys [db]} [layout-id new-base scale-increment]]
  {:db
   (update-in db
              [:album-layout/containers layout-id]
              (fn [{:keys [base-box] :as container}]
                (if (should-layout? scale-increment base-box new-base)
                  (do
                    (println "laying out")
                    (LayoutContainer. new-base new-base))
                  (assoc container :box new-base))))})

(reg-event-fx :album-layout/container-resized
              [trim-v]
              handle-container-resized)