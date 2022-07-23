(ns gh-pages.api
  (:require [gh-pages.git :as git]
            [gh-pages.fs :as fs]))

(def ^:private cached-dir ".cache/gh-pages")

;; workflow:
;; 9. git add all
;; 10. user.name, user.email 확인 (1번)
;; 11. git commit with message
;; 12. git push
(defn publish
  "push a git branch to a remote

  options:
    :base-dir (required) - source directory contains contents to deploy
    :repo - git url to publish, default to origin remote of current dir
    :remote - git remote to publish, default to 'origin'
    :branch - git branch to publish, default to 'gh-pages'
    :dest - destination dir within the destination branch. default to root.
    :add - do not clean dir before publish, default to false
    :message - commit message
  "
  [{:keys [base-dir repo remote branch dest message] :as _args
    :or   {remote  "origin"
           branch  "gh-pages"
           dest    "."
           message "Update"}}]
  (when-not (fs/dir? base-dir)
    (throw (ex-info "base directory does not exists" {})))

  ;; 싱크할 대상 구함 glob
  ;; 아무것도 없으면
  ;; (throw (ex-info "배포할 파일이 없음"))
  (let [target-files (fs/visible-file-seq base-dir)]
    (when-not (seq target-files)
      (throw (ex-info "no files to sync" {})))

    (let [user (git/user)
          repo (or repo (git/remote-url remote))]
      (when-not user
        (throw (ex-info "git user info required" {})))
      (when-not repo
        (throw (ex-info "git remote url required" {})))

      (git/shallow-clone cached-dir repo remote branch)

      (git/with-dir cached-dir
        (git/clean)
        (git/fetch remote)
        (git/checkout-orphaned remote branch)

        (git/rm dest)

        (let [dest-dir (fs/path-join cached-dir dest)]
          (fs/copy-files target-files base-dir dest-dir))

        (git/add-all)
        (git/commit message)
        (git/push remote branch)
        )

      )))


(defn clean
  "clean cache"
  []
  (fs/rm-r cached-dir))


(comment
  (publish {:base-dir "dist"
            :repo     "https://github.com/namenu/dummy"
            :branch   "main"})

  (clean)
  )
