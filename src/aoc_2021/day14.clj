(ns aoc-2021.day14
  (:require [aoc-2021.core :refer [sum slurp-strings parse-ints]]))

(defn parse []
  (let [lines (slurp-strings "day14.txt")
        start (first lines)
        insertions (into {} (mapv #(let [[[l r] [rep]] (clojure.string/split % #" -> ")]
                                     [[l r] rep])
                                  (drop 2 lines)))]
    [start insertions]))

(def input (parse))
(def start (first input))
(def insertions (second input))

(defn zip [line] (frequencies (into [] (partition 2 1 line))))
(defn += [freq k v] (update freq k (fnil + 0) v))
(defn ++= [freq v keys] (reduce (fn [res k] (+= res k v)) freq keys))

(defn insert [freq]
  (reduce (fn [result [[left right] cnt]]
            (let [mid (get insertions [left right])]
              (++= result cnt [[mid right] [left mid]])))
          {} freq))

(defn unzip-counts [freq]
  (let [counts (reduce
                 (fn [res [[a b] cnt]] (++= res cnt [a b]))
                 {} freq)
        fixed (++= counts 1 [(first start) (last start)])]
    (into {} (mapv (fn [[k v]] [k (quot v 2)]) fixed))))


(let [zipped (zip start)
      result (nth (iterate insert zipped) 40)
      unzipped (unzip-counts result)
      freq (sort (vals unzipped))]
  (- (last freq) (first freq)))