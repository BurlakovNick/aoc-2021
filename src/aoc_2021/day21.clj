(ns aoc-2021.day21)

(defn random [n] (inc (mod n 100)))
(defn move [x step] (inc (mod (+ (dec x) step) 10)))

(defn play [x y]
  (loop [x x y y sx 0 sy 0 n 0 p 0]
    (if (or (>= sx 1000) (>= sy 1000))
      [sx sy n]
      (let [dice (+ (random n) (random (+ n 1)) (random (+ n 2)))]
        (if (= p 0)
          (let [x' (move x dice)]
            (recur x' y (+ sx x') sy (+ n 3) 1))
          (let [y' (move y dice)]
            (recur x y' sx (+ sy y') (+ n 3) 0)))))))

"Easy"
(let [[sx sy n] (play 4 8)]
  (* n (min sx sy)))

(let [[sx sy n] (play 5 9)]
  (* n (min sx sy)))

"Hard"
(defn add [l r] (mapv + l r))
(defn sum [xs] (reduce add xs))

(def dices-3
  (let [r (range 1 4)]
    (into [] (for [x r y r z r] (+ x y z)))))

(def play-2
  (memoize
    (fn [x y sx sy p]
      (cond
        (>= sx 21) [1 0]
        (>= sy 21) [0 1]
        (= p 0) (sum (mapv #(play-2 % y (+ sx %) sy 1) (mapv #(move x %) dices-3)))
        (= p 1) (sum (mapv #(play-2 x % sx (+ sy %) 0) (mapv #(move y %) dices-3)))))))

(apply max (play-2 4 8 0 0 0))
(apply max (play-2 5 9 0 0 0))