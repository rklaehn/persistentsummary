[![Build Status](https://travis-ci.org/rklaehn/persistentsummary.png)](https://travis-ci.org/rklaehn/persistentsummary)
[![codecov.io](http://codecov.io/github/rklaehn/persistentsummary/coverage.svg?branch=master)](http://codecov.io/github/rklaehn/persistentsummary?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.rklaehn/persistentsummary_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.rklaehn/persistentsummary_2.11)
[![Scaladoc](http://javadoc-badge.appspot.com/com.rklaehn/persistentsummary_2.11.svg?label=scaladoc)](http://javadoc-badge.appspot.com/com.rklaehn/persistentsummary_2.11#com.rklaehn.persistentsummary.PersistentSummary$)

# PersistentSummary

This library allows to define persistent summaries of ***existing*** immutable, tree-based scala collections such as [`TreeSet`](http://www.scala-lang.org/api/current/index.html#scala.collection.immutable.TreeSet) or [`Vector`](http://www.scala-lang.org/api/current/index.html#scala.collection.immutable.Vector). This can yield to large efficiency gains when retaining a complex summary property of large collections.

## Demo

```scala
val sumOfElements = new Summary[Int, Int] {
  def empty = 0
  def combine(a: Int, b: Int) = a + b
  def apply(value: Int) = value
}

val sum = PersistentSummary.treeSet(sumOfElements)

val set = TreeSet(0 until 10000: _*)

println(sum(set))

// add an element. Most of the underlying tree of set will be reused
val set1 = set + 20000
// will reuse calculation from last call
println(sum(set1))

// this will do the entire calculation from scratch
println(set1.sum)
```

## What's a Summary

A summary is a value that is computed from all elements of a `Set` or `Seq`, or the keys, values or entries of a `Map`.

Here is the typeclass that is used to define summaries.

```scala
trait Summary[A, S] {
  def empty: S
  def apply(value: A): S
  def combine(a: S, b: S): S
}
```

You need to provide a way to create a summary from a single node, an empty summary, and a way to combine summaries which must be associative. Basically you need a total function `A => S` from the element type `A` to the summary type `S`, and a `Monoid[S]` instance for the summary. We are not using any of the monoid typeclasses from other libraries to avoid dependencies.

## How does it work

Immutable scala collections are persistent and use structural sharing. So e.g. adding a single element to a large set will not create a copy of the entire set, but just of a few tree nodes. This library allows adding summary information to the tree nodes and *reusing* the summary information to compute the summary of the updated set without having to recalculate the summary of all elements.

Here is a small red black tree (a `TreeSet` with 10 random elements):
![TreeSet internal structure](http://g.gravizo.com/g?
digraph G {
  aize="4,4"
  node [shape=box,style="filled, rounded",fillcolor="white"]
  n7181ae3f [label="61",color="black"]
  n62fdb4a6 [label="28",color="black"]
  n11e21d0e [label="21",color="black"]
  n1dd02175 [label="31",color="black"]
  n31206beb [label="60",color="red"]
  n3e77a1ed [label="73",color="black"]
  n3ffcd140 [label="71",color="black"]
  n23bb8443 [label="77",color="red"]
  n1176dcec [label="76",color="black"]
  n120d6fe6 [label="78",color="black"]
  n7181ae3f -> n62fdb4a6
  n62fdb4a6 -> n11e21d0e
  n62fdb4a6 -> n1dd02175
  n1dd02175 -> n31206beb
  n7181ae3f -> n3e77a1ed
  n3e77a1ed -> n3ffcd140
  n3e77a1ed -> n23bb8443
  n23bb8443 -> n1176dcec
  n23bb8443 -> n120d6fe6
}
)

And here is the same tree with one element (87) added:

![TreeSet internal structure](http://g.gravizo.com/g?
digraph G {
  aize="4,4"
  node [shape=box,style="filled, rounded",fillcolor="white"]
  n38364841 [label="61",color="black"]
  n62fdb4a6 [label="28",color="black"]
  n11e21d0e [label="21",color="black"]
  n1dd02175 [label="31",color="black"]
  n31206beb [label="60",color="red"]
  n28c4711c [label="73",color="black"]
  n3ffcd140 [label="71",color="black"]
  n59717824 [label="77",color="red"]
  n1176dcec [label="76",color="black"]
  n146044d7 [label="78",color="black"]
  n1e9e725a [label="87",color="red"]
  n38364841 -> n62fdb4a6
  n62fdb4a6 -> n11e21d0e
  n62fdb4a6 -> n1dd02175
  n1dd02175 -> n31206beb
  n38364841 -> n28c4711c
  n28c4711c -> n3ffcd140
  n28c4711c -> n59717824
  n59717824 -> n1176dcec
  n59717824 -> n146044d7
  n146044d7 -> n1e9e725a
}
)

As you can see, the structure of both trees is mostly the same, and will be shared in a good implementation. So any summary information that is attached to a subtree *will not have to be recalculated* when doing a small change to a big tree.

Here are the two trees merged into one. As you can see, a lot of the nodes are shared, so the summaries for these nodes would also be shared.

![TreeSet internal structure](http://g.gravizo.com/g?
digraph G {
  aize="4,4"
  node [shape=box,style="filled, rounded",fillcolor="white"]
  n7181ae3f [label="61",color="black"]
  n62fdb4a6 [label="28",color="black"]
  n11e21d0e [label="21",color="black"]
  n1dd02175 [label="31",color="black"]
  n31206beb [label="60",color="red"]
  n3e77a1ed [label="73",color="black"]
  n3ffcd140 [label="71",color="black"]
  n23bb8443 [label="77",color="red"]
  n1176dcec [label="76",color="black"]
  n120d6fe6 [label="78",color="black"]
  n7181ae3f -> n62fdb4a6
  n62fdb4a6 -> n11e21d0e
  n62fdb4a6 -> n1dd02175
  n1dd02175 -> n31206beb
  n7181ae3f -> n3e77a1ed
  n3e77a1ed -> n3ffcd140
  n3e77a1ed -> n23bb8443
  n23bb8443 -> n1176dcec
  n23bb8443 -> n120d6fe6
  n38364841 [label="61",color="black"]
  n62fdb4a6 [label="28",color="black"]
  n11e21d0e [label="21",color="black"]
  n1dd02175 [label="31",color="black"]
  n31206beb [label="60",color="red"]
  n28c4711c [label="73",color="black"]
  n3ffcd140 [label="71",color="black"]
  n59717824 [label="77",color="red"]
  n1176dcec [label="76",color="black"]
  n146044d7 [label="78",color="black"]
  n1e9e725a [label="87",color="red"]
  n38364841 -> n62fdb4a6
  n62fdb4a6 -> n11e21d0e
  n62fdb4a6 -> n1dd02175
  n1dd02175 -> n31206beb
  n38364841 -> n28c4711c
  n28c4711c -> n3ffcd140
  n28c4711c -> n59717824
  n59717824 -> n1176dcec
  n59717824 -> n146044d7
  n146044d7 -> n1e9e725a
}
)

## Supported collections

The approach of persistent summaries only makes sense for tree-based, immutable collections. So currently the following collections from `scala.collection.immutable` are supported:

- `TreeSet`
- `TreeMap`
  - keys, values, entries
- `HashSet`
- `HashMap`
  - keys, values, entries

## Non-trivial example

Imagine you have a large collection of `Double` samples (e.g. some measurement data), and you want to keep statistics such as the average and standard deviation for them. *You also want to keep the data up to date when new data is coming in*.

Here is how you would solve this using a persistent summary:

```scala
val cachedSummary = PersistentSummary.vector(StdDevSummary)

def avgAndStdDev(values: Vector[Double]): (Long, Double, Double) = {
  val (n, sum, sum2) = cachedSummary(values)
  val avg = sum / n
  val variance = sum2 / (n - 1) - (sum * sum) / (n * (n - 1.0))
  val stdDev = math.sqrt(variance)
  (n, avg, stdDev)
}

val max = 16 * 1024

val elements: Vector[Double] = Vector((0 until max).map { i => (i % 16).toDouble }: _*)
println(avgAndStdDev(elements))

val elements1 = elements.updated(max / 2, 1000.0)
println(avgAndStdDev(elements1))
```

### Benchmark

```scala
val th = Thyme.warmed(warmth = Thyme.HowWarm.BenchOff, verbose = println)

def stdDevUncached(): Double = {
  val elements1 = elements.updated(max / 2, 1000.0)
  avgAndStdDevUncached(elements1)._3
}

def stdDevCached(): Double = {
  val elements1 = elements.updated(max / 2, 1000.0)
  avgAndStdDev(elements1)._3
}

th.pbenchOffWarm(s"average and stddev when modifying one element in a collection of size $max")(th.Warm(stdDevCached()))(th.Warm(stdDevUncached()))
```

### Results

YMMV, as usual with benchmarks. Obviously the huge performance improvement is bought by additional memory usage, so you will have to look at this closely in a real world application.

```
Benchmark comparison (in 22.29 s): average and stddev when modifying one element in a collection of size 16384
Significantly different (p ~= 0)
  Time ratio:    82.82733   95% CI 75.32669 - 90.32797   (n=20)
    First     5.999 us   95% CI 5.912 us - 6.086 us
    Second    496.9 us   95% CI 452.5 us - 541.3 us
```

## Configuration

All the heavy lifting inside a persistent summary is done by a [guava cache](https://github.com/google/guava/wiki/CachesExplained). To configure this cache, you can pass a `PersistentSummary.Config` instance, which currently just contains a [`CacheBuilderSpec`](http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/cache/CacheBuilderSpec.html). The default configuration has weak keys, but no expiry rules. This will lead to summaries being kept indefinitely as long as the corresponding data is reachable, and summaries being collected as soon as the corresponding data is collected.

For a real life use case, you will probably want to set expiry or max cache size rules to avoid excessive memory consumption. I would suggest putting a string representation of the CacheBuilderSpec into a config file.

## Implementation details

This library is using reflection to get at some internals of the scala collections. It is also partially implemented in the scala.collections.immutable namespace to get around access restrictions. And it uses a WeakReference based cache from google [guava collections](https://github.com/google/guava) in order to prevent memory leaks.

Despite all these hairy details, I am confident that the approach will work for the forseeable future, unless there is a major redesign of the collections library. There is a certain performance overhead due to the weak reference based cache, but nothing to worry about if you do complex summaries. See the benchmarks.

## Limitations

- Because of the dependency on the google guava library, this library will not work on scala.js.
- Summaries are added to tree nodes using a guava cache with weak keys. There is some overhead over having the summary as a simple field of the tree node. For complex summaries, this won't matter much. But for simple summaries such as in the sum example above, it might be better to just recalculate the sum from scratch.

## Alternatives

There are some specialized collections that keep a summary property. E.g. this [FingerTree](https://github.com/Sciss/FingerTree).
