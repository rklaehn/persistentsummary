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
