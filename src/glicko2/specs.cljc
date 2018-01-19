(ns glicko2.specs
  (:require #?(:clj [clojure.spec.alpha :as s]
               :cljs [cljs.spec.alpha :as s])))

(s/def ::rating number?)
(s/def ::rd number?)
(s/def ::vol number?)

(s/def ::player (s/keys :req-un [::rating
                                 ::rd
                                 ::vol]))

(s/def ::players (s/coll-of ::player :distinct true))

(s/def ::player-id int?)
(s/def ::player1 ::player-id)
(s/def ::player2 ::player-id)
(s/def ::score number?)
(s/def ::result ::score)

(s/def ::game-result (s/keys :req-un [::player1
                                      ::player2
                                      ::result]))

(s/def ::results (s/coll-of ::game-result))

(s/def ::tau number?)
