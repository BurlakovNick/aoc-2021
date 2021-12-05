(ns aoc-2021.day05
  (:require [aoc-2021.core :refer [sum slurp-strings parse-ints irange]]))

(defn parse [] (mapv parse-ints (slurp-strings "day05.txt")))

(defn is-diagonal? [lx ly rx ry] (and (not= lx rx) (not= ly ry)))
(defn not-is-diagonal? [lx ly rx ry] (not (is-diagonal? lx ly rx ry)))

(defn points [lx ly rx ry]
  (let [xs (irange lx rx) ys (irange ly ry)]
    (if (is-diagonal? lx ly rx ry)
      (mapv vector xs ys)
      (for [x xs y ys] [x y]))))

(defn count-common-points [segments]
  (->> segments
       (mapv #(apply points %))
       (reduce concat)
       (frequencies)
       (filter (fn [[_ v]] (> v 1)))
       (count)))

(count-common-points (filterv #(apply not-is-diagonal? %) (parse)))
(count-common-points (parse))