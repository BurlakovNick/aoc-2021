(ns aoc-2021.day05
  (:require [aoc-2021.core :refer [sum slurp-strings parse-ints]]))

(defn parse [] (mapv parse-ints (slurp-strings "day05.txt")))

(defn is-diagonal? [lx ly rx ry] (and (not= lx rx) (not= ly ry)))
(defn not-is-diagonal? [lx ly rx ry] (not (is-diagonal? lx ly rx ry)))

(defn smart-range [left right]
  (if (< left right)
    (range left (inc right))
    (reverse (range right (inc left)))))

(defn points [lx ly rx ry]
  (if (is-diagonal? lx ly rx ry)
    (mapv vector (smart-range lx rx) (smart-range ly ry))
    (for [x (smart-range lx rx)
          y (smart-range ly ry)]
      [x y])))

(defn count-common-points [segments]
  (->> segments
       (mapv #(apply points %))
       (reduce concat)
       (frequencies)
       (filter (fn [[_ v]] (> v 1)))
       (count)))

(count-common-points (filterv #(apply not-is-diagonal? %) (parse)))
(count-common-points (parse))