# PersistentSummary

This library allows to define persistent summaries of immutable, tree-based scala collections. This can yield to large efficiency gains when working with [persistent collections](https://en.wikipedia.org/wiki/Persistent_data_structure) that use structural sharing.

## Supported collections

The approach of persistent summaries only makes sense for tree-based, immutable collections. So currently the following collections from `scala.collection.immutable` are supported:

- `TreeSet`
- `TreeMap`
  - keys, values, entries
- `HashSet`
- `HashMap`
  - keys, values, entries 

## Implementation details

You don't want to know. In case you do: this library is using reflection to get at some internals of the scala collections. It is also partially implemented in the scala.collections.immutable namespace to get around access restrictions. And it uses a WeakReference based cache from google [guava collections](https://github.com/google/guava) in order to prevent memory leaks.

Despite all these hairy details, I am confident that the approach will work for at least the 2.11 and 2.12 version of scala. There is a certain performance overhead due to the weak reference based cache, but nothing to worry about if you do complex summaries. See the benchmarks.

## Limitations

Because of the dependency on the google guava library, this library will not work on scala.js.
