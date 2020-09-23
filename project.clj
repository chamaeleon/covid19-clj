(defproject covid19 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.cli "1.0.194"]
                 [org.clojure/data.csv "1.0.0"]]
  ;:global-vars {*warn-on-reflection* true}
  :jvm-opts ["-Xmx1g"]
  :main ^:skip-aot covid19.core
  :target-path "target/%s"
  :uberjar-name "covid19.jar"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
