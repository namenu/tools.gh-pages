# tools.gh-pages

Publish static files to a `gh_pages` branch or another to serve [GitHub Pages](https://pages.github.com/)


## install

```sh
clj -Ttools install-latest :lib io.github.namenu/tools.gh-pages :as gh-pages
```

## usage

publish contents of `/dist` to `main` branch

```sh
clj -Tgh-pages publish :base-dir dist :branch main :repo https://github.com/some-org/some-repo
```

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


### Prior Art

- https://github.com/tschaub/gh-pages
