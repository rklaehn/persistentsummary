package com.rklaehn.persistentsummary

object StdDevSummary extends Summary[Double, (Long, Double, Double)] {
  override def empty: (Long, Double, Double) = (0L, 0, 0)

  override def combine(a: (Long, Double, Double), b: (Long, Double, Double)): (Long, Double, Double) =
  // you would usually sum the tuples using spire, but I don't want a dependency for this demo
    (a._1 + b._1, a._2 + b._2, a._3 + b._3)

  override def apply(value: Double): (Long, Double, Double) = (1L, value, value * value)
}
