(ns html2hiccup.core
  (:gen-class)
  (:require
   [hickory.core :as hickory]
   [clojure.string :as str]
   [clojure.walk :refer [postwalk]]
   [zprint.core :refer [czprint]]))

(defn remove-blanks
  [node]
  (if (and (vector? node) (keyword? (first node)))
    (into [] (remove #(and (string? %) (str/blank? %)) node))
    node))

(defn remove-empty-attrs
  [node]
  (if (and (vector? node) (keyword? (first node)) (map? (second node)) (empty? (second node)))
    (concat [(first node)] (rest (rest node)))
    node))

(defn -main
  [& args]
  (->> "example.html"
       slurp
       hickory/parse
       hickory/as-hiccup
       (postwalk remove-blanks)
       (postwalk remove-empty-attrs)
       czprint))
