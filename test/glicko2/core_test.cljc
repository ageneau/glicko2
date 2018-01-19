(ns glicko2.core-test
  #?(:cljs (:require-macros [cljs.test :refer (is deftest testing)]))
  (:require #?(:clj [clojure.test :refer :all]
               :cljs [cljs.test])
            [glicko2.core :as glicko2]
            [glicko2.math :as math]))

(def DEFAULT_EPS 0.01)

(deftest test-glicko2
  (testing "Glicko2"
    (let [player1 {:rating 1500
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
          
          players [player1
                   player2
                   player3
                   player4]
          
          results [{:player1 0
                    :player2 1
                    :result glicko2/POINTS_FOR_WIN}
                   {:player1 2
                    :player2 0
                    :result glicko2/POINTS_FOR_WIN}
                   {:player1 3
                    :player2 0
                    :result glicko2/POINTS_FOR_WIN}]]
      
      (is (math/approx= 0.844 (glicko2/g 1.1512924985234674) DEFAULT_EPS))
      (is (math/approx= 0.5604 (glicko2/e 0.28782312463086684 0.0 1.1512924985234674) DEFAULT_EPS))
      (is (math/approx= 7.044 (glicko2/v players 3 [{:player1 3
                                                     :player2 0
                                                     :result glicko2/POINTS_FOR_WIN}])
                        DEFAULT_EPS))

      (is (math/approx= 1464.06 (glicko2/get-rating (glicko2/compute-ratings players results 0.5) 0)
                        DEFAULT_EPS)))))
