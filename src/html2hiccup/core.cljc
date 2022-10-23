(ns html2hiccup.core
  (:require [hickory.core :as hickory]
            [clojure.string :as str]
            [clojure.walk :refer [postwalk]]
            [clojure.edn :as edn]))

(def keywordable-attr-val?
  #(and (string? %) (re-matches #"[a-zA-Z0-9_][-a-zA-Z0-9:_]+" %)))
(def keywordable-class? #(and (string? %) (re-matches #"[-a-zA-Z0-9:_]+" %)))
(def concatv #(into [] (apply concat %&)))
(def hiccup-vec? #(and (vector? %) (keyword? (first %)) (map? (second %))))
(def hiccup-walker #(fn [node] (if (hiccup-vec? node) (% node) node)))
(def numeric? #(and (string? %) (re-matches #"[1-9][0-9]*" %)))
(def try-keyword-attr-val #(if (keywordable-attr-val? %) (keyword %) %))
(def try-keyword-attr-vals
  #(zipmap (keys %) (map try-keyword-attr-val (vals %))))
(def comment? #(re-matches #"<!--.+-->" %))

(defn remove-blank-strings-and-html-comments
  [node]
  (if (and (sequential? node) (not (map-entry? node)))
    (into [] (remove #(and (string? %) (or (str/blank? %) (comment? %))) node))
    node))

(defn trim-all-strings [x] (if (string? x) (str/trim x) x))

(defn remove-empty-attr-maps
  [[tag attrs & children :as node]]
  (if (empty? attrs) (concatv [tag] children) node))

(defn keywordize-attr-vals
  [[tag attrs & children]]
  (concatv [tag (try-keyword-attr-vals attrs)] children))

(defn change-empty-string-attrs-to-true
  [[tag attrs & children]]
  (concatv [tag (zipmap (keys attrs) (map #(if (= "" %) true %) (vals attrs)))]
           children))

(defn fix-alpine-attrs
  [node]
  (if (and (keyword? node) (re-find #"^[:@]" (name node))) (name node) node))

(defn change-class-list-to-hiccup
  [[tag attrs & children :as node]]
  (if (:class attrs)
    (let [classes (str/split (:class attrs) #"\s+")]
      (if (every? keywordable-class? classes)
        (concatv [(keyword (str/join "." (concat [(name tag)] classes)))
                  (dissoc attrs :class)]
                 children)
        node))
    node))

(defn change-id-to-hiccup
  [[tag attrs & children :as node]]
  (if (:id attrs)
    (concatv [(keyword (str (name tag) "#" (:id attrs))) (dissoc attrs :id)]
             children)
    node))

(defn convert-numbers [node] (if (numeric? node) (edn/read-string node) node))

(defn ^:export html2hiccup
  [input]
  (->> input
       hickory/parse
       hickory/as-hiccup
       (postwalk fix-alpine-attrs)
       (postwalk trim-all-strings)
       (postwalk convert-numbers)
       (postwalk remove-blank-strings-and-html-comments)
       (postwalk (hiccup-walker change-id-to-hiccup))
       (postwalk (hiccup-walker change-class-list-to-hiccup))
       (postwalk (hiccup-walker change-empty-string-attrs-to-true))
       (postwalk (hiccup-walker keywordize-attr-vals))
       (postwalk (hiccup-walker remove-empty-attr-maps))))