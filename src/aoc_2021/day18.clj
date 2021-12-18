(ns aoc-2021.day18
  (:require [aoc-2021.core :refer [sum]]))

(defn parse [] (load-file "resources/day18.txt"))

(defn find-leaf
  ([v pred] (find-leaf v pred 0 0))
  ([v pred pos depth]
   (if (integer? v)
     (if (pred v pos depth) {:found v :pos pos :depth depth} {:next-pos (inc pos)})
     (loop [i 0 pos pos]
       (if (= i (count v))
         {:next-pos pos}
         (let [item (nth v i)
               result (find-leaf item pred pos (inc depth))
               pos (:next-pos result)]
           (if (contains? result :found)
             result
             (recur (inc i) pos))))))))

(defn replace-subtree
  ([pred new-val v] (:result (replace-subtree pred new-val 0 v)))
  ([pred new-val pos v]
   (if (integer? v)
     {:result   (if (pred pos pos) new-val v)
      :next-pos (inc pos)}
     (let [left pos]
       (loop [i 0 pos pos result []]
         (if (= i (count v))
           {:result   (if (pred left pos) new-val result)
            :next-pos pos}
           (let [item (nth v i)
                 child (replace-subtree pred new-val pos item)
                 pos (:next-pos child)]
             (recur (inc i) pos (conj result (:result child))))))))))

(defn find-nth-leaf [v n]
  (:found (find-leaf v (fn [v pos depth] (= pos n))) 0))

(defn find-explodes [v]
  (let [found (find-leaf v (fn [v pos depth] (>= depth 5)))]
    (if (contains? found :found) found nil)))

(defn find-split [v]
  (let [found (find-leaf v (fn [v pos depth] (>= v 10)))]
    (if (contains? found :found) found nil)))

(defn replace-nth-leaf [n new-val v]
  (replace-subtree (fn [l r] (and (= l n) (= r n))) new-val v))

(defn replace-explosion [n new-val v]
  (replace-subtree (fn [l r] (and (= l n) (= r (+ 2 n)))) new-val v))

(defn explodes [v]
  (let [exploding (find-explodes v)]
    (if (not exploding)
      v
      (let [prev-pos (dec (:pos exploding))
            next-pos (+ 2 (:pos exploding))
            left-explode-val (find-nth-leaf v (:pos exploding))
            right-explode-val (find-nth-leaf v (inc (:pos exploding)))
            prev-val (+ left-explode-val (find-nth-leaf v prev-pos))
            next-val (+ right-explode-val (find-nth-leaf v next-pos))]
        (->> v
             (replace-nth-leaf prev-pos prev-val)
             (replace-nth-leaf next-pos next-val)
             (replace-explosion (:pos exploding) 0))))))

(defn split [v]
  (let [splitting (find-split v)]
    (if (not splitting)
      v
      (let [pos (:pos splitting)
            val (:found splitting)
            left (quot val 2)
            right (+ left (mod val 2))]
        (replace-nth-leaf pos [left right] v)))))

(defn reduce-number [number]
  (let [number' (explodes number)]
    (if (not= number' number)
      (reduce-number number')
      (let [number' (split number)]
        (if (not= number' number)
          (reduce-number number')
          number')))))

(defn add [left right] [left right])

(defn sum-of [numbers]
  (reduce (fn [l r] (reduce-number (add l r))) (first numbers) (drop 1 numbers)))

(defn magnitude [v]
  (if (integer? v)
    v
    (let [[l r] v]
      (+ (* 3(magnitude l)) (* 2 (magnitude r))))))

"Easy"
(magnitude (sum-of (parse)))

"Hard"
(let [numbers (parse)
      n (count numbers)
      pairs (for [i (range 0 n) j (range 0 n) :when (not= i j)]
              [(nth numbers i) (nth numbers j)])]
  (apply max (map #(magnitude (sum-of %)) pairs)))