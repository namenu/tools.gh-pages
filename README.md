# tools.gh_pages

깃헙의 `gh_pages` 브랜치에 파일들을 배포합니다.
npm의 gh_pages 와 동일한 기능을 하는 것을 목표로 하지만, 클로저의 tools 를 사용합니다.


## install

```sh
clj -Ttools install-latest :lib io.github.namenu/tools.gh-pages :as gh-pages
```

## usage

the list of commands

```sh
clj -A:deps -Tgh-pages help/dir
```

docstrings

```sh
clj -A:deps -Tgh-pages help/doc 
```

```sh
clj -Tgh-pages publish :base-dir dist
```
