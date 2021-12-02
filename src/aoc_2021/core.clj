(ns aoc-2021.core)

(defn sum
  [sequence]
  (reduce + sequence))

(defn parse-ints
  [filename]
  (as-> (slurp (clojure.java.io/resource filename)) $
        (clojure.string/split $ #"\n")
        (map #(Integer/parseInt %) $)))


(defn parse-strings
  [filename]
  (as-> (slurp (clojure.java.io/resource filename)) $
        (clojure.string/split $ #"\n")))