(ns elo-api.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [elo-api.handler :refer :all]))

(deftest test-app
  (testing "GET /"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200))
      (is (= (:body response) "{\"msg\":\"Hello World!\"}"))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))

(deftest test-new-elo
  (testing "1500 expected 0.75, result win"
    (is (= (new-elo 1500 0.75 1) 1504)))
  (testing "1500 expected 0.75, result draw"
    (is (= (new-elo 1500 0.75 0.5) 1496)))
  (testing "1500 expected 0.75, result loss"
    (is (= (new-elo 1500 0.75 0) 1488))))

(deftest test-expected-value
  (testing "difference of +200 should result in a value of 0.76"
    (is (= (expected-value 1500 1300) 0.76)))
  (testing "Difference of +100 should result in a value of 0.64"
    (is (= (expected-value 1500 1400) 0.64)))
  (testing "Difference of -200 should result in a value of 0.24"
    (is (= (expected-value 1500 1700) 0.24)))
  (testing "Difference of -100 should result in a value of 0.36"
    (is (= (expected-value 1500 1600) 0.36)))
  (testing "difference of 0 should result in a value of 0.5"
    (is (= (expected-value 1500 1500) 0.5)))
  (testing "difference of +400 should result in a value of 0.91"
    (is (= (expected-value 1900 1500) 0.91)))
  (testing "difference of +600 should result in a value of 0.91"
    (is (= (expected-value 2100 1500) 0.91)))
  (testing "difference of -400 should result in a value of 0.09"
    (is (= (expected-value 1500 2100) 0.09)))
  (testing "difference of -600 should result in a value of 0.09"
    (is (= (expected-value 1500 2100) 0.09))))

(deftest test-to-two-digits
  (testing "0.8890 should result in 0.89"
    (is (= (to-two-digits 0.8890) 0.89)))
  (testing "10 should result in 10.0"
    (is (= (to-two-digits 10) 10.0)))
  (testing "1.234 should result in 1.23"
    (is (= (to-two-digits 1.234) 1.23))))


(deftest test-play
  (testing "1500 playerA winning a 1500 playerB should result in playerA having
           1508 points and playerB at 1492 points"
    (is (= (play 1500 1500 1) {:a 1508 :b 1492})))
  (testing "1500 playerA losing to a 1500 playerB should result in playerA having
           1492 points and playerB at 1508 points"
    (is (= (play 1500 1500 0) {:a 1492 :b 1508})))
  (testing "1600 playerA winning a 1500 playerB should result in playerA having
           1606 points and playerB at 1494 points"
    (is (= (play 1600 1500 1) {:a 1606 :b 1494})))
  (testing "1600 playerA losing to a 1500 playerB should result in playerA having
           1590 points and playerB at 1510 points"
    (is (= (play 1600 1500 0) {:a 1590 :b 1510})))
  (testing "two 1500 players drawing should result in both still having 1500 points"
    (is (= (play 1500 1500 0.5) {:a 1500 :b 1500}))))
