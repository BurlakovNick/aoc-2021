# Advent of Code 2021 solutions

Written in Clojure

## Day 01

Nothing particularly interesting. 

Today I've learned - macro `->>` and `as->` to straightforward calculation pipeline.

Also - there's `partition` function to get all consequitive groups of given number of items in a sequence.

## Day 02

Solution for second problem can be reused for the first one - just multiply aim by horizontal distance.

It'd better to always use vectors (instead of lists) because of the ease of deconstruction. 