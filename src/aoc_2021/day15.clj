(ns aoc-2021.day15
  (:require [aoc-2021.core :refer [sum slurp-strings ch->int]]))

(defn parse [] (slurp-strings "day15.txt"))

(def field (parse))
(def input-len (count field))

(defn get-risk [[x y]]
  (let [original (ch->int (get-in field [(mod x input-len) (mod y input-len)]))
        shift (+ (quot y input-len) (quot x input-len))]
    (inc (mod (+ (mod (dec original) 9) shift) 9))))

(defn is-in? [[x y] n] (and (>= x 0) (< x n) (>= y 0) (< y n)))

(defn grid-neighbors [p n]
  (->> [[0 -1] [0 1] [-1 0] [1 0]]
       (mapv #(mapv + p %))
       (filterv #(is-in? % n))))

(defn update-min [[cost queue] k v]
  (if (not (contains? cost k))
    [(assoc cost k v) (conj queue [v k])]
    (if (< (cost k) v)
      [cost queue]
      [(assoc cost k v) (conj (disj queue [(cost k) k]) [v k])])))

(defn find-min-path [n]
  (loop [cost {[0 0] 0} queue (sorted-set [0 [0 0]])]
    (let [[[_ cur] & _] queue]
      (if (not (some? cur))
        (cost [(dec n) (dec n)])
        (let [[cost' queue'] (reduce
                               (fn [res to]
                                 (update-min res to (+ (cost cur) (get-risk to))))
                               [cost queue] (grid-neighbors cur n))]
          (recur cost' (disj queue' [(cost cur) cur])))))))

(find-min-path (count field))
(find-min-path (* 5 (count field)))