(ns gh-pages.fs
  (:import (java.nio.file Paths)))

(defn cwd []
  (System/getProperty "user.dir"))

(defn path-join [^String p & ps]
  (str (.normalize (Paths/get p (into-array String ps)))))
