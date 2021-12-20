(ns aoc-2021.day20
  (:require [aoc-2021.core :refer [slurp-strings abs sum is-in?]]))

(defn parse []
  (let [lines (slurp-strings "day20.txt")
        pattern-lines (take-while #(not= "" %) lines)
        pattern (apply concat pattern-lines)
        image-lines (into [] (drop (inc (count pattern-lines)) lines))
        n (count image-lines)
        m (count (first image-lines))
        points (into {} (for [x (range n) y (range m)] [[x y] (get-in image-lines [x y])]))]
    [pattern points]))

(defn grid-neighbors [p]
  (->> [[-1 -1] [-1 0] [-1 1] [0 -1] [0 0] [0 1] [1 -1] [1 0] [1 1]]
       (mapv #(mapv + p %))))

(defn get-all-neighbors [points]
  (into [] (into #{} (apply concat (mapv grid-neighbors points)))))

(defn get-int [m p default] (if (= \# (get m p default)) 1 0))

(defn get-index [m p default]
  (reduce (fn [l r] (+ (* 2 l) r)) (mapv #(get-int m % default) (grid-neighbors p))))

(defn sim [pattern points default]
  (reduce (fn [m p] (assoc m p (nth pattern (get-index points p default))))
          {} (get-all-neighbors (keys points))))

(defn sim-2 [pattern points]
  (if (= \# (first pattern))
    (sim pattern (sim pattern points \.) \#)
    (sim pattern (sim pattern points \.) \.)))

(defn count-lit [points]
  (count (filter #(= \# %) (vals points))))

"Easy"
(let [[pattern points] (parse)]
  (count-lit (sim-2 pattern points)))

"Hard"
(let [[pattern points] (parse)]
  (count-lit (reduce (fn [p _] (sim-2 pattern p)) points (range 25))))
