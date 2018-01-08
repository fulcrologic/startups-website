(ns user
  (:require
    [clojure.tools.namespace.repl :as tools-ns :refer [set-refresh-dirs]]
    [com.stuartsierra.component :as component]
    [figwheel-sidecar.system :as fig]
    [fulcro-spec.suite :as suite]
    [fulcro-spec.selectors :as sel]
    startupsite.server
    [hawk.core :as hawk]
    [taoensso.timbre :as timbre]))

;;FIGWHEEL
(def figwheel (atom nil))

; Usable from a REPL to start one-or-more figwheel builds. Support -Dbuild-name and -Dfigwheel.port so that you can
; run any specific build(s) on a custom port from an IntelliJ Run Configuration. This is helpful when you want
; to run multiple builds, but want fast hot reload (place each build on a separate figwheel instance running on
; a custom port. E.g. in JVM Args: `-Dcards -Dfigwheel.port=5000` with parameters `script/figwheel.clj`.
(defn start-figwheel
  "Start Figwheel on the given builds, or defaults to build-ids in `figwheel-config`."
  ([]
   (let [figwheel-config (fig/fetch-config)
         props           (System/getProperties)
         all-builds      (->> figwheel-config :data :all-builds (mapv :id))]
     (start-figwheel (keys (select-keys props all-builds)))))
  ([build-ids]
   (let [figwheel-config   (fig/fetch-config)
         port              (some-> (System/getProperty "figwheel.port") Integer/parseInt)
         default-build-ids (-> figwheel-config :data :build-ids)
         build-ids         (if (empty? build-ids) default-build-ids build-ids)
         preferred-config  (cond-> (assoc-in figwheel-config [:data :build-ids] build-ids)
                             (and port (pos? port)) (assoc-in [:data :figwheel-options :server-port] port))]
     (reset! figwheel (component/system-map
                        :css-watcher (fig/css-watcher {:watch-paths ["resources/public/css"]})
                        :figwheel-system (fig/figwheel-system preferred-config)))
     (println "STARTING FIGWHEEL ON BUILDS: " build-ids)
     (swap! figwheel component/start)
     (fig/cljs-repl (:figwheel-system @figwheel)))))

;; ==================== SERVER ====================

(set-refresh-dirs "src/server" "src/dev" "src/main" "src/test")

(defonce system (atom nil))

(defn stop
  "Stop the server."
  []
  (when @system
    (swap! system component/stop))
  (reset! system nil))

(defn start
  "Initialize the server and start it."
  []
  (when (not @system)
    (let [new-system (startupsite.server/make-system "config/dev.edn")]
      (reset! system new-system)
      (swap! system component/start))))

(defn restart
  "Stop, refresh, and restart the server."
  []
  (stop)
  (tools-ns/refresh :after 'user/start))

; Run (start-server-tests) in a REPL to start a runner that can render results in a browser
(suite/def-test-suite start-server-tests
  {:config       {:port 8888}
   :test-paths   ["src/test"]
   :source-paths ["dev/server" "src/main"]}
  {:available #{:focused :unit :integration}
   :default   #{::sel/none :focused :unit}})

(let [refresh-time (atom 0)]
  (defn start-watching [server-source-dirs]
    (hawk/watch! [{:paths   server-source-dirs
                   :handler (fn [ctx e]
                              #_(require 'user)
                              (binding [*ns* (find-ns 'user)
                                        *e   nil]
                                (when-not (resolve 'restart)
                                  (timbre/error "Last compile failed. Trying refresh again.")
                                  (try
                                    (tools-ns/refresh :after 'user/start)
                                    (catch Exception e
                                      (.printStackTrace e))))
                                (let [now  (System/currentTimeMillis)
                                      stop (resolve 'stop)]
                                  (when (> (- now @refresh-time) 1000)
                                    (reset! refresh-time now)
                                    (try
                                      (stop)
                                      (tools-ns/refresh :after 'user/start)
                                      (when *e (timbre/error "Compile error: " *e))
                                      (catch Exception e
                                        (timbre/error "Restart failed.")
                                        (.printStackTrace e)))))))}])))
