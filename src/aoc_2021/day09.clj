(ns aoc-2021.day09
  (:require [aoc-2021.core :refer [sum slurp-strings parse-ints sum mul ch->int]])
  (:require [clojure.set]))

(defn parse [] (slurp-strings "day09.txt"))
(def heights (parse))
(def n (count heights))
(def m (count (first heights)))
(def points (for [x (range 0 n) y (range 0 m)] [x y]))

(defn get-height [p] (ch->int (get-in heights p)))
(defn is-in? [[x y]] (and (>= x 0) (< x n) (>= y 0) (< y m)))

(defn grid-neighbors [p]
  (->> [[0 -1] [0 1] [-1 0] [1 0]]
       (mapv #(mapv + p %))
       (filterv is-in?)))

(defn is-low-point? [p]
  (let [neighbors (grid-neighbors p)
        neighbor-heights (mapv get-height neighbors)
        current-height (get-height p)]
    (every? #(< current-height %) neighbor-heights)))

"Easy"
(sum (mapv #(inc (get-height %)) (filterv is-low-point? points)))

"Hard"
(defn dfs [pos visited]
  (if (contains? visited pos)
    #{}
    (let [visited' (conj visited pos)]
      (->> (grid-neighbors pos)
           (filterv #(< (get-height %) 9))
           (reduce
             (fn [vis pos'] (clojure.set/union (dfs pos' vis) vis))
             visited')))))

(mul (take-last 3 (sort (mapv count (mapv #(dfs % #{}) (filterv is-low-point? points))))))