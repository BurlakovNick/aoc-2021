(ns aoc-2021.day02
  (:require [aoc-2021.core :refer [sum parse-strings]]))

(defn parse-cmd [str]
  (let [[dir step] (clojure.string/split str #" ")]
    (into [] [dir (Integer/parseInt step)])))

(defn parse []
  (->> (parse-strings "day02.txt")
       (map parse-cmd)))

(defn move [[x y aim] [dir step]]
  (case dir
    "forward" [(+ x step) (+ y (* step aim)) aim]
    "down" [x y (+ aim step)]
    "up" [x y (- aim step)]))

(->> (parse)
     (reduce move [0 0 0])
     (apply (fn [x y aim] [(* x aim) (* x y)])))
