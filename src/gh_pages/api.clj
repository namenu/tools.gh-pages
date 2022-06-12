(ns gh-pages.api
  (:require [gh-pages.git :as git]))


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
  ;;
  ;; git clean ???
  ;; git fetch
  ;; git checkout


  )

(let [url (git/remote-url)]
  (git/fetch url))
