(ns gh-pages.fs
  (:require [clojure.java.io :as jio])
  (:import (java.nio.file Paths FileVisitOption)))

(defn dir? [^String path]
  (.isDirectory (jio/file path)))

(defn cwd []
  (System/getProperty "user.dir"))

(defn path-join [^String p & ps]
  (str (.normalize (Paths/get p (into-array String ps)))))

(defn visible-file-seq
  "traverse only files that are not hidden."
  [^java.io.File dir]
  (->> (tree-seq
         (fn [^java.io.File f] (.isDirectory f))
         (fn [^java.io.File d] (->> (seq (.listFiles d))
                                    (remove #(.isHidden %))))
         dir)
       (remove #(or (.isHidden %) (.isDirectory %)))))

(defn copy-file [^String from ^String to]
  (let [to (jio/file to)]
    (println to)
    (when (.mkdirs (.getParentFile to))
      (jio/copy (jio/file from) (jio/file to)))))

(comment
  (def path "/Users/namenu/Development/")
  (time
    (count (visible-file-seq (clojure.java.io/file path))))
  )
