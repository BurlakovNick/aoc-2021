(ns aoc-2021.day07
  (:require [aoc-2021.core :refer [sum slurp-strings parse-ints sum abs]]))

(defn parse [] (parse-ints (first (slurp-strings "day07.txt"))))

(defn dist [x y] (abs (- x y)))
(defn dist2 [x y] (let [n (dist x y)] (* n (/ (+ 1 n) 2))))

(defn fuel [f target x] (sum (mapv #(f target %) x)))

(defn min-fuel [f x] (apply min (mapv #(fuel f % x) (range 0 2000))))

(min-fuel dist (parse))
(min-fuel dist2 (parse))
