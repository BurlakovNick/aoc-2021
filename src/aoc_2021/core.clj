(ns aoc-2021.core)

(defn sum [sequence] (reduce + sequence))
(defn mul [sequence] (reduce * sequence))

(defn parse-ints
  ([str] (parse-ints str #"[, \t\-\>]"))
  ([str sep]
   (->> (clojure.string/split str (re-pattern sep))
        (filter not-empty)
        (mapv #(Integer/parseInt %)))))

(defn slurp-ints
  ([filename]
   (slurp-ints filename "\n"))
  ([filename sep] (as-> (slurp (clojure.java.io/resource filename)) $
                        (parse-ints $ sep))))

(defn slurp-strings
  [filename]
  (as-> (slurp (clojure.java.io/resource filename)) $
        (clojure.string/split $ #"\n")))

(defn find-first
  [f coll]
  (first (filter f coll)))

(defn find-last
  [f coll]
  (last (filter f coll)))

(defn transpose [m]
  (apply mapv vector m))

(defn is-in? [coll item]
  (some #{item} coll))

(defn irange [left right]
  (if (< left right)
    (range left (inc right))
    (reverse (range right (inc left)))))

(defn abs [n] (max n (- n)))

(defn ch->int [x] (Character/digit x 10))

(defn median [coll]
  (let [len (count coll)]
    (nth (sort coll) (quot len 2))))