(ns glicko2.specs
  (:require #?(:clj [clojure.spec.alpha :as s]
               :cljs [cljs.spec.alpha :as s])
            [clojure.set :as set]))


(s/def ::player-id any?)
(s/def ::percentage (s/and number?
                           #(and (<= % 1)
                                 (>= % 0))))
(s/def ::rating number?)
(s/def ::rd number?)
(s/def ::vol number?)

(s/def ::player (s/keys :req-un [::rating
                                 ::rd
                                 ::vol]))

(s/def ::players (s/map-of ::player-id ::player))
(s/def ::player1 ::player-id)
(s/def ::player2 ::player-id)
(s/def ::score ::percentage)
(s/def ::result ::score)

(s/def ::game-result (s/keys :req-un [::player1
                                      ::player2
                                      ::result]))

(s/def ::results (s/coll-of ::game-result))

(s/def ::tau number?)
(s/def ::expected-score ::percentage)

(defn valid-results? [players results]
  (and (set/superset? (set (keys players)) (set (map :player1 results)))
       (set/superset? (set (keys players)) (set (map :player2 results)))))

(s/fdef glicko2.core/convert-rating-to-original-glicko-scale
        :args (s/cat :rating ::rating)
        :ret ::rating)

(s/fdef glicko2.core/convert-rating-to-glicko2-scale
        :args (s/cat :rating ::rating)
        :ret ::rating)

(s/fdef glicko2.core/convert-rating-deviation-to-original-glicko-scale
        :args (s/cat :rd ::rd)
        :ret ::rd)

(s/fdef glicko2.core/convert-rating-deviation-to-glicko2-scale
        :args (s/cat :rd ::rd)
        :ret ::rd)

(s/fdef glicko2.core/get-glicko2-rating
        :args (s/cat :players ::players :key ::player-id)
        :ret ::rating)

(s/fdef glicko2.core/get-glicko2-rating-deviation
        :args (s/cat :players ::players :key ::player-id)
        :ret ::rating)

(s/fdef glicko2.core/get-rating
        :args (s/cat :players ::players :key ::player-id)
        :ret ::rating)

(s/fdef glicko2.core/get-rating-deviation
        :args (s/cat :players ::players :key ::player-id)
        :ret ::rating)

(s/fdef glicko2.core/get-volatility
        :args (s/cat :players ::players :key ::player-id)
        :ret ::vol)

(s/fdef glicko2.core/participated?
        :args (s/cat :result ::game-result :player ::player-id)
        :ret any?)

(s/fdef glicko2.core/get-results
        :args (s/cat :results ::results :player ::player-id)
        :ret ::results)

(s/fdef glicko2.core/get-participants
        :args (s/cat :results ::results)
        :ret (s/coll-of ::player-id :distinct true))

(s/fdef glicko2.core/opponent
        :args (s/cat :result ::game-result
                     :player ::player-id)
        :ret ::player-id)

(s/fdef glicko2.core/score
        :args (s/cat :result ::game-result :player ::player-id)
        :ret ::score)

(s/fdef glicko2.core/g
        :args (s/cat :deviation number?)
        :ret number?)

(s/fdef glicko2.core/e
        :args (s/cat :player-rating ::rating
                     :opp-rating ::rating
                     :opp-deviation ::rd)
        :ret number?)

(s/fdef glicko2.core/v
        :args (s/cat :players ::players
                     :player ::player-id
                     :results ::results)
        :ret number?)

(s/fdef glicko2.core/f
        :args (s/cat :x number?
                     :delta number?
                     :phi number?
                     :v number?
                     :a number?
                     :tau ::tau)
        :ret number?)

(s/fdef glicko2.core/outcome-based-rating
        :args (s/cat :players ::players
                     :player ::player-id
                     :results ::results)
        :ret number?)

(s/fdef glicko2.core/delta
        :args (s/cat :players ::players
                     :player ::player-id
                     :results ::results)
        :ret number?)

(s/fdef glicko2.core/calculate-new-rd
        :args (s/cat :phi number?
                     :sigma number?)
        :ret number?)

(s/fdef glicko2.core/calculate-new-rating
        :args (s/cat :players ::players
                     :player ::player-id
                     :results ::results
                     :tau number?)
        :ret ::player)

(s/fdef glicko2.core/expected-game-outcome
        :args (s/cat :player1 ::player
                     :player2 ::player)
        :ret ::expected-score)

(s/fdef glicko2.core/compute-ratings
        :args (s/cat :players ::players
                     :results ::results
                     :tau ::tau)
        :ret ::players
        :fn (fn [{:keys [args]}]
              (valid-results? (:players args) (:results args))))
