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

  /**
    * Summarize the elements of a TreeSet
    * @param summary the kind of summary to use
    * @tparam K the element type
    * @tparam S the summary type
    * @return a function that performs the summary
    */
  def treeSet[K, S](summary: Summary[K, S]): (TreeSet[K] => S) = PersistentSummaryHelper.treeSet(summary)

  /**
    * Summarize the elements of a TreeMap
    * @param summary the kind of summary to use
    * @tparam K the element type
    * @tparam S the summary type
    * @return a function that performs the summary
    */
  def treeMapKey[K, S](summary: Summary[K, S]): (TreeMap[K, _] => S) = PersistentSummaryHelper.treeMapKey(summary)

  /**
    * Summarize the values of a TreeMap
    * @param summary the kind of summary to use
    * @tparam V the element type
    * @tparam S the summary type
    * @return a function that performs the summary
    */
  def treeMapValue[V, S](summary: Summary[V, S]): (TreeMap[_, V] => S) = PersistentSummaryHelper.treeMapValue(summary)

  /**
    * Summarize the entries of a TreeMap
    * @param summary the kind of summary to use
    * @tparam K the key type
    * @tparam V the value type
    * @tparam S the summary type
    * @return a function that performs the summary
    */
  def treeMapEntry[K, V, S](summary: Summary[(K, V), S]): (TreeMap[K, V] => S) = PersistentSummaryHelper.treeMapEntry(summary)

  /**
    * Summarize the elements of a HashSet
    * @param summary the kind of summary to use
    * @tparam K the element type
    * @tparam S the summary type
    * @return a function that performs the summary
    */
  def hashSet[K, S](summary: Summary[K, S]): (HashSet[K] => S) = PersistentSummaryHelper.hashSet(summary)

  /**
    * Summarize the keys of a HashMap
    * @param summary the kind of summary to use
    * @tparam K the element type
    * @tparam S the summary type
    * @return a function that performs the summary
    */
  def hashMapKey[K, S](summary: Summary[K, S]): (HashMap[K, _] => S) = PersistentSummaryHelper.hashMapKey(summary)

  /**
    * Summarize the values of a HashMap
    * @param summary the kind of summary to use
    * @tparam V the element type
    * @tparam S the summary type
    * @return a function that performs the summary
    */
  def hashMapValue[V, S](summary: Summary[V, S]): (HashMap[_, V] => S) = PersistentSummaryHelper.hashMapValue(summary)

  /**
    * Summarize the entries of a HashMap
    * @param summary the kind of summary to use
    * @tparam K the key type
    * @tparam V the value type
    * @tparam S the summary type
    * @return a function that performs the summary
    */
  def hashMapEntry[K, V, S](summary: Summary[(K, V), S]): (HashMap[K, V] => S) = PersistentSummaryHelper.hashMapEntry(summary)
}
