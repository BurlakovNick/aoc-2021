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
