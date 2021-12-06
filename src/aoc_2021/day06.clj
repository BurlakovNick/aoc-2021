(ns aoc-2021.day06
  (:require [aoc-2021.core :refer [sum slurp-strings parse-ints sum]]))

(defn parse [] (parse-ints (first (slurp-strings "day06.txt"))))

(defn += [freq k v] (update freq k (fnil + 0) v))

(defn simulate [freq]
  (reduce-kv (fn [res timer count]
               (if (= timer 0)
                 (+= (+= res 6 count) 8 count)
                 (+= res (dec timer) count)))
          {} freq))

(defn play [day]
  (sum (vals (nth (iterate simulate (frequencies (parse))) day))))

(play 80)
(play 256)