(ns aoc-2021.day13
  (:require [aoc-2021.core :refer [sum slurp-strings parse-ints]]))

(defn parse []
  (let [lines (slurp-strings "day13.txt")
        points (take-while #(not= "" %) lines)
        folds (drop (inc (count points)) lines)
        parsed-points (into #{} (mapv parse-ints points))
        parsed-folds (mapv #(let [[type mid] (clojure.string/split % #"=")]
                              [type (Integer/parseInt mid)]) folds)]
    [parsed-points parsed-folds]))

(defn fold-point [[x y] [type mid]]
  (cond
    (and (= type "fold along x") (> x mid)) [(- mid (- x mid)) y]
    (and (= type "fold along y") (> y mid)) [x (- mid (- y mid))]
    :else [x y]))

(defn fold [points cmd]
  (into #{} (mapv #(fold-point % cmd) points)))

"Easy"
(let [[points folds] (parse)]
  (count (fold points (first folds))))

"Hard"
(let [[points folds] (parse)
      folded (reduce fold points folds)
      draw (for [x (range 10) y (range 50)] (if (contains? folded [y x]) \# \.))
      lines (mapv #(apply str %) (partition 50 draw))]
  lines)
"ZKAUCFUC"