(ns aoc-2021.day24
  (:require [aoc-2021.core :refer [slurp-strings int->ch]]))

(defn var? [x] (contains? #{"w" "x" "y" "z"} x))

(defn parse [] (->> (slurp-strings "day24.txt")
                    (mapv #(clojure.string/split % #" "))
                    (mapv (fn [[cmd & rest]]
                            (into [cmd] (mapv #(if (var? %) % (Integer/parseInt %)) rest))))))

(defn bool->int [x] (if x 1 0))
(defn equals [l r] (bool->int (= l r)))

(defn operand [state key] (if (integer? key) key (get state key)))
(defn binary-op [left right state fn] (assoc state left (fn (operand state left) (operand state right))))
(defn add [left right state] (binary-op left right state +))
(defn mul [left right state] (binary-op left right state *))
(defn div [left right state] (binary-op left right state quot))
(defn rem [left right state] (binary-op left right state mod))
(defn eql [left right state] (binary-op left right state equals))

(def command->func {"add" add "mul" mul "div" div "mod" rem "eql" eql})

(defn evaluate [state commands]
  (reduce (fn [state [cmd l r]] ((get command->func cmd) l r state))
    state commands))

(def calc
  (memoize
    (fn [z command-blocks biggest?]
      (cond
        (and (empty? command-blocks) (= z 0)) ""
        (and (empty? command-blocks) (not= z 0)) nil
        (> z (* 26 26 26 26)) nil
        :else
        (let [[commands & rest] command-blocks
              inputs (range 1 10)
              inputs (if biggest? (reverse inputs) inputs)]
          (first (filter some? (map (fn [input]
                                      (let [new-state (evaluate {"x" 0 "y" 0 "w" input "z" z} (drop 1 commands))
                                            z' (get new-state "z")
                                            biggest-input (calc z' rest biggest?)]
                                        (if (some? biggest-input)
                                          (apply str (int->ch input) biggest-input)
                                          nil)))
                                    inputs))))))))

(calc 0 (into [] (partition 18 18 (parse))) true)
(calc 0 (into [] (partition 18 18 (parse))) false)