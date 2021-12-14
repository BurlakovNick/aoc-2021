(ns aoc-2021.day14
  (:require [aoc-2021.core :refer [sum slurp-strings parse-ints]]))

(defn parse []
  (let [lines (slurp-strings "day14.txt")
        [start _ & rest] lines
        insertions (->> rest
                        (mapv #(let [[from [to]] (clojure.string/split % #" -> ")]
                                 [(into [] from) to]))
                        (into {}))]
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
              (++= result cnt [[left mid] [mid right]])))
          {} freq))

(defn unzip-counts [freq]
  (reduce
    (fn [res [[a b] cnt]] (++= res cnt [a b]))
    {(first start) 1, (last start) 1}
    freq))

(let [zipped (zip start)
      result (nth (iterate insert zipped) 40)
      unzipped (unzip-counts result)
      freq (sort (vals unzipped))]
  (/ (- (last freq) (first freq)) 2))