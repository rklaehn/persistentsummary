package com.rklaehn.persistentsummary

import ichi.bench.Thyme

class Foo(val a: AnyRef, val b: AnyRef)

object ReflectiveAccessBench extends App {

  val foo = new Foo("", "")

  val accessor = classOf[Foo].getDeclaredField("a")
  accessor.setAccessible(true)

  val th = Thyme.warmed(warmth = Thyme.HowWarm.BenchOff, verbose = println)

  th.pbenchOffWarm("direct vs. reflective property access")(th.Warm(foo.b))(th.Warm(accessor.get(foo)))
}
