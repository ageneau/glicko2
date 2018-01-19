(defproject ageneau/glicko2 "0.1.0"
  :description "Implementation of the Glicko-2 rating algorithm"
  :author "Sylvain Ageneau"
  :url "https://github.com/ageneau/glicko2"
  :license {:name "BSD 2-Clause \"Simplified\" License"
            :url "https://opensource.org/licenses/BSD-2-Clause"
            :year 2018
            :key "bsd-2-clause"}

  ;; Sets the values of global vars within Clojure. This example
  ;; disables all pre- and post-conditions and emits warnings on
  ;; reflective calls. See the Clojure documentation for the list of
  ;; valid global variables to set (and their meaningful values).
  :global-vars {*warn-on-reflection* true
                *assert* false}

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946" :scope "provided"]
                 [org.clojure/algo.generic "0.1.2"]]

  :profiles {:dev
             {:dependencies [[lein-doo "0.1.8"]
                             [com.cemerick/piggieback "0.2.2"]]

              :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

              :plugins      [[lein-doo "0.1.7"]]
              }}

  :plugins [[lein-cljsbuild "1.1.5"]]

  :cljsbuild
  {:builds [{:id "test"
             :source-paths ["src" "test" "target/classes"]
             :compiler {:output-to "target/js/testable.js"
                        :output-dir "target/js/out"
                        :main glicko2.test-runner
                        :optimizations :none}}
            {:id "node-test"
             :source-paths ["src" "test" "target/classes"]
             :compiler {:output-to "target/nodejs/testable.js"
                        :output-dir "target/nodejs/out"
                        :main glicko2.test-runner
                        :optimizations :none
                        :target :nodejs}}]})
