(ns aoc-2021.day22
  (:require [aoc-2021.core :refer [slurp-strings abs sum irange]]))

(defn parse []
  (->> (slurp-strings "day22.txt")
       (mapv #(first (re-seq #"(\w+) x=(-?\d+)..(-?\d+),y=(-?\d+)..(-?\d+),z=(-?\d+)..(-?\d+)" %)))
       (map-indexed (fn [[_ cmd lx rx ly ry lz rz]]
                      [(if (= cmd "on") 1 0)
                       [(Integer/parseInt lx) (Integer/parseInt rx)]
                       [(Integer/parseInt ly) (Integer/parseInt ry)]
                       [(Integer/parseInt lz) (Integer/parseInt rz)]]))))

(defn filter-axis [commands n x]
  (filter (fn [command] (let [[l r] (nth command n)]
                          (and (>= x l) (<= x r))))
          commands))

(defn filter-x [commands x] (filter-axis commands 1 x))
(defn filter-y [commands x] (filter-axis commands 2 x))
(defn filter-z [commands x] (filter-axis commands 3 x))

(defn is-on? [command]
  (if (some? command)
    (= 1 (first command))
    false))

(defn unique [xs] (into [] (sort (into #{} (flatten xs)))))
(defn dense-axis [commands n]
  (unique (mapv (fn [[l r]] [l (inc r)]) (mapv #(nth % n) commands))))

(defn densed-points [commands]
  (into [] (mapv #(dense-axis commands %) (range 1 4))))

(defn count-on [commands]
  (let [[xs ys zs] (densed-points commands)]
    (sum (for [[lx rx] (partition 2 1 xs)
               :let [commands-by-x (filter-x commands lx)]
               [ly ry] (partition 2 1 ys)
               :let [commands-by-xy (filter-y commands-by-x ly)]
               [lz rz] (partition 2 1 zs)
               :let [commands-by-xyz (filter-z commands-by-xy lz)
                     volume (* (- rx lx) (- ry ly) (- rz lz))]
               :when (is-on? (last commands-by-xyz))]
           volume))))

"Easy"
(defn inside-init? [v] (every? #(<= (abs %) 50) (flatten v)))
(count-on (filterv inside-init? (parse)))

"Hard"
(count-on (parse))