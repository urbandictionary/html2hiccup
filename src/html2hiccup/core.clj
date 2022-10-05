(ns html2hiccup.core
  (:gen-class)
  (:require
   [hickory.core :as hickory]
   [clojure.string :as str]
   [clojure.walk :refer [postwalk]]
   [zprint.core :refer [czprint]]))

(defn remove-blanks
  [node]
  (if (and (vector? node) (keyword? (first node)) (map? (second node)))
    (->> (concat [(first node) (second node)] (remove #(and (string? %) (str/blank? %)) (rest (rest node))))
         (into []))
    node))

(defn trim-strings [node] (if (string? node) (str/trim node) node))

(defn remove-empty-attrs
  [node]
  (if (and (vector? node) (keyword? (first node)) (map? (second node)) (empty? (second node)))
    (into [] (concat [(first node)] (rest (rest node))))
    node))

(def keywordable? (partial re-matches #"[a-zA-Z][-a-zA-Z0-9:]+"))

(def try-keyword #(if (and (not (keyword? %)) (keywordable? %)) (keyword %) %))

(defn tw-classes [classes] (map try-keyword (str/split classes #"\s+")))

(defn keyword-attrs
  [node]
  (if (and (vector? node) (keyword? (first node)) (map? (second node)))
    (into []
          (concat [(first node) (zipmap (keys (second node)) (map try-keyword (vals (second node))))]
                  (rest (rest node))))
    node))

(defn tw
  [node]
  (if (and (vector? node) (keyword? (first node)) (map? (second node)) (:class (second node)))
    (into []
          (concat [(first node)]
                  (cond-> [(concat ['tw] (tw-classes (:class (second node))))]
                    (not= [:class] (keys (second node))) (conj (dissoc (second node) :class)))
                  (rest (rest node))))
    node))

(defn html2hiccup
  [input]
  (->> input
       hickory/parse
       hickory/as-hiccup
       (postwalk remove-blanks)
       (postwalk trim-strings)
       (postwalk tw)
       (postwalk keyword-attrs)
       (postwalk remove-empty-attrs)
       ))

(defn -main
  [file]
  (->> file
       slurp
       html2hiccup
       (apply czprint)))
