(ns gh-pages.fs
  (:require [clojure.java.io :as jio])
  (:import (java.nio.file Paths Path)
           (java.io File)))

(defn dir? [path]
  (let [f (-> path str jio/file)]
    (.isDirectory f)))

(defn cwd []
  (System/getProperty "user.dir"))

(defn path-join
  [^String p & ps]
  (str (.normalize (Paths/get p (into-array String ps)))))

(defn visible-file-seq
  "traverse only files that are not hidden."
  [dir]
  (let [root  (-> dir str jio/file)]
    (->> (tree-seq (fn [^File f] (.isDirectory f))
                   (fn [^File d] (->> (seq (.listFiles d))
                                      (remove #(.isHidden %))))
                   root)
         (remove #(or (.isHidden %) (.isDirectory %))))))

(defn copy-file [from to]
  (println "copy-file" from to)
  (let [to (jio/file to)]
    (jio/make-parents to)
    (jio/copy (jio/file from) to)))

(defn copy-files
  "
  base-path 하위에 있는 files 목록 (상대적인 위치)를 받아서,
  dest-dir 하위에 복사한다.
  "
  [files base-dir dest-dir]
  (let [base-path  (Path/of (.toURI (jio/file (str base-dir))))
        relativize (fn [p]
                     (-> base-path
                         (.relativize (Paths/get (.toURI p)))
                         (str)))]
    (println "copying files:" base-dir "->" dest-dir)
    (->> files
         (run! (fn [src]
                 (let [dest (path-join dest-dir (relativize src))]
                   (copy-file src dest)))))))

(defn rm-r [f & [silently]]
  (let [f (jio/file f)]
    (if (.isDirectory f)
      (dorun (map #(rm-r % silently) (.listFiles f))))
    (jio/delete-file f silently)))