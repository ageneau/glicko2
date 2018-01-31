(ns glicko2.core
  (:require [glicko2.math :as math]
            [glicko2.specs :as specs]
            #?(:clj [clojure.spec.alpha :as s]
               :cljs [cljs.spec.alpha :as s])
            #?(:clj [clojure.spec.test.alpha :as stest])))

(def DEFAULT_RATING 1500)
(def DEFAULT_DEVIATION 350)
(def DEFAULT_VOLATILITY 0.06)
(def DEFAULT_TAU 0.75)
(def MULTIPLIER 173.7178)
(def CONVERGENCE_TOLERANCE 0.000001)

(def POINTS_FOR_WIN 1)
(def POINTS_FOR_LOSS 0)
(def POINTS_FOR_DRAW 0.5)


(defn convert-rating-to-original-glicko-scale[rating]
  (+ (* rating MULTIPLIER)
     DEFAULT_RATING))

(defn convert-rating-to-glicko2-scale[rating]
  (/ (- rating DEFAULT_RATING)
     MULTIPLIER))

(defn convert-rating-deviation-to-original-glicko-scale[rd]
  (* rd MULTIPLIER))

(defn convert-rating-deviation-to-glicko2-scale[rd]
  (/ rd MULTIPLIER))

(defn get-glicko2-rating[players player]
  (convert-rating-to-glicko2-scale (:rating (get players player))))

(defn get-glicko2-rating-deviation[players player]
  (convert-rating-deviation-to-glicko2-scale (:rd (get players player))))

(defn get-rating[players player]
  (:rating (get players player)))

(defn get-rating-deviation[players player]
  (:rd (get players player)))

(defn get-volatility [players player]
  (:vol (get players player)))

(defn participated? [result player]
  (or (= (:player1 result) player)
      (= (:player2 result) player)))

(defn get-results [results player]
  (filter #(participated? % player) results))

(defn get-participants [results]
  (into #{}
        (flatten
         (map (fn [{:keys [player1 player2]}] [player1 player2])
              results))))

(defn opponent [result player]
  (if (= (:player1 result) player)
    (:player2 result)
    (:player1 result)))

(defn score [result player]
  (if (= (:player1 result) player)
    (:result result)
    (- POINTS_FOR_WIN (:result result))))

(defn g [deviation]
  (/ 1
     (math/sqrt (+ 1
                   (* 3 (/ (math/expt deviation 2)
                           (math/expt Math/PI 2)))))))

(defn e
  "expected fractional score of a game"
  [player-rating opp-rating opp-deviation]
  (/ 1
     (+ 1
        (math/exp (* -1
                     (g opp-deviation)
                     (- player-rating opp-rating))))))


(defn v
  "estimated variance of the team’s/player’s
  rating based only on game outcomes."
  [players player results]
  (math/expt (loop [results results
                    v 0.0]
               (if (seq results)
                 (let [result (first results)
                       opponent (opponent result player)
                       e (e (get-glicko2-rating players player)
                            (get-glicko2-rating players opponent)
                            (get-glicko2-rating-deviation players opponent))
                       incv (* (math/expt (g (get-glicko2-rating-deviation players opponent)) 2)
                               e
                               (- 1 e))]
                   (recur (next results)
                          (+ v incv)))
                 v))
             -1))

(defn f [x delta phi v a tau]
  (- (/ (* (math/exp x)
           (- (math/expt delta 2)
              (math/expt phi 2)
              v
              (math/exp x)))
        (* 2
           (math/expt (+ (math/expt phi 2)
                         v
                         (math/exp x))
                      2)))
     (/ (- x a)
        (math/expt tau 2))))


(defn outcome-based-rating [players player results]
  (loop [results results
         outcome-based-rating 0.0]
    (if (seq results)
      (let [result (first results)
            opponent (opponent result player)
            score (score result player)
            inc-outcome (* (g (get-glicko2-rating-deviation players opponent))
                           (- score
                              (e (get-glicko2-rating players player)
                                 (get-glicko2-rating players opponent)
                                 (get-glicko2-rating-deviation players opponent))))]
        (recur (next results)
               (+ outcome-based-rating
                  inc-outcome)))
      outcome-based-rating)))

(defn delta
  "estimated improvement in rating"
  [players player results]
  (* (v players player results)
     (outcome-based-rating players player results)))

(defn calculate-new-rd [phi sigma]
  (math/sqrt (+ (math/expt phi 2)
                (math/expt sigma 2))))

(defn calculate-new-rating [players player results tau]
  (let [phi (get-glicko2-rating-deviation players player)
        sigma (get-volatility players player)
        a (math/log (math/expt sigma 2))
        delta (delta players player results)
        v (v players player results)
        a- a
        b- (if (> (math/expt delta 2) (+ (math/expt phi 2) v))
             (math/log (- (math/expt delta 2)
                          (math/expt phi 2)
                          v))
             (loop [k 1
                    b- (- a (* k (math/abs tau)))]
               (if (< (f b- delta phi v a tau) 0)
                 (recur (inc k)
                        (- a (* k (math/abs tau))))
                 b-)))
        fa (f a- delta phi v a tau)
        fb (f b- delta phi v a tau)]
    (loop [a- a-
           b- b-
           fa fa
           fb fb]
      (if (> (math/abs (- b- a-)) CONVERGENCE_TOLERANCE)
        (let [c- (+ a- (/ (* (- a- b-) fa)
                          (- fb fa)))
              fc (f c- delta phi v a tau)]
          (if (neg? (* fc fb))
            (recur b- c- fb fc)
            (recur a- c- (/ fa 2) fc)))
        (let [new-sigma (math/exp (/ a- 2))
              phi-star (calculate-new-rd phi new-sigma)
              new-phi (/ 1
                         (math/sqrt (+ (/ 1 (math/expt phi-star 2))
                                       (/ 1 v))))]
          {:vol new-sigma
           :rating (convert-rating-to-original-glicko-scale
                    (+ (get-glicko2-rating players player)
                       (* (math/expt new-phi 2)
                          (outcome-based-rating players player results))))
           :rd (convert-rating-deviation-to-original-glicko-scale new-phi)})))))

(defn expected-game-outcome
  "expected fractional score of a game"
  [player1 player2]
  (e (convert-rating-to-glicko2-scale (:rating player1))
     (convert-rating-to-glicko2-scale (:rating player2))
     (convert-rating-deviation-to-glicko2-scale (:rd player2))))

(defn compute-ratings
  "Compute the updated ratings after a rating period.
  returns a list of players with updated ratings"
  [players results tau]
  (s/assert (:fn (s/get-spec `compute-ratings)) {:args {:players players
                                                        :results results
                                                        :tau tau }})
  (let [participants (get-participants results)]
    (into (empty players)
          (for [[player-id player] players]
            (vector player-id
                    (if (contains? participants player-id)
                      (let [res (get-results results player-id)]
                        (if (pos? (count res))
                          (calculate-new-rating players player-id res tau)
                          {:rating (convert-rating-to-original-glicko-scale (get-glicko2-rating players player-id))
                           :rd (convert-rating-deviation-to-original-glicko-scale (calculate-new-rd (get-glicko2-rating-deviation players player-id)
                                                                                                    (get-volatility players player-id)))
                           :vol (get-volatility players player-id)}))
                      player))))))

#?(:clj (stest/instrument (stest/instrumentable-syms)))

