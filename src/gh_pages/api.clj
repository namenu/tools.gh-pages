(ns gh-pages.api
  (:require [gh-pages.git :as git]
            [gh-pages.fs :as fs]))


(defn clear-slate
  "`git rm` every files under `dest`"
  [dest]
  (print "removing files")
  (let [path (fs/path-join (fs/cwd) dest)
        files (->> (file-seq (clojure.java.io/file path))
                   (map str))]
    (git/rm files)))

(defn copy-files [files base-path dest]
  (print "copying files")
  (let [path (fs/path-join (fs/cwd) dest)])
  )

(defn publish
  "push a git branch to a remote

  options:
    :add - do not clean dir before publish, default to false
  "
  [dir options]

  ;; option

  ;; dir 이 있는지 확인
  ;; 없으면
  ;; (throw (ex-info "dir 오류"))


  ;; 싱크할 대상 구함 glob
  ;; 아무것도 없으면
  ;; (throw (ex-info "배포할 파일이 없음"))

  ;; 메인 플로우
  ;; 1. user를 구해서 (옵션 제공) + throw ex
  ;; 2. repo를 구함 (옵션 제공) + throw ex
  ;; 3. git clean ???
  ;; 4. git fetch
  ;; 5. git checkout
  ;; 6. (optional) git deleteRef (branch)
  ;; 7. remove files at dest
  ;; 8. copying files from src to dest
  ;;   - globbed files: basePath/dotfiles?
  ;; 9. git add all
  ;; 10. user.name, user.email 확인 (1번)
  ;; 11. git commit ("Updates")
  ;; 12. git push


  )

(let [url (git/remote-url)]
  (git/fetch url))
