(ns galleries.events
  (:require [galleries.db :refer [default-value]]
            [re-frame.core :refer [reg-cofx reg-event-db reg-event-fx inject-cofx trim-v]]))


;; ===========
;;   Effects
;; ===========
;;
;; These functions take coeffects as inputs and return effects.

(def breakpoints
  [320 480 720 1960])

(defn current-breakpoint
  "Given the window dimensions, return the larget breakpoint that is
  less than that width."
  [{:keys [width]}]
  (last (filter #(<= % width) breakpoints)))

(defn handle-window-resized
  "Update either the base window size, or the window scale based
  on the size of the resized window, and the previous size of the
  window."
  [{:keys [db]} [gallery-id window]]
  (let [current-window (get-in db [:windows gallery-id :base])]
    (if (= (current-breakpoint current-window)
           (current-breakpoint window))
      {:db (assoc-in db
                     [:windows gallery-id :scale]
                      (/ (:width window) (:width current-window)))}
      {:db (assoc-in db
                     [:windows gallery-id]
                     {:base  window
                      :scale 1.0})})))

(reg-event-fx :window-resized
              [trim-v]
              handle-window-resized)

(reg-event-fx
  :initialize-db
  [trim-v]
  (fn [{:keys [db]} _]
    {:db (merge db default-value)}))