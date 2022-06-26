(ns gh-pages.api
  (:require [clojure.java.io :as jio]
            [gh-pages.git :as git]
            [gh-pages.fs :as fs]))

(defn- clear-slate
  "`git rm` every files under `dest`"
  [dest]
  (print "removing files")
  (let [path  (fs/path-join (fs/cwd) dest)
        files (->> (file-seq (jio/file path))
                   (map str))]
    (git/rm files)))

(defn- copy-files
  "
  base-path 하위에 있는 files 목록 (상대적인 위치)를 받아서,
  dest-dir 하위에 복사한다.
  "
  [files _base-path dest-dir]
  (println "copying files")
  (let [dest-dir (fs/path-join (fs/cwd) dest-dir)]
    (->> files
         (run! (fn [src]
                 (let [dest (fs/path-join dest-dir src)]
                   (fs/copy-file src dest)))))))

(defn publish
  "push a git branch to a remote

  workflow:
    1. user를 구해서 (옵션 제공) + throw ex
    2. repo를 구함 (옵션 제공) + throw ex
    3. git clean ???
    4. git fetch
    5. git checkout origin gh-pages
    6. (optional) git deleteRef (branch)
    7. remove files at dest
    8. copying files from src to dest
      - globbed files: basePath/dotfiles?
    9. git add all
    10. user.name, user.email 확인 (1번)
    11. git commit with message
    12. git push

  options:
    :base-dir (required) - source directory contains contents to deploy
    :remote - git remote to publish, default to 'origin'
    :branch - git branch to publish, default to 'gh-pages'
    :dest - destination dir within the destination branch. default to root.
    :add - do not clean dir before publish, default to false
    :message - commit message
  "
  [{:keys [base-dir remote branch dest message] :as _args
    :or   {remote  "origin"
           branch  "gh-pages"
           dest    "."
           message "Update"}}]
  (when-not (fs/dir? base-dir)
    (throw (ex-info "base directory does not exists" {})))

  ;; 싱크할 대상 구함 glob
  ;; 아무것도 없으면
  ;; (throw (ex-info "배포할 파일이 없음"))
  (let [target-files (fs/visible-file-seq (jio/file base-dir))]
    (when-not (seq target-files)
      (throw (ex-info "no files to sync" {})))

    (let [user    (git/user)
          repo    (git/remote-url)]
      (when-not user
        (throw (ex-info "git user info required" {})))
      (when-not repo
        (throw (ex-info "git remote url required" {})))

      (git/clean)
      (git/fetch repo)
      (git/checkout-orphaned remote branch)

      (clear-slate dest)
      (copy-files target-files base-dir dest)

      (git/add-all)
      (git/commit message)
      (git/push remote branch)

      ))

  )

(comment
  (publish {:base-dir "dist"})

  (let [url (git/remote-url)]
    (git/fetch url)))
