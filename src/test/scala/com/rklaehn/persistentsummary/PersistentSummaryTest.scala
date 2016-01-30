package com.rklaehn.persistentsummary

import org.scalatest.FunSuite

class PersistentSummaryTest extends FunSuite {

  test("vector3") {
    val x = Vector(1, 2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -3, -2, -1).drop(2)
    val f = PersistentSummary.vector(IntLongSummary)
    assert(f(x) == x.sum)
  }

  test("vector2") {
    val x = Vector(-1)
    val f = PersistentSummary.vector(IntLongSummary)
    assert(f(x) == -1L)
  }

  test("vector1") {
    val x = Vector(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1)
    val f = PersistentSummary.vector(IntLongSummary)
    assert(f(x) == -1L)
  }
}
