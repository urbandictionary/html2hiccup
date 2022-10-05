(ns html2hiccup.core-test
  (:require
   [clojure.test :refer [deftest is]]
   [hickory.render :refer [hiccup-to-html]]
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
          (tw :bg-sky-600 :hover:bg-sky-700 :px-5 :py-3 :text-white :rounded-lg)
          "BUTTON EXAMPLE"]]]])
    (html2hiccup (slurp "tw.html")))))

(deftest example-test
  (is
   (=
    '("<!DOCTYPE html>"
      [:html
       [:head
        [:title "Example Domain"]
        [:meta {:charset :utf-8}]
        [:meta
         {:content "text/html; charset=utf-8" :http-equiv :Content-type}]
        [:meta
         {:content "width=device-width, initial-scale=1" :name :viewport}]
        [:style
         {:type "text/css"}
         "body {\n        background-color: #f0f0f2;\n        margin: 0;\n        padding: 0;\n        font-family: -apple-system, system-ui, BlinkMacSystemFont, \"Segoe UI\", \"Open Sans\", \"Helvetica Neue\", Helvetica, Arial, sans-serif;\n        \n    }\n    div {\n        width: 600px;\n        margin: 5em auto;\n        padding: 2em;\n        background-color: #fdfdff;\n        border-radius: 0.5em;\n        box-shadow: 2px 3px 7px 2px rgba(0,0,0,0.02);\n    }\n    a:link, a:visited {\n        color: #38488f;\n        text-decoration: none;\n    }\n    @media (max-width: 700px) {\n        div {\n            margin: 0 auto;\n            width: auto;\n        }\n    }"]]
       [:body
        [:div
         [:h1 "Example Domain"]
         [:p
          "This domain is for use in illustrative examples in documents. You may use this\n    domain in literature without prior coordination or asking for permission."]
         [:p
          [:a
           {:href "https://www.iana.org/domains/example"}
           "More information..."]]]]])
    (html2hiccup (slurp "example.html")))))

(deftest windmill-test (is (not= [] (html2hiccup (slurp "windmill.html")))))

(deftest defer-test
  (is (= '([:html [:head [:script {:defer true, :src "s.js"}]] [:body]]) (html2hiccup "<script defer src='s.js'></script>"))))

(deftest alpine-test
  (is (= '([:html [:head] [:body [:a {":href" "#"} "x"]]]) (html2hiccup "<a :href=\"#\">x</a>"))))

(deftest defer->html-test
  (is (= "<script defer>hi</script>" (hiccup-to-html (list [:script {:defer true} "hi"])))))