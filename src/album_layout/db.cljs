(ns album-layout.db
  (:require [re-frame.core :as re-frame]))

(def default-value
  "The default value for the image gallery state."
  {:album-layout/containers {}})