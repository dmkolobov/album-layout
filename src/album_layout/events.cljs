(ns album-layout.events
  (:require [album-layout.db :refer [default-value]]
            [re-frame.core :refer [reg-cofx reg-event-db reg-event-fx inject-cofx trim-v]]
            [album-layout.db :as db]))

(def breakpoints
  [320 480 720 1960])

(defn current-breakpoint
  "Given the window dimensions, return the larget breakpoint that is
  less than that width."
  [{:keys [width]}]
  (last (filter #(<= % width) breakpoints)))

(defn handle-container-resized
  "Update either the base window size, or the window scale based
  on the size of the resized window, and the previous size of the
  window."
  [{:keys [db]} [gallery-id window]]
  (let [current-window (get-in db [:album-layout/containers gallery-id :base])]
    (if (= (current-breakpoint current-window)
           (current-breakpoint window))
      {:db (assoc-in db
                     [:album-layout/containers gallery-id :scale]
                      (/ (:width window) (:width current-window)))}
      {:db (assoc-in db
                     [:album-layout/containers gallery-id]
                     {:base  window
                      :scale 1.0})})))

(reg-event-fx :album-layout/container-resized
              [trim-v]
              handle-container-resized)

(defn handle-initialize-db
  "Adds default schema for album-layouts. All keys
  used by album-layout are prefixed with :album-layout/<key>"
  [{:keys [db]} _]
  {:db (merge db db/default-value)})

(reg-event-fx :album-layout/initialize-db
              [trim-v]
              handle-initialize-db)