(ns aoc-2021.day06
  (:require [aoc-2021.core :refer [sum slurp-strings parse-ints sum]]))

(defn parse [] (parse-ints (first (slurp-strings "day06.txt"))))

(defn += [freq k v]
  (let [old (get freq k 0)]
    (assoc freq k (+ old v))))

(defn simulate [freq]
  (reduce (fn [res [timer count]]
            (if (= timer 0)
              (+= (+= res 6 count) 8 count)
              (+= res (dec timer) count)))
          {} freq))

(defn play [day]
  (sum (map (fn [[_ count]] count) (nth (iterate simulate (frequencies (parse))) day))))

(play 80)
(play 256)