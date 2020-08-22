(ns elo-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]))

(use '[ring.middleware.json :as middleware]
     '[ring.middleware.defaults :refer :all]
     '[ring.util.response :only [response]])

(def rank-multiplier 32)

(defn expected-value
  "Calculates the expected score of playerA.
  In a game where a win is worth 1 point, a draw is 0.5 points and a loss is worth 0 points.
  A difference of 200 elo should translate to about 0.75 which means A should win 75% of the time."
  [playerA playerB]
  (/ 1
     (+ 1
        (Math/pow 10
                  (/ (- playerB playerA)
                     400)))))

(defn new-elo
  "Calculates the new elo-score based on current elo, expected score and the actual result."
  [current-elo expected actual]
  (+
   current-elo
   (* rank-multiplier
      (- actual
         expected))))

(defn play
  "Calculates the new elo-ratings for player a and player b.
  game should be a map object with the following form:
  {
    a: <Current elo-rating of player a, as an integer>
    b: <Current elo-rating of player b, as an integer>
    result: <the result of the game as an integer, 1 meaning A won, 0.5 meaning a draw and 0 for B's victory>.
  }"
  [game]
  (let [a (game "a")
        b (game "b")
        result (game "result")
        new-a (new-elo a
                       (expected-value a b)
                       result)
        new-b (new-elo b
                       (expected-value b a)
                       (- 1 result))]
    {:a (Math/round (double new-a))
     :b (Math/round (double new-b))}))

(defroutes app-routes
  (GET "/" [] (response {:msg "Hello World!"}))
  (POST "/" req (response (play (req :body))))
  (route/not-found "Not Found"))

(def app
  (->
    app-routes
    (middleware/wrap-json-body)
    (middleware/wrap-json-response)
    (wrap-defaults api-defaults)))
