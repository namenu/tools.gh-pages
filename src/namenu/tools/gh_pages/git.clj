;; not a general git wrapping interface
(ns namenu.tools.gh-pages.git
  (:require [clojure.java.io :as jio]
            [clojure.string :as str]
            [namenu.tools.gh-pages.fs :as fs])
  (:import (java.io File)))

;; git working dir
(def ^:dynamic ^File *dir* nil)

(defn printerrln [& msgs]
  (binding [*out* *err*]
    (apply println msgs)))

;; from tools.gitlibs
(defn- run-git
  [& args]
  (let [command-args (cons "git" (map str args))]
    (apply printerrln command-args)
    (let [proc-builder (ProcessBuilder. ^java.util.List command-args)
          _            (.directory proc-builder *dir*)
          proc         (.start proc-builder)
          exit         (.waitFor proc)
          out          (slurp (.getInputStream proc))
          err          (slurp (.getErrorStream proc))]
      {:args command-args, :exit exit, :out out, :err err})))

(defmacro with-dir [dir & forms]
  `(binding [*dir* (jio/file ~dir)]
     ~@forms))

(defn shallow-clone
  "(shallow)clone repo into dir.
  if the dir already exists, make ensure its remote correctly targeted."
  [dir repo remote branch]
  #_(if (fs/dir? dir)
      ;; TODO: verify if remote url directs repo url
      (assert true)
      )
  (jio/make-parents dir)
  ;; TODO: can't clone when there's no such branch
  (run-git "clone" repo dir
           "--branch" branch
           "--single-branch"
           "--origin" remote
           "--depth" 1))

(defn fetch [url]
  (run-git "fetch" (str url)))

(defn clean []
  (run-git "clean" "-f" "-d"))

(defn checkout-orphaned
  "remote에 해당하는 ref가
   없으면, --orphan 옵션을 두어 마지막 커밋만 체크아웃
   있으면, clean & reset
  "
  [remote branch]
  (let [branch (str branch)
        ref    (str remote "/" branch)
        _      (println "Checking out" ref)
        result (run-git "ls-remote" "--exit-code" "." ref)]
    (if (= 0 (:exit result))
      ;; already exists
      (do
        (run-git "checkout" branch)
        (clean)
        (run-git "reset" "--hard" ref))
      ;; fresh checkout
      (run-git "checkout" "--orphan" branch)
      )))

(defn rm [& files]
  (when (seq files)
    (apply run-git "rm" "--ignore-unmatch" "-r" "-f" files)))

(defn add-all []
  (run-git "add" "."))

(defn commit
  "commit if there are any changes"
  [message]
  (printerrln "committing")
  (let [has-diff? (not= 0 (:exit (run-git "diff-index" "--quiet" "HEAD")))]
    (if has-diff?
      (run-git "commit" "-m" message))))

(defn push
  "ex) git push --tags origin gh-pages"
  [remote branch]
  (run-git "push" #_"--tags" remote branch))

(defn user []
  (let [git-name  (run-git "config" "user.name")
        git-email (run-git "config" "user.email")]
    (if (= 0 (:exit git-name) (:exit git-email))
      {::user  (str/trim (:out git-name))
       ::email (str/trim (:out git-email))})))

(defn remote-url [remote]
  (let [git-url (run-git "config" "--get" (str "remote." remote ".url"))]
    (if (= 0 (:exit git-url))
      (str/trim (:out git-url)))))


(comment
  (run-git "config" "--get" "remote.origin.url")

  (fetch (remote-url)))