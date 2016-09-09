(ns galleries.events
  (:require [galleries.db :refer [default-value]]
            [re-frame.core :refer [reg-event-db]]))

(reg-event-db
  :initialize-db
  (fn [db _] (merge db default-value)))