(ns aoc-2021.day07
  (:require [aoc-2021.core :refer [sum slurp-strings parse-ints sum abs]]))

(defn parse [] (parse-ints (first (slurp-strings "day07.txt"))))

(defn dist [x y] (abs (- x y)))
(defn dist2 [x y] (let [n (dist x y)] (* n (/ (+ 1 n) 2))))
(defn fuel [target x] (sum (mapv #(dist target %) x)))
(defn fuel2 [target x] (sum (mapv #(dist2 target %) x)))

(defn min-fuel [f x] (apply min (mapv #(f % x) (range 0 2000))))

(min-fuel fuel (parse))
(min-fuel fuel2 (parse))