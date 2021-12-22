(ns aoc-2021.day22
  (:require [aoc-2021.core :refer [slurp-strings abs sum irange]]))

(defn parse []
  (->> (slurp-strings "day22.txt")
       (mapv #(first (re-seq #"(\w+) x=(-?\d+)..(-?\d+),y=(-?\d+)..(-?\d+),z=(-?\d+)..(-?\d+)" %)))
       (mapv (fn [[_ cmd lx rx ly ry lz rz]]
               [(if (= cmd "on") 1 0)
                [(Integer/parseInt lx) (Integer/parseInt rx)]
                [(Integer/parseInt ly) (Integer/parseInt ry)]
                [(Integer/parseInt lz) (Integer/parseInt rz)]]))))

(defn inside? [[x y z] [lx rx] [ly ry] [lz rz]]
  (and
    (>= x lx)
    (<= x rx)
    (>= y ly)
    (<= y ry)
    (>= z lz)
    (<= z rz)))

(defn is-on? [[x y z] commands]
  (let [last-command (last (filterv (fn [[_ dx dy dz]] (inside? [x y z] dx dy dz)) commands))]
    (if (some? last-command)
      (= 1 (first last-command))
      false)))

(defn unique [xs] (into [] (sort (into #{} (flatten xs)))))
(defn dense-axis [commands n]
  (unique (mapv (fn [[l r]] [l (inc r)]) (mapv #(nth % n) commands))))

(defn densed-points [commands]
  (into [] (mapv #(dense-axis commands %) (range 1 4))))

(defn count-on [commands]
  (let [[xs ys zs] (densed-points commands)]
    (sum (for [[lx rx] (partition 2 1 xs)
               [ly ry] (partition 2 1 ys)
               [lz rz] (partition 2 1 zs)
               :let [volume (* (- rx lx) (- ry ly) (- rz lz))]
               :when (is-on? [lx ly lz] commands)]
           volume))))

"Easy"
(defn inside-init? [v] (every? #(<= (abs %) 50) (flatten v)))
(count-on (filterv inside-init? (parse)))

"Hard"
(count-on (parse))