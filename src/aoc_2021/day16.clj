(ns aoc-2021.day16
  (:require [aoc-2021.core :refer [sum mul slurp-strings]]))

(defn parse [] (first (slurp-strings "day16.txt")))

(defn parse-binary [string] (Integer/parseInt string 2))

(def hex2bin
  {\0 "0000"
   \1 "0001"
   \2 "0010"
   \3 "0011"
   \4 "0100"
   \5 "0101"
   \6 "0110"
   \7 "0111"
   \8 "1000"
   \9 "1001"
   \A "1010"
   \B "1011"
   \C "1100"
   \D "1101"
   \E "1110"
   \F "1111"})

(defn hex->bin [string]
  (apply str (mapv hex2bin string)))

(defn get-int [binary len]
  (let [prefix (apply str (take len binary))]
    [(parse-binary prefix) (apply str (drop len binary))]))

(defn get-literal [binary]
  (loop [cur 0 rest binary]
    (let [[first-bit rest] (get-int rest 1)
          [value rest] (get-int rest 4)
          cur' (+ (* cur 16) value)]
      (if (= first-bit 0)
        [cur' rest]
        (recur cur' rest)))))

(defn get-0-subpackets [binary get-next]
  (let [[len rest] (get-int binary 15)]
    (loop [len len rest rest subpackets []]
      (if (= len 0)
        [subpackets rest]
        (let [[subpacket rest'] (get-next rest)
              parsed-len (- (count rest) (count rest'))
              new-len (- len parsed-len)]
          (recur new-len rest' (conj subpackets subpacket)))))))

(defn get-1-subpackets [binary get-next]
  (let [[len rest] (get-int binary 11)]
    (loop [len len rest rest subpackets []]
      (if (= len 0)
        [subpackets rest]
        (let [[subpacket rest'] (get-next rest)]
          (recur (dec len) rest' (conj subpackets subpacket)))))))

(defn get-next [binary]
  (let [[version rest] (get-int binary 3)
        [type rest] (get-int rest 3)]
    (if (= type 4)
      (let [[value rest] (get-literal rest)]
        [{:version version
          :type    type
          :value   value} rest])
      (let [[length-type rest] (get-int rest 1)
            [subpackets rest] (if (= length-type 0)
                                (get-0-subpackets rest get-next)
                                (get-1-subpackets rest get-next))]
        [{:version    version
          :type       type
          :subpackets subpackets} rest]))))

(defn sum-versions [packet]
  (let [subpackets (:subpackets packet)
        version (:version packet)]
    (if (some? subpackets)
      (sum (conj (map sum-versions subpackets) version))
      version)))

"Easy"
(sum-versions (first (get-next (hex->bin (parse)))))

(defn greater-than [a b] (if (> a b) 1 0))
(defn less-than [a b] (if (< a b) 1 0))
(defn equal [a b] (if (= a b) 1 0))

"Hard"
(def type2command
  {0 sum
   1 mul
   2 (partial apply min)
   3 (partial apply max)
   5 (partial apply greater-than)
   6 (partial apply less-than)
   7 (partial apply equal)})

(defn calc-value [packet]
  (let [subpackets (:subpackets packet)
        value (:value packet)]
    (if (some? value)
      value
      (let [values (mapv calc-value subpackets)
            cmd (type2command (:type packet))]
        (cmd values)))))

(calc-value (first (get-next (hex->bin (parse)))))