(ns galleries.events
  (:require [galleries.db :refer [default-value]]
            [re-frame.core :refer [reg-cofx reg-event-db reg-event-fx inject-cofx]]))

;; ===========
;;   Coeffects
;; ===========
;;
;; These functions take a coeffects map as input and return another
;; coeffects map, augmented with information from the "real world".

(reg-cofx
  :window-dimensions
  (fn [coeffects]
    (assoc coeffects
      :window-dimensions {:width  (.-innerWidth js/window)
                          :height (.-innerHeight js/window)})))

;; ===========
;;   Effects
;; ===========
;;
;; These functions take coeffects as handlers and return effects

(defn window-resized-handler
  "Get the inner width and inner height of the browser window for computing
  gallery layout."
  [{:keys [db window-dimensions]} _]
  {:db (assoc db :window window-dimensions)})

(reg-event-fx
  :window-resized
  [(inject-cofx :window-dimensions)]
  window-resized-handler)

(reg-event-db
  :initialize-db
  (fn [db [_ images]]
    (merge db
           default-value
           {:images images})))