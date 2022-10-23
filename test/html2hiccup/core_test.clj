(ns html2hiccup.core-test
  (:require
   [clojure.test :refer [deftest is]]
   [hickory.render :refer [hiccup-to-html]]
   [html2hiccup.core :refer [html2hiccup]]
   [clojure.java.io :as io]
   [clojure.edn :as edn])
  (:import
   (java.io PushbackReader)))

(defn edn-file [path] (edn/read (PushbackReader. (io/reader path))))

(deftest example-test (is (= (edn-file "example.edn") (html2hiccup (slurp "example.html")))))

(deftest windmill-test (is (= (edn-file "windmill.edn") (html2hiccup (slurp "windmill.html")))))

(deftest defer-test
  (is (= '([:html [:head [:script {:defer true :src "s.js"}]] [:body]])
         (html2hiccup "<script defer src='s.js'></script>"))))

(deftest a-1-test
  (is (= '([:html [:head] [:body [:a.a-1.b-2.c-3 {:href "#"}]]])
         (html2hiccup "<a class=\"a-1 b-2 c-3\" href=\"#\"></a>"))))

(deftest alpine-test
  (is (= '([:html [:head] [:body [:a {":href" "#"} "x"]]]) (html2hiccup "<a :href=\"#\">x</a>")))
  (is (= '([:html [:head] [:body [:a {"@click" "#"} "x"]]]) (html2hiccup "<a @click=\"#\">x</a>"))))

(deftest defer->html-test
  (is (= "<script defer>hi</script>" (hiccup-to-html (list [:script {:defer true} "hi"])))))