package com.rklaehn.persistentsummary

import ichi.bench.Thyme

import scala.collection.immutable.TreeSet

object SimpleSummaryBench extends App {

  val th = Thyme.warmed(warmth = Thyme.HowWarm.BenchOff, verbose = println)

  val summarizer = PersistentSummary.treeSet(IntLongSummary)

  def buildCached(n: Int): Long = {
    var current = TreeSet.empty[Int]
    var totalSum = 0L
    for(i ← 0 until n) {
      current += i
      totalSum += summarizer(current)
    }
    totalSum
  }

  def buildUncached(n: Int): Long = {
    var current = TreeSet.empty[Int]
    var totalSum = 0L
    for(i ← 0 until n) {
      current += i
      totalSum += current.iterator.map(_.toLong).sum
    }
    totalSum

  }

  val n = 5000
  th.pbenchOffWarm(s"build 1 to $n uncached vs. cached")(th.Warm(buildUncached(n)))(th.Warm(buildCached(n)))
}
