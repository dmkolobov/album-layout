(ns galleries.db
  (:require [re-frame.core :as re-frame]))

(def default-value
  "The default value for the image gallery state."
  {:windows {}})