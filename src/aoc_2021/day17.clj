(ns aoc-2021.day17
  (:require [aoc-2021.core :refer [slurp-strings]]))

(defn parse [] (as-> (first (slurp-strings "day17.txt")) $
                     (re-seq #"target area: x=(-?\d+)..(-?\d+), y=(-?\d+)..(-?\d+)" $)
                     (mapv #(Integer/parseInt %) (drop 1 (first $)))))

(def target (parse))

(defn in-target? [[x y]]
  (let [[lx rx ly ry] target]
    (and (>= x lx) (<= x rx) (>= y ly) (<= y ry))))

(defn dec-vx [x] (if (= x 0) 0 (dec x)))

(defn path [[vx vy]]
  (let [[lx rx ly ry] target]
    (loop [x 0 y 0 vx vx vy vy points []]
      (let [points' (conj points [x y])]
        (cond
          (in-target? [x y]) points'
          (and (= vx 0) (or (< x lx) (> x rx))) points'
          (< y ly) points'
          :else (recur (+ x vx) (+ y vy) (dec-vx vx) (dec vy) points'))))))

(defn hit? [vel] (some in-target? (path vel)))
(defn max-y [vel] (apply max (mapv second (path vel))))

(def velocities (for [vx (range 200) vy (range -200 200)] [vx vy]))

"Easy"
(apply max (map max-y (filter hit? velocities)))

"Hard"
(count (filter hit? velocities))