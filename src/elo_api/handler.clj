(ns elo-api.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]))

(use '[ring.middleware.json :only [wrap-json-params]]
     '[ring.middleware.defaults :only [api-defaults]]
     '[ring.util.response :only [response]])

(def rank-multiplier 32)

(defn format-result
  [label old-val new-val]
  (str label ": " (Math/round (double new-val))
       " (" (if (pos? (- new-val old-val))
              "+"
              "")
       (Math/round (double (- new-val old-val)))
       ")"))

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
    (str
      (format-result "A" a new-a)
      "\n"
      (format-result "B" b new-b)
      "\n")))

(defroutes app-routes
  (GET "/" [] "hello")
  (POST "/" req (play (req :params)))
  (route/not-found "Not Found"))

(def app
  (wrap-json-params app-routes api-defaults))
