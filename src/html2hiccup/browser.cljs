(ns html2hiccup.browser
  (:require [html2hiccup.core :refer [html2hiccup]]
            [zprint.core :refer [zprint]]
            ["highlight.js/lib/core" :as hljs]
            ["highlight.js/lib/languages/clojure" :as hljs-clojure]))

(.. hljs (registerLanguage "clojure" hljs-clojure))

(defn ^:export text [input] (with-out-str (zprint (html2hiccup input))))

(defn ^:export html
  [input]
  (.. hljs (highlight (text input) #js {:language "clojure"}) -value))