# elo-api

Ring/compojure api for calculating a new elo value after a match between two players.

Equivalent to FIDE elo system, expect the K value is a constant 16 instead of changing based on rating of the players.

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

```lein ring server```
