package com.rklaehn.persistentsummary

import scala.collection.immutable._

/**
  * Creates persistent summaries for various collection types. The return value of these methods must be stored
  * somewhere for the summaries calculated by it to be persistent. The underlying implementation uses a guava cache with
  * weak keys.
  *
  * https://code.google.com/p/guava-libraries/wiki/CachesExplained
  */
object PersistentSummary {

  def treeSet[K, S](summary: Summary[K, S]): (TreeSet[K] => S) = PersistentSummaryHelper.treeSet(summary)

  def treeMapKey[K, S](summary: Summary[K, S]): (TreeMap[K, _] => S) = PersistentSummaryHelper.treeMapKey(summary)

  def treeMapValue[V, S](summary: Summary[V, S]): (TreeMap[_, V] => S) = PersistentSummaryHelper.treeMapValue(summary)

  def treeMapEntry[K, V, S](summary: Summary[(K, V), S]): (TreeMap[K, V] => S) = PersistentSummaryHelper.treeMapEntry(summary)

  def hashSet[K, S](summary: Summary[K, S]): (HashSet[K] => S) = PersistentSummaryHelper.hashSet(summary)

  def hashMapKey[K, S](summary: Summary[K, S]): (HashMap[K, _] => S) = PersistentSummaryHelper.hashMapKey(summary)

  def hashMapValue[V, S](summary: Summary[V, S]): (HashMap[_, V] => S) = PersistentSummaryHelper.hashMapValue(summary)

  def hashMapEntry[K, V, S](summary: Summary[(K, V), S]): (HashMap[K, V] => S) = PersistentSummaryHelper.hashMapEntry(summary)
}
