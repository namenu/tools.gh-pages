;; not a general git wrapping interface
(ns gh-pages.git
  (:require [clojure.string :as str])
  )

(defn printerrln [& msgs]
  (binding [*out* *err*]
    (apply println msgs)))

;; from tools.gitlibs
(defn- run-git
  [& args]
  (let [command-args (cons "git" args)]
    ;(apply printerrln command-args)
    (let [proc-builder (ProcessBuilder. ^java.util.List command-args)
          proc         (.start proc-builder)
          exit         (.waitFor proc)
          out          (slurp (.getInputStream proc))
          err          (slurp (.getErrorStream proc))]
      {:args command-args, :exit exit, :out out, :err err})))

(defn fetch [url]
  (run-git "fetch" url))

(defn checkout
  "remote에 해당하는 ref가
   없으면, --orphan 옵션을 두어 마지막 커밋만 체크아웃
   있으면, clean & reset
  "
  ([]
   (checkout "origin" "gh-pages"))
  ([remote branch]
   (let [ref    (str remote "/" branch)
         _      (println "Checking out" ref)
         result (run-git "ls-remote" "--exit-code" "." ref)]
     (if (= 0 (:exit result))
       ;; already exists
       (do
         (run-git "checkout" branch)
         (run-git "clean" "-f" "-d")
         (run-git "reset" "--hard" ref))
       ;; fresh checkout
       (run-git "checkout" "--orphan" branch)
       ))))

(defn rm [files]
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

(add-all)
(run-git "diff-index" "--quiet" "HEAD")
(commit "commit test")

(defn user []
  (let [git-name  (run-git "config" "user.name")
        git-email (run-git "config" "user.email")]
    (if (= 0 (:exit git-name) (:exit git-email))
      {::user  (str/trim (:out git-name))
       ::email (str/trim (:out git-email))})))

(defn remote-url []
  (let [remote  "origin"
        git-url (run-git "config" "--get" (str "remote." remote ".url"))]
    (if (= 0 (:exit git-url))
      (str/trim (:out git-url)))))

(run-git "config" "--get" "remote.origin.url")

(fetch (remote-url))