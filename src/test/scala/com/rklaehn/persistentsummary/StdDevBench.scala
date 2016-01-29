package com.rklaehn.persistentsummary

import ichi.bench.Thyme

import scala.collection.immutable.TreeMap

object StdDevCalculator {

  val stdDevSummary = new Summary[Double, (Long, Double, Double)] {
    override def empty: (Long, Double, Double) = (0L, 0, 0)

    override def combine(a: (Long, Double, Double), b: (Long, Double, Double)): (Long, Double, Double) =
    // you would usually sum the tuples using spire, but I don't want a dependency for this demo
      (a._1 + b._1, a._2 + b._2, a._3 + b._3)

    override def apply(value: Double): (Long, Double, Double) = (1L, value, value * value)
  }

  val cachedSummary = PersistentSummary.treeMapValue(stdDevSummary)

  def avgAndStdDev(values: TreeMap[Int, Double]): (Long, Double, Double) = {
    val (n, sum, sum2) = cachedSummary(values)
    val avg = sum / n
    val variance = sum2 / (n - 1) - (sum * sum) / (n * (n - 1.0))
    val stdDev = math.sqrt(variance)
    (n, avg, stdDev)
  }

  def avgAndStdDevUncached(values: TreeMap[Int, Double]): (Long, Double, Double) = {
    val (n, sum, sum2) = values.values.foldLeft(stdDevSummary.empty) { case (a, e) => stdDevSummary.combine(a, stdDevSummary.apply(e)) }
    val avg = sum / n
    val variance = sum2 / (n - 1) - (sum * sum) / (n * (n - 1.0))
    val stdDev = math.sqrt(variance)
    (n, avg, stdDev)
  }
}

object StdDevBench extends App {

  val max = 16 * 1024

  val elements: TreeMap[Int, Double] = TreeMap((0 until max).map { i => i -> (i % 16).toDouble }: _*)

  println(StdDevCalculator.avgAndStdDev(elements))

  val elements1 = elements + (max / 2 -> 1000.0)

  println(StdDevCalculator.avgAndStdDev(elements1))

  val th = Thyme.warmed(warmth = Thyme.HowWarm.BenchOff, verbose = println)

  def stdDevUncached(): Double = {
    val elements1 = elements + (max / 2 -> 1000.0)
    StdDevCalculator.avgAndStdDevUncached(elements1)._3
  }

  def stdDevCached(): Double = {
    val elements1 = elements + (max / 2 -> 1000.0)
    StdDevCalculator.avgAndStdDev(elements1)._3
  }

  th.pbenchOffWarm(s"average and stddev when modifying one element in a collection of size $max")(th.Warm(stdDevUncached()))(th.Warm(stdDevCached()))

}
