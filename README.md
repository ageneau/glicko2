# glicko2

Implementation of the Glicko-2 rating algorithm. See http://www.glicko.net/glicko/glicko2.pdf.

## Getting started

Add the necessary dependency to your project:

```clojure
[ageneau/glicko2 "0.1.0"]
```

## Usage

```clojure
(let [
      ;; First we define the rating parameters for the players we want to consider for the rating period
      player1 {:rating 1500
               :rd 200
               :vol 0.06}
      player2 {:rating 1400
               :rd 30
               :vol 0.06}

      player3 {:rating 1550
               :rd 100
               :vol 0.06}

      player4 {:rating 1700
               :rd 300
               :vol 0.06}

      ;; the indexes of the players in this vector are later used when defining game results
      players [player1
               player2
               player3
               player4]

      ;; The list of game results for the rating period
      results [{:player1 0
                :player2 1
                :result glicko2/POINTS_FOR_WIN}
               {:player1 2
                :player2 0
                :result glicko2/POINTS_FOR_WIN}
               {:player1 3
                :player2 0
                :result glicko2/POINTS_FOR_WIN}]]

  ;; The compute-rating function returns an array of player with updated ratings
  (glicko2/get-rating (glicko2/compute-ratings players results 0.5) 0))
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
