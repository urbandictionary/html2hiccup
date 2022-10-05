(ns html2hiccup.core-test
  (:require
   [clojure.test :refer [deftest is]]
   [html2hiccup.core :refer [html2hiccup]]))

(deftest tw-test
  (is
   (=
    '([:html
       [:head]
       [:body
        [:main
         (tw :my-0 :mx-auto :max-w-3xl :text-center)
         [:h2 (tw :p-6 :text-4xl) "A Basic Tailwind CSS Example"]
         [:a
          (tw :px-10 :pb-10 :text-left)
          {:href "#"}
          "Tailwind CSS works by scanning all of your HTML files, JavaScript components, and any other templates for class names, generating the corresponding styles and then writing them to a static CSS file. It's fast, flexible, and reliable — with zero-runtime."]
         [:button
          (tw :bg-sky-600 "hover:bg-sky-700" :px-5 :py-3 :text-white :rounded-lg)
          "BUTTON EXAMPLE"]]]])
    (html2hiccup (slurp "tw.html")))))
