(ns html2hiccup.core
  (:require
   [hickory.core :as hickory]
   [clojure.string :as str]
   [clojure.walk :refer [postwalk]]
   [clojure.edn :as edn]
   ))

(def hiccup-vector-with-attrs? #(and (vector? %) (keyword? (first %)) (map? (second %))))
(def keywordable? #(and (string? %) (re-matches #"[a-zA-Z][-a-zA-Z0-9:]+" %)))
(def try-keyword #(if (keywordable? %) (keyword %) %))
(def hiccup-walker #(fn [x] (if (hiccup-vector-with-attrs? x) (% x) x)))

(defn remove-blank-strings-and-html-comments
  [x]
  (->> (concat [(first x) (second x)]
               (remove #(and (string? %) (or (str/blank? %) (re-matches #"<!-- .+ -->" %))) (rest (rest x))))
       (into [])))

(defn trim-all-strings [x] (if (string? x) (str/trim x) x))

(defn remove-empty-attr-maps
  [x]
  (if (and (hiccup-vector-with-attrs? x) (empty? (second x)))
    (into [] (concat [(first x)] (rest (rest x))))
    x))

(defn keywordize-attr-values
  [x]
  (into []
        (concat [(first x) (zipmap (keys (second x)) (map try-keyword (vals (second x))))] (rest (rest x)))))

(defn change-empty-string-attrs-to-true
  [x]
  (into []
        (concat [(first x) (zipmap (keys (second x)) (map #(if (= "" %) true %) (vals (second x))))]
                (rest (rest x)))))

(defn fix-alpine-attrs [x] (if (and (keyword? x) (re-find #"^[:@]" (name x))) (name x) x))

(defn change-class-list-to-hiccup
  [x]
  (if (:class (second x))
    (let [classes (str/split (:class (second x)) #"\s+")]
      (if (every? keywordable? classes)
        (into []
              (concat [(keyword (str/join "." (concat [(name (first x))] classes)))
                       (dissoc (second x) :class)]
                      (rest (rest x))))
        x))
    x))

(defn convert-numbers [x] (if (and (string? x) (re-matches #"[0-9]+" x)) (edn/read-string x) x))

(defn html2hiccup
  [input]
  (->> input
       hickory/parse
       hickory/as-hiccup
       (postwalk fix-alpine-attrs)
       (postwalk trim-all-strings)
       (postwalk convert-numbers)
       (postwalk (hiccup-walker remove-blank-strings-and-html-comments))
       (postwalk (hiccup-walker change-class-list-to-hiccup))
       (postwalk (hiccup-walker change-empty-string-attrs-to-true))
       (postwalk (hiccup-walker keywordize-attr-values))
       (postwalk remove-empty-attr-maps)))