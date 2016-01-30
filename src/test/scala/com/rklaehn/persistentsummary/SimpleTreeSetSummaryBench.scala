package com.rklaehn.persistentsummary

import ichi.bench.Thyme

import scala.collection.immutable.TreeSet

object SimpleTreeSetSummaryBench extends App {

  val th = Thyme.warmed(warmth = Thyme.HowWarm.BenchOff, verbose = println)

  val summarizer = PersistentSummary.treeSet(IntLongSummary)

  def buildCached(n: Int): Long = {
    var current = TreeSet.empty[Int]
    var totalSum = 0L
    for (i ← 0 until n) {
      current += i
      totalSum += summarizer(current)
    }
    totalSum
  }

  def buildUncached(n: Int): Long = {
    var current = TreeSet.empty[Int]
    var totalSum = 0L
    for (i ← 0 until n) {
      current += i
      totalSum += current.iterator.map(_.toLong).sum
    }
    totalSum
  }

  val n = 5000
  val s = TreeSet(0 until n: _*)
  def accessSummary(): Long = {
    summarizer(s)
  }

  def summarySmallChange(): Long = {
    val s1 = s + 10000
    summarizer(s)
  }

  def sumSmallChange(): Long = {
    val s1 = s + 10000
    s1.iterator.map(_.toLong).sum
  }

  println("measures the time to access an already existing summary from a guava cache with weak keys")
  th.pbenchWarm(th.Warm(accessSummary()), title = "access")

  println("compares persistent summary with recalculating from scratch every time")
  th.pbenchOffWarm(s"build 1 to $n uncached vs. cached")(th.Warm(buildUncached(n)))(th.Warm(buildCached(n)))

  println("compare persistent summary with recalculating from scratch (single update to large collection)")
  th.pbenchOffWarm(s"update 5000 / 1 uncached vs. cached")(th.Warm(sumSmallChange()))(th.Warm(summarySmallChange()))

}
