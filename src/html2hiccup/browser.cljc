(ns html2hiccup.browser
  (:require
   [html2hiccup.core :refer [html2hiccup]]
   [zprint.core :refer [zprint]]))

(defn convert [input]
  (let [x (with-out-str (zprint (html2hiccup input)))]
    (js/console.log x)
    x))