(ns galleries.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(reg-sub :debug (fn [db _]  (str db)))