(ns galleries.events
  (:require [galleries.db :refer [default-value]]
            [re-frame.core :refer [reg-cofx reg-event-db reg-event-fx inject-cofx trim-v]]))

;; ===========
;;   Coeffects
;; ===========
;;
;; These functions take a coeffects map as input and return another
;; coeffects map, augmented with information from the "real world".

(defn window-dimensions
  "Adds the current window width and height to the coeffects map."
  [coeffects]
  (assoc coeffects
    :window {:width  (.-clientWidth (.-body js/document))
             :height (.-innerHeight js/window)}))

(reg-cofx :window-dimensions window-dimensions)

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
  [{:keys [db window]} _]
  (let [current-window (:window db)]
    (if (= (current-breakpoint current-window)
           (current-breakpoint window))
      {:db (assoc db
             :window-scale (/ (:width window)
                              (:width current-window)))}
      {:db (assoc db
             :window        window
             :window-scale  1.0)})))

(reg-event-fx :window-resized
              [(inject-cofx :window-dimensions)]
              handle-window-resized)

(reg-event-fx
  :initialize-db
  [trim-v]
  (fn [{:keys [db]} [images]]
    {:db (merge db
                default-value
                {:images images})}))