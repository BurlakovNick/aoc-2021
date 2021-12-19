(ns aoc-2021.day19
  (:require [aoc-2021.core :refer [slurp-strings parse-ints]]
            [clojure.set :as set])
  (:require [clojure.math.combinatorics :refer [cartesian-product]]))

(defn parse-block [lines]
  (let [beacons (take-while not-empty (drop 1 lines))]
    [(mapv #(parse-ints % ",") beacons) (drop (inc (count beacons)) lines)]))

(defn parse []
  (loop [result [] lines (slurp-strings "day19.txt")]
    (if (empty? lines)
      result
      (let [[block lines] (parse-block lines)]
        (recur (conj result block) (drop 1 lines))))))

"https://stackoverflow.com/a/16467849"
(defn roll-1 [[x y z]] [x z (- y)])
(defn roll [points] (mapv roll-1 points))
(defn turn-1 [[x y z]] [(- y) x z])
(defn turn [points] (mapv turn-1 points))
(defn rotate [points operation] (operation points))

(defn rotations [points]
  (let [operations (flatten (repeat 3 [roll turn turn turn]))]
    (let [result (reductions rotate points operations)
          points' (roll (turn (roll (last result))))
          result' (reductions rotate points' operations)]
      (into [] (into #{} (concat result result'))))))

(defn add [l r] (mapv + l r))
(defn sub [l r] (mapv - l r))
(defn shift [points offset] (mapv #(add % offset) points))

(defn count-same [left right]
  (count (clojure.set/intersection (into #{} left) (into #{} right))))

(defn overlap [[left-block right-block]]
  (->> (for [rotated (rotations right-block)
             offset-pair (cartesian-product left-block rotated)]
         (let [offset (sub (first offset-pair) (second offset-pair))
               shifted (shift rotated offset)]
           {:left    left-block
            :right   right-block
            :rotated rotated
            :shifted shifted
            :offset  offset
            :common  (count-same left-block shifted)}))
       (filterv (fn [x] (>= (:common x) 12)))
       (first)))

(defn remove-block [b blocks]
  (filterv #(not= b %) blocks))

(defn calc-beacons [blocks]
  (let [[head & blocks] blocks]
    (loop [known [head] blocks blocks]
      (println (count known) (count blocks))
      (if (empty? blocks)
        (into #{} (apply concat known))
        (let [pairs (for [known-block known unknown-block blocks]
                      [known-block unknown-block])
              overlapped (first (filter some? (map overlap pairs)))]
          (recur (conj known (:shifted overlapped)) (remove-block (:right overlapped) blocks)))))))

(count (into #{} (apply concat (parse))))
(count (calc-beacons (parse)))