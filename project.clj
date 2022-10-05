(defproject html2hiccup "0.1.0-SNAPSHOT"
  :sha {:length 8}
  :repositories {"gitlab" {:password :env/gitlab_package_token
                           :sign-releases false
                           :url "gitlab://gitlab.com/api/v4/projects/36492006/packages/maven"
                           :username "Private-Token"}}
  :bat-test {:capture-output? false}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [hickory/hickory "0.7.1"]
                 [zprint "1.2.4"]
                 [org.clojure/tools.reader "1.3.6"]]
  :main ^:skip-aot html2hiccup.core
  :target-path "target/%s"
  :plugins
  [[com.ragaus/lein-gitlab-wagon "1.0.0"] [coreagile/lein-sha-version "0.1.2"] [metosin/bat-test "0.4.4"]]
  :profiles {:uberjar {:aot :all :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
