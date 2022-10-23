(ns html2hiccup.browser
  (:require [html2hiccup.core :refer [html2hiccup]]
            [zprint.core :refer [zprint]]
            ["highlight.js/lib/core" :as hljs]
            ["highlight.js/lib/languages/clojure" :as hljs-clojure]))

(.. hljs (registerLanguage "clojure" hljs-clojure))

(defn convert
  [input]
  (.. hljs
      (highlight (with-out-str (zprint (html2hiccup input)))
                 #js {:language "clojure"})
      -value))