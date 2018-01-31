# glicko2

Implementation of the Glicko-2 rating algorithm. See http://www.glicko.net/glicko/glicko2.pdf.

## Getting started

Add the necessary dependency to your project:

```clojure
[ageneau/glicko2 "0.1.2"]
```

## Usage

```clojure
(ns glicko2.core-test
  (:require [glicko2.core :as glicko2]))

(let [
      ;; First we define the rating parameters for the players we want to consider for the rating period
      players {:player1 {:rating 1500
                         :rd 200
                         :vol 0.06}
               :player2 {:rating 1400
                         :rd 30
                         :vol 0.06}
               :player3 {:rating 1550
                         :rd 100
                         :vol 0.06}
               :player4 {:rating 1700
                         :rd 300
                         :vol 0.06}}

      ;; The list of game results for the rating period
      results [{:player1 :player1
                :player2 :player2
                :result glicko2/POINTS_FOR_WIN}
               {:player1 :player3
                :player2 :player1
                :result glicko2/POINTS_FOR_WIN}
               {:player1 :player4
                :player2 :player1
                :result glicko2/POINTS_FOR_WIN}]

      ;; Reasonable choices are between 0.3 and 1.2
      tau 0.5

      ;; The compute-rating function returns an array of players with updated ratings
      new-ratings (glicko2/compute-ratings players results tau)

      ;; The expected-game-outcome function returns a value between 0 and 1 representing the expected fractional score of a game
      expected-outcome-game1 (glicko2/expected-game-outcome (get players :player1) (get players :player2))]

  [{:desc "Updated rating for player1"
    :value (glicko2/get-rating new-ratings :player1)}
   {:desc "Updated rating deviation for player2"
    :value (glicko2/get-rating-deviation new-ratings :player2)}
   {:desc "Updated volatility for player2"
    :value (glicko2/get-volatility new-ratings :player2)}
   {:desc "All updated ratings"
    :value new-ratings}
   {:desc "Expected score of a game between player1 and player2"
    :value expected-outcome-game1}])
```

## Tests

To run the Clojure tests

    lein test

To run the Clojurescript tests using NodeJS

    lein do javac, doo node node-test

To run the Clojurescript tests using http://phantomjs.org/

    lein do javac, doo phantom test

## License

Copyright &copy; 2018 Sylvain Ageneau

This project is licensed under the [BSD 2-Clause "Simplified" License][license].

[license]: https://opensource.org/licenses/BSD-2-Clause
