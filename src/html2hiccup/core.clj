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
    (into [] (concat [(first node)] (rest (rest node))))
    node))

(defn tw
  [node]
  (if (and (vector? node) (keyword? (first node)) (map? (second node)) (:class (second node)))
    (into []
          (concat [(first node)]
                  [(list 'tw (:class (second node)) (dissoc (second node) :class))]
                  (rest (rest node))))
    node))

(defn -main
  [& args]
  (->> "tw.html"
       slurp
       hickory/parse
       hickory/as-hiccup
       (postwalk remove-blanks)
       (postwalk remove-empty-attrs)
       (postwalk tw)
       czprint))
