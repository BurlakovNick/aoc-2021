(ns aoc-2021.day18
  (:require [clojure.math.combinatorics :refer [permuted-combinations]]))

(defn parse [] (load-file "resources/day18.txt"))

(defn find-leaf
  ([v pred] (first (find-leaf v pred 0 0)))
  ([v pred pos depth]
   (if (integer? v)
     (if (pred v pos depth)
       [{:found v :pos pos :depth depth} (inc pos)]
       [nil (inc pos)])
     (let [[left right] v
           [found pos] (find-leaf left pred pos (inc depth))]
       (if (some? found)
         [found pos]
         (find-leaf right pred pos (inc depth)))))))

(defn replace-subtree
  ([pred new-val v] (first (replace-subtree pred new-val 0 v)))
  ([pred new-val pos v]
   (if (integer? v)
     [(if (pred pos pos) new-val v) (inc pos)]
     (let [from-pos pos
           [left right] v
           [left-result pos] (replace-subtree pred new-val pos left)
           [right-result pos] (replace-subtree pred new-val pos right)]
       (if (pred from-pos pos)
         [new-val pos]
         [[left-result right-result] pos])))))

(defn nth-value [v n] (:found (find-leaf v (fn [v pos depth] (= pos n))) 0))
(defn find-explodes [v] (find-leaf v (fn [v pos depth] (>= depth 5))))
(defn find-split [v] (find-leaf v (fn [v pos depth] (>= v 10))))

(defn replace-nth [n new-val v]
  (replace-subtree (fn [l r] (and (= l n) (= r n))) new-val v))

(defn replace-explosion [n new-val v]
  (replace-subtree (fn [l r] (and (= l n) (= r (+ 2 n)))) new-val v))

(defn explode [v]
  (let [exploding (find-explodes v)]
    (if (not exploding)
      v
      (let [prev-pos (dec (:pos exploding))
            next-pos (+ 2 (:pos exploding))
            prev-val (+ (nth-value v (:pos exploding)) (nth-value v prev-pos))
            next-val (+ (nth-value v (inc (:pos exploding))) (nth-value v next-pos))]
        (->> v
             (replace-nth prev-pos prev-val)
             (replace-nth next-pos next-val)
             (replace-explosion (:pos exploding) 0))))))

(defn split [v]
  (let [splitting (find-split v)]
    (if (not splitting)
      v
      (let [val (:found splitting)
            left (quot val 2)
            right (+ left (mod val 2))]
        (replace-nth (:pos splitting) [left right] v)))))

(defn reduce-number [number]
  (let [exploded (explode number) splitted (split number)]
    (cond
      (not= exploded number) (reduce-number exploded)
      (not= splitted number) (reduce-number splitted)
      :else number)))

(defn sum [numbers] (reduce (fn [l r] (reduce-number [l r])) numbers))

(defn magnitude [v]
  (if (integer? v)
    v
    (+ (* 3 (magnitude (first v))) (* 2 (magnitude (second v))))))

"Easy"
(magnitude (sum (parse)))

"Hard"
(let [pairs (permuted-combinations (parse) 2)]
  (apply max (map #(magnitude (sum %)) pairs)))