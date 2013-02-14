# tuna

Tuna serves your music files, so you can play it on your web browser. (It is inspired by [zmusic-ng.](http://git.zx2c4.com/zmusic-ng/about/))

## Prerequisites

You will need [Leiningen][1] 2 installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein deps
    lein cljsbuild once
    lein ring server

## License

Copyright © 2013 Samrat Man Singh
