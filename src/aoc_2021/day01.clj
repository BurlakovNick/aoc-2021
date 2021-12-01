(ns aoc-2021.day01
  (:require [aoc-2021.core :refer [sum]]))

(defn parse
  []
  (as-> (slurp "tests/day01.txt") $
        (clojure.string/split $ #"\n")
        (map #(Integer/parseInt %) $)))

(defn count-inc
  [seq]
  (->> seq
        (partition 2 1)
        (filter (fn [args] (apply < args)))
        (count)))

"Easy"
(count-inc (parse))

"Hard"
(->> (parse)
     (partition 3 1)
     (map sum)
     (count-inc))

