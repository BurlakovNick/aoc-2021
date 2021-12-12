(ns aoc-2021.day12
  (:require [aoc-2021.core :refer [sum slurp-strings]]
            [clojure.string :as str]))

(defn parse []
  (let [forward (mapv #(clojure.string/split % #"-") (slurp-strings "day12.txt"))
        backward (mapv (fn [[a b]] [b a]) forward)
        edges (into #{} (concat backward forward))]
    [(into #{} (flatten forward)) edges]))

(def input (parse))
(def caves (sort (first input)))
(def edges (second input))

(defn is-small? [x] (= x (str/lower-case x)))
(defn neighbors [v] (filterv #(contains? edges [v %]) caves))

(defn inc-visits [visited v]
  (if (is-small? v)
   (let [old (get visited v 0)]
     (assoc visited v (inc old)))
   visited))

(defn max-visits [problem visited cur]
  (cond
    (= cur "start") 1
    (= problem "Easy") 1
    (and (= problem "Hard") (some #{2} (vals visited))) 1
    :else 2))

(defn count-paths [cur visited problem]
  (let [max (max-visits problem visited cur)]
   (cond
     (= cur "end") 1
     (>= (get visited cur 0) max) 0
     :else (let [visited' (inc-visits visited cur)]
             (sum (mapv #(count-paths % visited' problem) (neighbors cur)))))))

(count-paths "start" {} "Easy")

(count-paths "start" {} "Hard")