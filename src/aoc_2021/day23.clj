(ns aoc-2021.day23
  (:require [aoc-2021.core :refer [slurp-strings irange]]))

(defn parse [] (slurp-strings "day23.txt"))

(defn modify [grid [x y] ch] (update grid x (fn [row] (apply str (assoc (vec row) y ch)))))
(defn pprint [grid] (println (clojure.string/join "\n" grid)))

(defn grid-neighbors [grid p]
  (->> [[0 -1] [0 1] [-1 0] [1 0]]
       (mapv #(mapv + p %))
       (filterv #(not= \# (get-in grid %)))))

(defn amphipod? [ch] (contains? #{\A \B \C \D} ch))
(defn chamber? [[x y]] (> x 1))
(defn final-chamber? [amphipod [x y]]
  (and
    (> x 1)
    (= y (case amphipod \A 3 \B 5 \C 7 \D 9))))

(defn hallway? [[x y]] (and (= x 1) (contains? #{1 2 4 6 8 10 11} y)))

(defn amphipods [grid]
  (for [x (irange 1 (dec (count grid))) y (irange 1 11)
        :let [cell (get-in grid [x y] \#)]
        :when (amphipod? cell)]
    [x y]))

(defn finalized-chamber? [grid [x y]]
  (let [amphi (get-in grid [x y])
        down (get-in grid [(inc x) y])]
    (and (amphipod? amphi)
         (final-chamber? amphi [x y])
         (or (= down \#) (finalized-chamber? grid [(inc x) y])))))

(defn dfs [grid from visited len]
  (cond
    (contains? visited from) visited
    :else (let [neighbors (filter #(= \. (get-in grid %)) (grid-neighbors grid from))
                visited' (assoc visited from len)]
            (reduce (fn [visited' to] (dfs grid to visited' (inc len)))
                    visited' neighbors))))

(defn reachable [grid from] (dfs grid from {} 0))

(def amphi-cost {\A 1 \B 10 \C 100 \D 1000})

(defn allowed-to-move [grid [x y]]
  (let [amphipod (get-in grid [x y])]
    (cond
      (not (amphipod? amphipod)) []
      (finalized-chamber? grid [x y]) []
      (chamber? [x y]) (->> (reachable grid [x y])
                            (filter (fn [[p _]] (hallway? p))))
      (hallway? [x y]) (->> (reachable grid [x y])
                            (filter (fn [[[nx ny] _]]
                                      (and
                                        (final-chamber? amphipod [nx ny])
                                        (or (= \# (get-in grid [(inc nx) ny]))
                                            (finalized-chamber? grid [(inc nx) ny])))))))))

(defn update-min [[cost queue] [k v]]
  (if (not (contains? cost k))
    [(assoc cost k v) (conj queue [v k])]
    (if (< (cost k) v)
      [cost queue]
      [(assoc cost k v) (conj (disj queue [(cost k) k]) [v k])])))

(defn move [grid amphi from to] (modify (modify grid from \.) to amphi))

(defn find-min-path [grid final]
  (loop [cost {grid 0} queue (sorted-set [0 grid]) i 0]
    (let [[[cur-cost grid] & _] queue]
      (if (= 0 (mod i 1000))
        (println cur-cost))
      (cond
        (empty? queue) -1
        (= grid final) (cost final)
        :else (let [[cost' queue']
                    (reduce update-min [cost queue]
                            (for [from (amphipods grid)
                                  [to len] (allowed-to-move grid from)
                                  :let [amphi (get-in grid from)
                                        new-cost (+ cur-cost (* len (get amphi-cost amphi)))
                                        new-grid (move grid amphi from to)]]
                              [new-grid new-cost]))]
                (recur cost' (disj queue' [(cost grid) grid]) (inc i)))))))


(def sample ["#############"
             "#...........#"
             "###B#C#B#D###"
             "  #A#D#C#A#"
             "  #########"])

(def final ["#############"
            "#...........#"
            "###A#B#C#D###"
            "  #A#B#C#D#"
            "  #########"])

(def hard-final ["#############"
                 "#...........#"
                 "###A#B#C#D###"
                 "  #A#B#C#D#"
                 "  #A#B#C#D#"
                 "  #A#B#C#D#"
                 "  #########"])

(defn hardify [grid]
  (into [] (concat (take 3 grid) ["  #D#C#B#A#" "  #D#B#A#C#"] (drop 3 grid))))

"Easy"
(time (find-min-path sample final))
(time (find-min-path (parse) final))

"Hard"
(time (find-min-path (hardify sample) hard-final))
(time (find-min-path (hardify (parse)) hard-final))