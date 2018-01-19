(ns glicko2.math
  (:require [clojure.algo.generic.math-functions :as math]))

(defn sqrt [x]
  "Square root"
  (math/sqrt x))

(defn expt [base pow]
  "(expt base pow) is base to the pow power."
  (math/pow base pow))

(def PI
  "The double value that is closer than any other to pi, the ratio of the circumference of a circle to its diameter."
  Math/PI)

(defn log [x]
  "Returns the natural logarithm (base e) of x double value."
  (Math/log x))

(defn abs [x]
  "(abs x) is the absolute value of x"
  (math/abs x))

(defn exp [x]
  "Returns Euler's number e raised to the power of x double value."
  (Math/exp x))

(defn approx= [x y eps]
  "Return true if the absolute value of the difference between x and y
   is less than eps."
  (math/approx= x y eps))
