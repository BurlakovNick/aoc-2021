(ns aoc-2021.day09
  (:require [aoc-2021.core :refer [sum slurp-strings parse-ints sum mul]]))

(defn parse [] (slurp-strings "day09.txt"))
(def heights (parse))
(def n (count heights))
(def m (count (first heights)))
(def points (for [x (range 0 n) y (range 0 m)] [x y]))

(defn get-height [[x y]]
  (Character/digit (nth (nth heights x) y) 10))

(defn is-in? [[x y]]
  (and (>= x 0) (< x n) (>= y 0) (< y m)))

(defn grid-neighbors [[x y]]
 (->> [[0 -1] [0 1] [-1 0] [1 0]]
      (mapv #(mapv + [x y] %))
      (filterv is-in?)))

(defn is-low-point? [[x y]]
  (let [neighbors (grid-neighbors [x y])
        neighbor-heights (mapv get-height neighbors)
        current-height (get-height [x y])]
    (every? #(< current-height %) neighbor-heights)))

"Easy"
(sum (mapv #(+ 1 (get-height %)) (filterv is-low-point? points)))

"Hard"
(defn dfs [[x y] visited]
  (if (contains? visited [x y])
    #{}
    (let [visited' (conj visited [x y])]
      (->> (grid-neighbors [x y])
           (filterv #(< (get-height [x y]) (get-height %)))
           (filterv #(< (get-height %) 9))
           (mapv #(dfs % visited'))
           (reduce clojure.set/union visited')))))

(mul (take-last 3 (sort (mapv count (mapv #(dfs % #{}) (filterv is-low-point? points))))))