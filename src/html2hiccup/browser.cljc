(ns html2hiccup.browser
  (:require [html2hiccup.core :refer [html2hiccup]]
            [zprint.core :refer [czprint]]))

(defn convert [input]
  (with-out-str (czprint (html2hiccup input))))