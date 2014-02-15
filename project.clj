(defproject Sneer/Sneer "0.0.1-SNAPSHOT"
  :description "Main Sneer App"
  :url "http://github.com/sneerteam/android.main"
  :license {:name "LGPLv3"
            :url "https://www.gnu.org/licenses/lgpl.html"}
  :min-lein-version "2.0.0"

  :global-vars {*warn-on-reflection* true}

  :source-paths ["src/clojure" "src"]
  :java-source-paths ["src/java" "gen"]
  :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]

  :dependencies [[org.clojure-android/clojure "1.5.1-jb" :use-resources true]
                 [neko/neko "3.0.0"]]
  :profiles {:dev {:dependencies [[org.clojure/tools.nrepl "0.2.3"]
                                  [compliment "0.0.3"]]
                   :android {:aot :all-with-unused}}
             :release {:android
                       {;; Specify the path to your private keystore
                        ;; and the the alias of the key you want to
                        ;; sign APKs with. Do it either here or in
                        ;; ~/.lein/profiles.clj
                        ;; :keystore-path "/home/user/.android/private.keystore"
                        ;; :key-alias "mykeyalias"

                        :ignore-log-priority [:debug :verbose]
                        :aot :all}}}

  :android {:target-version "19"
            :aot-exclude-ns ["clojure.parallel" "clojure.core.reducers"]})
