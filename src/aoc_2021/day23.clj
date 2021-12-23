(ns aoc-2021.day23
  (:require [aoc-2021.core :refer [slurp-strings abs sum irange]]))

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
    (= y (case amphipod
           \A 3
           \B 5
           \C 7
           \D 9))))

(defn hallway? [[x y]] (= x 1))
(def hallway (into [] (for [y [1 2 4 6 8 10 11]] [1 y])))
(def all-chambers (into [] (for [x [2 3] y [3 5 7 9]] [x y])))
(defn final-chamber [amphipod] (filterv #(final-chamber? amphipod %) all-chambers))

(defn allowed-final-chamber [grid amphipod]
  (let [finals (final-chamber amphipod)]
    (if (every? #(contains? #{\. amphipod} (get-in grid %)) finals)
      finals
      [])))

(defn amphipods [grid]
  (for [x (irange 1 3) y (irange 1 11)
        :let [cell (get-in grid [x y] \#)]
        :when (amphipod? cell)]
    [x y]))

(defn dfs [grid from to visited len]
  (cond
    (and (= from to)) len
    (contains? visited from) nil
    :else
    (let [neighbors (filter #(= \. (get-in grid %)) (grid-neighbors grid from))
          visited' (conj visited from)]
      (first (filterv #(some? %) (mapv #(dfs grid % to visited' (inc len)) neighbors))))))

(defn can-move [grid from to] (some? (dfs grid from to #{} 0)))

(def amphi-cost {\A 1 \B 10 \C 100 \D 1000})
(defn move-cost [grid from to]
  (let [len (dfs grid from to #{} 0)
        amphi (get-in grid from)]
    (* len (get amphi-cost amphi))))

(defn allowed-to-move [grid [x y]]
  (let [amphipod (get-in grid [x y])]
    (cond
      (not (amphipod? amphipod)) []
      (chamber? [x y]) (->> (concat hallway (allowed-final-chamber grid amphipod))
                            (remove #(= [1 y] %))
                            (filterv #(can-move grid [x y] %)))
      (hallway? [x y]) (->> (allowed-final-chamber grid amphipod)
                            (filterv #(can-move grid [x y] %))))))

(defn update-min [[cost queue] k v]
  (if (not (contains? cost k))
    [(assoc cost k v) (conj queue [v k])]
    (if (< (cost k) v)
      [cost queue]
      [(assoc cost k v) (conj (disj queue [(cost k) k]) [v k])])))

(def final-state ["#############" "#...........#" "###A#B#C#D###" "  #A#B#C#D#" "  #########"])

(defn move [grid from to]
  (let [amphi (get-in grid from)]
    (modify (modify grid from \.) to amphi)))

(defn find-min-path [grid]
  (loop [cost {grid 0} queue (sorted-set [0 grid]) i 0]
    (if (= i 1000000)
      -100
      (let [[[cur-cost grid] & _] queue]
        (pprint grid)
        (println cur-cost)
        (cond
          (not (some? grid)) (cost final-state)
          (= grid final-state) (cost final-state)
          :else (let [new-states (for [amphi (amphipods grid)
                                       to (allowed-to-move grid amphi)
                                       :let [new-cost (+ cur-cost (move-cost grid amphi to))
                                             new-grid (move grid amphi to)]]
                                   [new-grid new-cost])
                      [cost' queue'] (reduce
                                       (fn [res [new-grid new-cost]]
                                         (update-min res new-grid new-cost))
                                       [cost queue] new-states)]
                  (recur cost' (disj queue' [(cost grid) grid]) (inc i))))))))

(find-min-path (parse))
(pprint (modify (parse) [1 1] \E))
(pprint (parse))