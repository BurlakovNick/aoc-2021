(ns aoc-2021.day08
  (:require [aoc-2021.core :refer [sum slurp-strings parse-ints sum abs]])
  (:require [clojure.math.combinatorics :refer [permutations]]))

(slurp-strings "day08.txt")

(defn parse-line [str]
  (as-> (clojure.string/split str #" ") $
        (split-with #(not= "|" %) $)
        (let [[digits tests] $]
          [(apply vector digits) (filterv #(not= "|" %) tests)])))

(defn parse [] (mapv parse-line (slurp-strings "day08.txt")))

"Easy"
(let [lines (parse)
      tests (flatten (mapv (fn [[_ b]] b) lines))]
  (count (filter #(contains? #{2 7 4 3} (count %)) tests)))

"Hard"
(def digit-codes {"abcefg" \0, "cf" \1, "acdeg" \2, "acdfg" \3, "bcdf" \4, "abdfg" \5, "abdefg" \6, "acf" \7, "abcdefg" \8, "abcdfg" \9})

(defn get-digit
  ([code]
   (get digit-codes (apply str (sort code))))
  ([code mapping]
   (get-digit (apply str (mapv mapping code)))))

(defn get-char [i] (char (+ (int \a) i)))
(defn to-mapping [perm] (into {} (map-indexed (fn [i, ch] [ch (get-char i)]) perm)))

(defn is-valid? [perm digits]
  (let [mapping (to-mapping perm)]
    (every? #(some? (get-digit % mapping)) digits)))

(defn decipher [digits]
  (let [perms (permutations "abcdefg")]
    (first (filter #(is-valid? % digits) perms))))

(defn solve [digits tests]
  (let [mapping (to-mapping (decipher digits))
        solved (mapv #(get-digit % mapping) tests)]
    (apply str solved)))

(sum (mapv #(Integer/parseInt (apply solve %)) (parse)))