(ns html2hiccup.main
  (:require
   [html2hiccup.core :refer [html2hiccup]]
   [zprint.core :refer [zprint]]))

(defn -main
  [file]
  (->> file
       slurp
       html2hiccup
       zprint))
