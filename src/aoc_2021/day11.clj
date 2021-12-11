(ns aoc-2021.day11
  (:require [aoc-2021.core :refer [sum slurp-strings is-in? sum ch->int int->ch]]))

(def coords (for [x (range 10) y (range 10)] [x y]))
(defn parse-level [lines p] (ch->int (get-in lines p)))

(defn parse []
  (let [lines (slurp-strings "day11.txt")]
    (into {} (mapv (fn [p] [p (parse-level lines p)]) coords))))

"For debugging"
(defn map->str [m] (mapv (partial apply str) (partition 10 (mapv int->ch (mapv (parse) coords)))))

(defn inside? [[x y]] (and (>= x 0) (< x 10) (>= y 0) (< y 10)))

(defn grid-neighbors [p]
  (->> [[0 -1] [0 1] [-1 0] [1 0] [-1 -1] [-1 1] [1 -1] [1 1]]
       (mapv #(mapv + p %))
       (filterv inside?)))

(defn zero-level [m p] (assoc m p 0))
(defn inc-level [m p] (update m p inc))
(defn inc-neighbors [m p] (reduce inc-level m (grid-neighbors p)))
(defn inc-levels [m] (reduce inc-level m coords))
(defn flashes [m] (filterv #(> (m %) 9) coords))
(defn flash-to-zero [m] (reduce zero-level m (flashes m)))

(defn sim [args]
  (let [m (:map args)
        inced (inc-levels m)]
    (loop [visited []
           result inced
           flashes-count 0]
      (let [flash (first (filterv #(not (is-in? visited %)) (flashes result)))]
        (if (some? flash)
          (let [visited' (conj visited flash)
                result' (inc-neighbors result flash)]
            (recur visited' result' (inc flashes-count)))
          {:map (flash-to-zero result), :flashes flashes-count})))))

(def iterations (iterate sim {:map (parse) :flashes 0}))

"Easy"
(sum (mapv :flashes (take 101 iterations)))

"Hard"
(count (take-while #(not= (:flashes %) 100) iterations))