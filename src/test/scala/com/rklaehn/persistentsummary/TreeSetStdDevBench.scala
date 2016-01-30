package com.rklaehn.persistentsummary

import ichi.bench.Thyme
import scala.collection.immutable.TreeMap

object TreeSetStdDevBench extends App {

  val cachedSummary = PersistentSummary.treeMapValue(StdDevSummary)

  def avgAndStdDev(values: TreeMap[Int, Double]): (Long, Double, Double) = {
    val (n, sum, sum2) = cachedSummary(values)
    val avg = sum / n
    val variance = sum2 / (n - 1) - (sum * sum) / (n * (n - 1.0))
    val stdDev = math.sqrt(variance)
    (n, avg, stdDev)
  }

  def avgAndStdDevUncached(values: TreeMap[Int, Double]): (Long, Double, Double) = {
    val (n, sum, sum2) = values.values.foldLeft(StdDevSummary.empty) { case (a, e) => StdDevSummary.combine(a, StdDevSummary.apply(e)) }
    val avg = sum / n
    val variance = sum2 / (n - 1) - (sum * sum) / (n * (n - 1.0))
    val stdDev = math.sqrt(variance)
    (n, avg, stdDev)
  }

  val max = 16 * 1024

  val elements: TreeMap[Int, Double] = TreeMap((0 until max).map { i => i -> (i % 16).toDouble }: _*)

  println(avgAndStdDev(elements))

  val elements1 = elements + (max / 2 -> 1000.0)

  println(avgAndStdDev(elements1))

  val th = Thyme.warmed(warmth = Thyme.HowWarm.BenchOff, verbose = println)

  def stdDevUncached(): Double = {
    val elements1 = elements + (max / 2 -> 1000.0)
    avgAndStdDevUncached(elements1)._3
  }

  def stdDevCached(): Double = {
    val elements1 = elements + (max / 2 -> 1000.0)
    avgAndStdDev(elements1)._3
  }

  th.pbenchOffWarm(s"average and stddev when modifying one element in a collection of size $max")(th.Warm(stdDevCached()))(th.Warm(stdDevUncached()))
}
