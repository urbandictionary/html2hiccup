(ns html2hiccup.core
  (:gen-class)
  (:require
   [hickory.core :as hickory]
   [clojure.string :as str]
   [clojure.walk :refer [postwalk]]
   [zprint.core :refer [zprint]]))

(def hiccup-vector-with-attrs? #(and (vector? %) (keyword? (first %)) (map? (second %))))
(def keywordable? #(and (string? %) (re-matches #"[a-zA-Z][-a-zA-Z0-9:]+" %)))
(def try-keyword #(if (keywordable? %) (keyword %) %))

(defn remove-blank-strings-and-html-comments
  [node]
  (if (hiccup-vector-with-attrs? node)
    (->> (concat [(first node) (second node)]
                 (remove #(and (string? %) (or (str/blank? %) (re-matches #"<!-- .+ -->" %)))
                         (rest (rest node))))
         (into []))
    node))

(defn trim-all-strings [node] (if (string? node) (str/trim node) node))

(defn remove-empty-attr-maps
  [node]
  (if (and (hiccup-vector-with-attrs? node) (empty? (second node)))
    (into [] (concat [(first node)] (rest (rest node))))
    node))

(defn keywordize-attr-keys
  [node]
  (if (hiccup-vector-with-attrs? node)
    (into []
          (concat [(first node) (zipmap (keys (second node)) (map try-keyword (vals (second node))))]
                  (rest (rest node))))
    node))

(defn attr-values-empty-strings->true
  [node]
  (if (hiccup-vector-with-attrs? node)
    (into []
          (concat [(first node)
                   (zipmap (keys (second node)) (map #(if (= "" %) true %) (vals (second node))))]
                  (rest (rest node))))
    node))

(defn fix-alpine-keywords [node] (if (and (keyword? node) (re-find #"^[:@]" (name node))) (name node) node))

(defn html2hiccup
  [input]
  (->> input
       hickory/parse
       hickory/as-hiccup
       (postwalk fix-alpine-keywords)
       (postwalk remove-blank-strings-and-html-comments)
       (postwalk trim-all-strings)
       (postwalk attr-values-empty-strings->true)
       (postwalk keywordize-attr-keys)
       (postwalk remove-empty-attr-maps)))

(defn -main
  [file]
  (->> file
       slurp
       html2hiccup
       zprint))
