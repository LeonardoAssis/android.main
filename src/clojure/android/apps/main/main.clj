(ns android.apps.main.main
  (:use [neko.activity :only [defactivity set-content-view!]]
        [neko.threading :only [on-ui]]
        [neko.ui :only [make-ui]]
        [neko.notify :only [toast]])
  (:import [android.content Intent]
           [android.app Activity]))


(defn start-service!
  [^Activity context class-name connected]
  (let [intent (android.content.Intent.)
        connection (proxy [android.content.ServiceConnection] []
                     (onServiceConnected [component-name binder]
                                         (connected binder))
                     (onServiceDisconnected [component-name] ()))]
    (.setClassName intent context class-name)
    (.startService context intent)
    (.bindService context intent connection 0)
    connection))

(defn stop-service!
  [^Activity context connection]
  (.unbindService context connection))

(defn click [_ activity ]
  (start-service! activity "android.apps.main.SneerService" #(toast (str %))))

(defactivity android.apps.main.MainActivity
  :def a
  :on-create
  (fn [this bundle]
    (on-ui
     (set-content-view! a
      (make-ui [:linear-layout {}
                [:text-view {:text "Hello from Clojure!"}]
                [:button {:text "Press Me" :on-click #(click %1 a)}]])))))


(do
  (gen-class
    :name "CustomBinder"
    :extends android.os.Binder
    :state "state"
    :init "init"
    :constructors {[android.app.Service] []}
    :prefix "binder-")
  (defn binder-init
    [service]
    [[] service])
  (defn create-binder
    [service]
    (CustomBinder. service)))


(gen-class :name android.apps.main.SneerService
           :extends android.app.Service
           :main false)

(defn -onStartCommand [ this ^Intent intent, flags, startId ]
  (toast "Sneer Started!" :short)
  android.app.Service/START_STICKY)

(defn -onBind [ this ^Intent intent ]
  (toast "Sneer Bound!" :short)
  (create-binder this))

(defn -onCreate [ this ]
    (toast "Sneer Created!" :short))

(defn -onDestroy [ this ]
    (toast "Sneer Destroyed!" :short))


