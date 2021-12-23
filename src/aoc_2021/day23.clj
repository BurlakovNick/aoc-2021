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

(defn hallway? [[x y]]
  (and (= x 1) (contains? #{1 2 4 6 8 10 11} y)))

(def hallway (into [] (for [y [1 2 4 6 8 10 11]] [1 y])))
(defn all-chambers [grid] (into [] (for [x (irange 2 (- (count grid) 2)) y [3 5 7 9]] [x y])))
(defn final-chamber [grid amphipod] (filterv #(final-chamber? amphipod %) (all-chambers grid)))

(defn allowed-final-chamber [grid amphipod]
  (let [finals (final-chamber grid amphipod)]
    (if (every? #(contains? #{\. amphipod \#} (get-in grid %)) finals)
      finals
      [])))

(defn amphipods [grid]
  (for [x (irange 1 (dec (count grid))) y (irange 1 11)
        :let [cell (get-in grid [x y] \#)]
        :when (amphipod? cell)]
    [x y]))

(defn mark-final-amphipods [grid]
  (reduce
    (fn [grid [amphi [x y]]]
      (if (and
            (= (get-in grid [x y]) amphi)
            (= (get-in grid [(inc x) y]) \#))
        (modify grid [x y] \#)
        grid))
    grid (for [amphi [\A \B \C \D]
               p (reverse (final-chamber grid amphi))]
           [amphi p])))

(defn dfs [grid from visited len]
  (if
    (contains? visited from) visited
    (let [neighbors (filter #(= \. (get-in grid %)) (grid-neighbors grid from))
          visited' (assoc visited from len)]
      (reduce (fn [visited' to]
                (dfs grid to visited' (inc len)))
              visited' neighbors))))

(defn reachable [grid from] (dfs grid from {} 0))

(def amphi-cost {\A 1 \B 10 \C 100 \D 1000})
(defn move-cost [grid from len]
  (let [amphi (get-in grid from)]
    (* len (get amphi-cost amphi))))

(defn allowed-to-move [grid [x y]]
  (let [grid (mark-final-amphipods grid)
        amphipod (get-in grid [x y])]
    (cond
      (not (amphipod? amphipod)) []
      (chamber? [x y]) (->> (reachable grid [x y])
                            (filter (fn [[p _]] (hallway? p))))
      (hallway? [x y]) (->> (reachable grid [x y])
                            (filter (fn [[[nx ny] _]]
                                      (and
                                        (final-chamber? amphipod [nx ny])
                                        (= \# (get-in grid [(inc nx) ny])))))))))

(defn update-min [[cost queue] k v]
  (if (not (contains? cost k))
    [(assoc cost k v) (conj queue [v k])]
    (if (< (cost k) v)
      [cost queue]
      [(assoc cost k v) (conj (disj queue [(cost k) k]) [v k])])))

(defn move [grid from to]
  (let [amphi (get-in grid from)]
    (modify (modify grid from \.) to amphi)))

(defn find-min-path [grid final]
  (loop [cost {grid 0} queue (sorted-set [0 grid]) i 0]
    (let [[[cur-cost grid] & _] queue]
      (if (= 0 (mod i 1000))
        (println cur-cost))
      (cond
        (not (some? grid)) (cost final)
        (= grid final) (cost final)
        :else (let [new-states (for [amphi (amphipods grid)
                                     [to len] (allowed-to-move grid amphi)
                                     :let [new-cost (+ cur-cost (move-cost grid amphi len))
                                           new-grid (move grid amphi to)]]
                                 [new-grid new-cost])
                    [cost' queue'] (reduce
                                     (fn [res [new-grid new-cost]]
                                       (update-min res new-grid new-cost))
                                     [cost queue] new-states)]
                (recur cost' (disj queue' [(cost grid) grid]) (inc i)))))))

(time (find-min-path ["#############"
                      "#...........#"
                      "###B#C#B#D###"
                      "  #A#D#C#A#"
                      "  #########"]

                     ["#############"
                      "#...........#"
                      "###A#B#C#D###"
                      "  #A#B#C#D#"
                      "  #########"]))

(time (find-min-path (parse)
                     ["#############"
                      "#...........#"
                      "###A#B#C#D###"
                      "  #A#B#C#D#"
                      "  #########"]))

(time (find-min-path (into [] (concat (take 3 (parse)) ["  #D#C#B#A#" "  #D#B#A#C#"] (drop 3 (parse))))
                     ["#############"
                      "#...........#"
                      "###A#B#C#D###"
                      "  #A#B#C#D#"
                      "  #A#B#C#D#"
                      "  #A#B#C#D#"
                      "  #########"]))