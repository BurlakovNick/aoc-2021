(ns aoc-2021.day25
  (:require [aoc-2021.core :refer [slurp-strings int->ch]]))

(defn parse [] (slurp-strings "day25.txt"))
(def n (count (parse)))
(def m (count (first (parse))))
(def coords (for [x (range n) y (range m)] [x y]))
(defn pprint [grid] (println (clojure.string/join "\n" grid)))

(defn modify [[x y] ch grid]
  (update grid x (fn [row] (apply str (assoc (vec row) y ch)))))

(defn east [[x y]] [x (mod (inc y) m)])
(defn south [[x y]] [(mod (inc x) n) y])

(defn move-east [grid p]
  (->> grid
       (modify p \.)
       (modify (east p) \>)))

(defn move-south [grid p]
  (->> grid
       (modify p \.)
       (modify (south p) \v)))

(defn move [grid]
  (let [grid' (->> coords
                   (filterv #(= \> (get-in grid %)))
                   (filterv #(= \. (get-in grid (east %))))
                   (reduce move-east grid))
        grid' (->> coords
                   (filterv #(= \v (get-in grid' %)))
                   (filterv #(= \. (get-in grid' (south %))))
                   (reduce move-south grid'))]
    grid'))

(->> (iterate move (parse))
     (partition 2 1)
     (take-while (fn [[prev next]] (not= prev next)))
     (count)
     (inc))