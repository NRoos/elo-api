(ns elo-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]))

(use '[ring.middleware.json :as middleware]
     '[ring.middleware.defaults :refer :all]
     '[ring.util.response :only [response]])

(def rank-multiplier 32)

(defn expected-value
  [playerA playerB]
  (/ 1
     (+ 1
        (Math/pow 10
                  (/ (- playerB playerA)
                     400)))))

(defn new-elo
  [current-elo expected actual]
  (+
   current-elo
   (* rank-multiplier
      (- actual
         expected))))

(defn play
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
