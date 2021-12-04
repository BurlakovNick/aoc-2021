(ns aoc-2021.day04
  (:require [aoc-2021.core :refer [sum slurp-strings parse-ints find-first find-last transpose is-in?]]))

(defn parse []
  (let [[drawn-str _ & rest] (slurp-strings "day04.txt")
        drawn (parse-ints drawn-str)
        boards-strs (mapv #(parse-ints %) (filter not-empty rest))
        boards (partition 5 5 boards-strs)]
    [drawn boards]))

(defn is-row-bingo? [drawn row]
  (every? #(is-in? drawn %) row))

(defn is-bingo? [drawn boards]
  (some #(is-row-bingo? drawn %) (concat boards (transpose boards))))

(defn rounds-to-win [drawn board]
  (find-first (fn [round] (is-bingo? (take round drawn) board))
              (range 1 (inc (count drawn)))))

(defn winning-board [drawn boards strategy]
  (apply strategy #(rounds-to-win drawn %) boards))

(defn non-drawn-numbers [drawn board round]
  (let [actually-drawn (into #{} (take round drawn))
        numbers (flatten board)]
    (filterv #(not (contains? actually-drawn %)) numbers)))

(defn play [strategy]
  (let [[drawn boards] (parse)
        board (winning-board drawn boards strategy)
        round (rounds-to-win drawn board)
        last-number (nth drawn (dec round))
        non-drawn-on-board (non-drawn-numbers drawn board round)]
    (* (sum non-drawn-on-board) last-number)))

"Easy"
(play min-key)
(play max-key)