(ns aoc-2021.day10
  (:require [aoc-2021.core :refer [sum slurp-strings is-in? sum mul median]]))

(defn parse [] (slurp-strings "day10.txt"))

(defn is-opening? [x] (is-in? "[(<{" x))
(def closing {\( \), \[ \], \{ \}, \< \>})
(def score {\) 3, \] 57, \} 1197, \> 25137})
(def score-2 {\) 1, \] 2, \} 3, \> 4})
(defn completion-score [xs] (reduce (fn [r x] (+ (* r 5) (score-2 x))) 0 xs))

(defn spell-check [brackets]
  (loop [open []
         left brackets]
    (if (empty? left)
      (if (empty? open)
        [:ok]
        [:missing-par (mapv closing (reverse open))])
      (let [[cur & rest] left]
        (if (is-opening? cur)
          (recur (conj open cur) rest)
          (if (= (closing (last open)) cur)
            (recur (pop open) rest)
            [:bad-char cur]))))))

(defn find-errors [type]
  (for [[err x] (mapv spell-check (parse))
        :when (= err type)]
    x))

(sum (mapv score (find-errors :bad-char)))
(median (mapv completion-score (find-errors :missing-par)))