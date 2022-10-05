(ns html2hiccup.core
  (:gen-class)
  (:require
   [hickory.core :as hickory]
   [clojure.string :as str]
   [clojure.walk :refer [prewalk]]
   [zprint.core :refer [czprint]]))

(defn remove-blanks [tree] (prewalk #(if (string? %) (str/trim %) %) tree))

(defn -main
  [& args]
  (->> "example.html"
       slurp
       hickory/parse
       hickory/as-hiccup
       remove-blanks
       czprint))
