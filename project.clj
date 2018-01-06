(defproject startupsite "0.1.0-SNAPSHOT"
  :description "My Cool Project"
  :license {:name "MIT" :url "https://opensource.org/licenses/MIT"}
  :min-lein-version "2.7.0"

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]

                 ; ui
                 [cljsjs/semantic-ui-react "0.73.0-0"]
                 [fulcrologic/semantic-ui-react-wrappers "1.0.0-SNAPSHOT"]
                 [fulcrologic/fulcro "2.1.0-beta2"]

                 ; server
                 [com.draines/postal "2.0.2"]

                 ; pinned
                 [cljsjs/react "16.2.0-1"]
                 [cljsjs/react-dom "16.2.0-1"]
                 [commons-codec "1.11"]

                 ; test
                 [fulcrologic/fulcro-spec "2.0.0-beta3" :scope "test"]]

  :uberjar-name "startupsite.jar"

  :source-paths ["src/main"]
  :test-paths ["src/test"]
  :clean-targets ^{:protect false} ["target" "resources/public/js" "resources/private"]

  ; Notes  on production build:
  ; - The hot code reload stuff in the dev profile WILL BREAK ADV COMPILATION. So, make sure you
  ; use `lein with-profile production cljsbuild once production` to build!
  :cljsbuild {:builds [{:id           "production"
                        :source-paths ["src/main"]
                        :jar          true
                        :compiler     {:asset-path    "js/prod"
                                       ;:main                 startupsite.client-main
                                       :optimizations :advanced
                                       :output-dir    "resources/public/js/prod"
                                       :output-to     "resources/public/js/startupsite.js"
                                       :foreign-libs  [{:provides       ["cljsjs.react"]
                                                        :file           "node_modules/react/dist/react.js"
                                                        :global-exports {cljsjs.react React}}
                                                       {:provides       ["cljsjs.react.dom"]
                                                        :file           "node_modules/react-dom/dist/react-dom.js"
                                                        :global-exports {cljsjs.react.dom ReactDOM}}]
                                       :install-deps  false
                                       :npm-deps      {:react                "15.6.1"
                                                       :react-dom            "15.6.1"
                                                       ;:semantic-ui-react    "0.74.2"
                                                       :react-facebook-login "3.6.2"
                                                       :react-google-login   "2.11.2"}
                                       :source-map    "resources/public/js/startupsite.js.map"}}]}

  :profiles {:uberjar    {:main           startupsite.server-main
                          :aot            :all
                          :jar-exclusions [#"public/js/prod" #"com/google.*js$"]
                          :prep-tasks     ["clean" ["clean"]
                                           "compile" ["with-profile" "production" "cljsbuild" "once" "production"]]}
             :production {}
             :dev        {:source-paths ["src/dev" "src/main" "src/test" "src/cards"]

                          :jvm-opts     ["-XX:-OmitStackTraceInFastThrow" "-client" "-XX:+TieredCompilation" "-XX:TieredStopAtLevel=1"
                                         "-Xmx1g" "-XX:+UseConcMarkSweepGC" "-XX:+CMSClassUnloadingEnabled" "-Xverify:none"]

                          :doo          {:build "automated-tests"
                                         :paths {:karma "node_modules/karma/bin/karma"}}

                          :figwheel     {:css-dirs        ["resources/public/css"]
                                         :validate-config false}

                          :test-refresh {:report       fulcro-spec.reporters.terminal/fulcro-report
                                         :with-repl    true
                                         :changes-only true}

                          :cljsbuild    {:builds
                                         [{:id           "dev"
                                           :figwheel     {:on-jsload "cljs.user/mount"}
                                           :source-paths ["src/dev" "src/main"]
                                           :compiler     {:asset-path           "js/dev"
                                                          :main                 cljs.user
                                                          :optimizations        :none
                                                          :output-dir           "resources/public/js/dev"
                                                          :output-to            "resources/public/js/startupsite.js"
                                                          :preloads             [devtools.preload]
                                                          :foreign-libs         [{:provides       ["cljsjs.react"]
                                                                                  :file           "node_modules/react/dist/react.js"
                                                                                  :global-exports {cljsjs.react React}}
                                                                                 {:provides       ["cljsjs.react.dom"]
                                                                                  :file           "node_modules/react-dom/dist/react-dom.js"
                                                                                  :global-exports {cljsjs.react.dom ReactDOM}}]
                                                          :install-deps         true
                                                          :npm-deps             {:react                "15.6.1"
                                                                                 :react-dom            "15.6.1"
                                                                                 ;:semantic-ui-react    "0.74.2"
                                                                                 :react-facebook-login "3.6.2"
                                                                                 :react-google-login   "2.11.2"}
                                                          :source-map-timestamp true}}
                                          {:id           "i18n" ;for gettext string extraction
                                           :source-paths ["src/main"]
                                           :compiler     {:asset-path    "i18n"
                                                          :main          startupsite.client-main
                                                          :optimizations :whitespace
                                                          :output-dir    "i18n/tmp"
                                                          :output-to     "i18n/i18n.js"}}
                                          {:id           "test"
                                           :source-paths ["src/test" "src/main"]
                                           :figwheel     {:on-jsload "startupsite.client-test-main/client-tests"}
                                           :compiler     {:asset-path    "js/test"
                                                          :main          startupsite.client-test-main
                                                          :optimizations :none
                                                          :output-dir    "resources/public/js/test"
                                                          :output-to     "resources/public/js/test/test.js"
                                                          :preloads      [devtools.preload]}}
                                          {:id           "automated-tests"
                                           :source-paths ["src/test" "src/main"]
                                           :compiler     {:asset-path    "js/ci"
                                                          :main          startupsite.CI-runner
                                                          :optimizations :none
                                                          :output-dir    "resources/private/js/ci"
                                                          :output-to     "resources/private/js/unit-tests.js"}}
                                          {:id           "cards"
                                           :figwheel     {:devcards true}
                                           :source-paths ["src/main" "src/cards"]
                                           :compiler     {:asset-path           "js/cards"
                                                          :main                 startupsite.cards
                                                          :optimizations        :none
                                                          :output-dir           "resources/public/js/cards"
                                                          :output-to            "resources/public/js/cards.js"
                                                          :preloads             [devtools.preload]
                                                          :source-map-timestamp true}}]}

                          :plugins      [[lein-cljsbuild "1.1.7"]
                                         [lein-doo "0.1.8"]
                                         [com.jakemccrary/lein-test-refresh "0.21.1"]]

                          :dependencies [[binaryage/devtools "0.9.8"]
                                         [fulcrologic/fulcro-inspect "2.0.0-alpha2"]
                                         [org.clojure/tools.namespace "0.3.0-alpha4"]
                                         [lein-doo "0.1.7" :scope "test"]
                                         [figwheel-sidecar "0.5.14"]
                                         [devcards "0.2.4" :exclusions [cljsjs/react]]]
                          :repl-options {:init-ns user}}})
