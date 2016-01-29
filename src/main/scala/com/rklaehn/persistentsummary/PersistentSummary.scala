package com.rklaehn.persistentsummary

import com.google.common.cache.CacheBuilderSpec

import scala.collection.immutable._

/**
  * Creates persistent summaries for various collection types. The return value of these methods must be stored
  * somewhere for the summaries calculated by it to be persistent. The underlying implementation uses a guava cache with
  * weak keys.
  *
  * [[https://code.google.com/p/guava-libraries/wiki/CachesExplained]]
  */
object PersistentSummary {

  /**
    * Summarize the elements of a TreeSet
    * @param summary the kind of summary to use
    * @param config the cache configuration
    * @tparam K the element type
    * @tparam S the summary type
    * @return a function that performs the summary
    */
  def treeSet[K, S](summary: Summary[K, S])(implicit config: Config): (TreeSet[K] => S) =
    PersistentSummaryHelper.treeSet(summary, config.spec)

  /**
    * Summarize the elements of a TreeMap
    * @param summary the kind of summary to use
    * @param config the cache configuration
    * @tparam K the element type
    * @tparam S the summary type
    * @return a function that performs the summary
    */
  def treeMapKey[K, S](summary: Summary[K, S])(implicit config: Config): (TreeMap[K, _] => S) =
    PersistentSummaryHelper.treeMapKey(summary, config.spec)

  /**
    * Summarize the values of a TreeMap
    * @param summary the kind of summary to use
    * @param config the cache configuration
    * @tparam V the element type
    * @tparam S the summary type
    * @return a function that performs the summary
    */
  def treeMapValue[V, S](summary: Summary[V, S])(implicit config: Config): (TreeMap[_, V] => S) =
    PersistentSummaryHelper.treeMapValue(summary, config.spec)

  /**
    * Summarize the entries of a TreeMap
    * @param summary the kind of summary to use
    * @param config the cache configuration
    * @tparam K the key type
    * @tparam V the value type
    * @tparam S the summary type
    * @return a function that performs the summary
    */
  def treeMapEntry[K, V, S](summary: Summary[(K, V), S])(implicit config: Config): (TreeMap[K, V] => S) =
    PersistentSummaryHelper.treeMapEntry(summary, config.spec)

  /**
    * Summarize the elements of a HashSet
    * @param summary the kind of summary to use
    * @param config the cache configuration
    * @tparam K the element type
    * @tparam S the summary type
    * @return a function that performs the summary
    */
  def hashSet[K, S](summary: Summary[K, S])(implicit config: Config): (HashSet[K] => S) =
    PersistentSummaryHelper.hashSet(summary, config.spec)

  /**
    * Summarize the keys of a HashMap
    * @param summary the kind of summary to use
    * @param config the cache configuration
    * @tparam K the element type
    * @tparam S the summary type
    * @return a function that performs the summary
    */
  def hashMapKey[K, S](summary: Summary[K, S])(implicit config: Config): (HashMap[K, _] => S) =
    PersistentSummaryHelper.hashMapKey(summary, config.spec)

  /**
    * Summarize the values of a HashMap
    * @param summary the kind of summary to use
    * @param config the cache configuration
    * @tparam V the element type
    * @tparam S the summary type
    * @return a function that performs the summary
    */
  def hashMapValue[V, S](summary: Summary[V, S])(implicit config: Config): (HashMap[_, V] => S) =
    PersistentSummaryHelper.hashMapValue(summary, config.spec)

  /**
    * Summarize the entries of a HashMap
    * @param summary the kind of summary to use
    * @param config the cache configuration
    * @tparam K the key type
    * @tparam V the value type
    * @tparam S the summary type
    * @return a function that performs the summary
    */
  def hashMapEntry[K, V, S](summary: Summary[(K, V), S])(implicit config: Config): (HashMap[K, V] => S) =
    PersistentSummaryHelper.hashMapEntry(summary, config.spec)

  /**
    * Configuration object to configure the underlying guava cache
    * @param spec the CacheBuilderSpec to use
    */
  final case class Config(spec: CacheBuilderSpec)

  trait Config0 {

    /**
      * The default is to use weakKeys and no explicit expiry time or maximum size
      */
    implicit val default = new Config(CacheBuilderSpec.parse("weakKeys"))
  }

  object Config extends Config0
}
