package com.rklaehn.summarizer

import scala.collection.immutable._

object Summarizer {

  def treeSet[K, S](summary: Summary[K, S]): (TreeSet[K] => S) = SummaryHelper.treeSet(summary)

  def treeMapKey[K, S](summary: Summary[K, S]): (TreeMap[K, _] => S) = SummaryHelper.treeMapKey(summary)

  def treeMapValue[V, S](summary: Summary[V, S]): (TreeMap[_, V] => S) = SummaryHelper.treeMapValue(summary)

  def treeMapEntry[K, V, S](summary: Summary[(K, V), S]): (TreeMap[K, V] => S) = SummaryHelper.treeMapEntry(summary)

  def hashSet[K, S](summary: Summary[K, S]): (HashSet[K] => S) = SummaryHelper.hashSet(summary)

  def hashMapKey[K, S](summary: Summary[K, S]): (HashMap[K, _] => S) = SummaryHelper.hashMapKey(summary)

  def hashMapValue[V, S](summary: Summary[V, S]): (HashMap[_, V] => S) = SummaryHelper.hashMapValue(summary)

  def hashMapEntry[K, V, S](summary: Summary[(K, V), S]): (HashMap[K, V] => S) = SummaryHelper.hashMapEntry(summary)
}
