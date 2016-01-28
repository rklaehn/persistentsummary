# PersistentSummary

This library allows to define persistent summaries of immutable, tree-based scala collections. This can yield to large efficiency gains when working with [persistent collections](https://en.wikipedia.org/wiki/Persistent_data_structure) that use structural sharing.

There are some specialized collections that keep a summary property. E.g. this [FingerTree](https://github.com/Sciss/FingerTree). This library instead provides the ability to add any number of arbitrary summaries to ***existing*** immutable scala collections.

## Summary

A summary is an aggregation function that is computed from the elements of a `Set` or `Seq`, or the keys, values or entries of a `Map`.

Here is the typeclass that is used to define summaries.

```scala
trait Summary[A, S] {
  def empty: S
  def apply(value: A): S
  def combine(a: S, b: S): S
}
```

You need to provide a way to create a summary from a single node, an empty summary, and a way to combine summaries which must be associative. Basically you need a total function `A => S` from the element type `A` to the summary type `S`, and a `Monoid[S]` instance for the summary. We are not using any of the monoid typeclasses from other libraries to avoid dependencies.

## Persistence

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

## Supported collections

The approach of persistent summaries only makes sense for tree-based, immutable collections. So currently the following collections from `scala.collection.immutable` are supported:

- `TreeSet`
- `TreeMap`
  - keys, values, entries
- `HashSet`
- `HashMap`
  - keys, values, entries 

## Implementation details

You don't want to know. Trust me. In case you do: this library is using reflection to get at some internals of the scala collections. It is also partially implemented in the scala.collections.immutable namespace to get around access restrictions. And it uses a WeakReference based cache from google [guava collections](https://github.com/google/guava) in order to prevent memory leaks.

Despite all these hairy details, I am confident that the approach will work for at least the 2.11 and 2.12 version of scala. There is a certain performance overhead due to the weak reference based cache, but nothing to worry about if you do complex summaries. See the benchmarks.

## Limitations

Because of the dependency on the google guava library, this library will not work on scala.js.
