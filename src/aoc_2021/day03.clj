(ns aoc-2021.day03
  (:require [aoc-2021.core :refer [sum parse-strings]])
  (:require [clojure.string :refer [starts-with?]]))

(defn parse [] (parse-strings "day03.txt"))

(defn transpose [m]
  (apply mapv vector m))

(defn most-common [freq]
  (let [zero-freq (get freq \0 0)
        one-freq (get freq \1 0)]
    (if (> zero-freq one-freq) \0 \1)))

(defn rev [bit] (if (= bit \1) \0 \1))

(defn least-common [freq]
  (rev (most-common freq)))

(defn calc-bit [bit-chooser vec]
  (bit-chooser (frequencies vec)))

(defn parse-binary [string] (Integer/parseInt string 2))

"Easy"
(defn calc-by-columns [bit-chooser strings]
  (->> (transpose strings)
       (mapv #(calc-bit bit-chooser %))
       (apply str)
       parse-binary))

(let [strings (parse)
      gamma (calc-by-columns most-common strings)
      epsilon (calc-by-columns least-common strings)]
  (* gamma epsilon))

"Hard"
(defn calc-by-columns-2
  [bit-chooser strings]
  (loop [prefix ""]
    (let [prefixed-strings (filter #(starts-with? % prefix) strings)]
      (if (= (count prefixed-strings) 1)
        (parse-binary (first prefixed-strings))
        (let [next-bit (calc-bit bit-chooser (nth (transpose prefixed-strings) (count prefix)))]
          (recur (str prefix next-bit)))))))

(let [strings (parse)
      oxygen (calc-by-columns-2 most-common strings)
      co2 (calc-by-columns-2 least-common strings)]
  (* oxygen co2))
