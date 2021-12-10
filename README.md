# Advent of Code 2021 solutions

Written in Clojure

## Day 01

Nothing particularly interesting. 

Today I've learned - macro `->>` and `as->` to straightforward calculation pipeline.

Also - there's `partition` function to get all consequitive groups of given number of items in a sequence.

## Day 02

Solution for second problem can be reused for the first one - just multiply aim by horizontal distance.

It'd better to always use vectors (instead of lists) because of the ease of deconstruction - and hence use `mapv` instead of `map`.

Would be good:
- To learn how to debug this nightmare of parenthesis. REPL is good, but I seem to use it inproperly
- To get good with paredit / parinfer, too painful to edit and refactor my code

## Day 03

Today I've learned:
- `let` is pretty convenient for writing complex calculations
- the easiest way to transpose matrix is `(apply mapv vector m)`
- recursive `loop` is much easier to write and understand than generic `reduce`

## Day 04

`contains?` works quite weird for vectors - it works with indexes instead of items [Stack Overflow](https://stackoverflow.com/questions/3249334/test-whether-a-list-contains-a-specific-value-in-clojure)

## Day 05

Clojure's `range` is quite dumb - it works only in increasing way (`start < end`), `end` is exclusive.
Had to implement `smart-range` for the purposes of the task.

`filter` can work with maps! But maps are flattened so each item is represented by an array `[key value]`, interesting.

## Day 06

`iterate` is a nice way to generate infinite sequence of simulations.

~~Mutating maps is not pretty, but doable `(assoc freq k (+ old v))` ~~

There's neat way to update a map:

```
(defn += [freq k v] (update freq k (fnil + 0) v))
```

`update` take function to apply on old value, even args can be provided separately (as `v`)

`fnil` is the way to deal with `nil` values - in case an item isn't found in a map, `0` will be used.

Also - there's `reduce-kv` to reduce maps without wrapping key-value pair in `[k v]`

## Day 07

When dealing with `Math/abs` Clojure doesn't seem to understand the type and choose right overload :( Sad

## Day 08

Clojure exceptions are piece of shit, completely unusable. 

Always ALWAYS use vectors instead of sequences wherever possible!

## Day 09

Neat way to get all grid neighbors
```
(defn grid-neighbors [[x y]]
 (->> [[0 -1] [0 1] [-1 0] [1 0]]
      (mapv #(mapv + [x y] %))
      (filterv is-in?)))
```

Since nearly all functions use all the same parameters (map, size), it's easier to calc them once and save
```
(def heights (parse))
(def n (count heights))
(def m (count (first heights)))
(def points (for [x (range 0 n) y (range 0 m)] [x y]))
```

## Day 10

List comprehensions can tidy up boring code with deconstruction / map / filtering
```
(defn find-errors [type]
  (for [[err x] (mapv spell-check (parse))
        :when (= err type)]
    x))
```