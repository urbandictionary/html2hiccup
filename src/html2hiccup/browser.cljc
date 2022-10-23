(ns html2hiccup.browser
  (:require
   [html2hiccup.core :refer [html2hiccup]]
   [zprint.core :refer [czprint]]
   ["ansi-to-html" :as ansi-to-html]))

(defn convert [input] (.toHtml (ansi-to-html. #js {:newline true}) (with-out-str (czprint (html2hiccup input)))))