package com.rklaehn.persistentsummary

import org.scalacheck.Properties
import org.scalacheck.Prop._

import scala.collection.immutable._

object IntLongSummary extends Summary[Int, Long] {
  override def empty: Long = 0
  override def combine(a: Long, b: Long): Long = a + b
  override def apply(value: Int): Long = value
}

object IntStringLongSummary extends Summary[(Int, String), Long] {
  override def empty: Long = 0
  override def combine(a: Long, b: Long): Long = a + b
  override def apply(value: (Int, String)): Long = value._1.toLong + value._2.length.toLong
}

object PersistentSummaryCheck extends Properties("PersistentSummary") {

  val treeSet = PersistentSummary.treeSet(IntLongSummary)
  val treeMapKey = PersistentSummary.treeMapKey(IntLongSummary)
  val treeMapValue = PersistentSummary.treeMapValue(IntLongSummary)
  val treeMapEntry = PersistentSummary.treeMapEntry(IntStringLongSummary)
  val hashSet = PersistentSummary.hashSet(IntLongSummary)
  val hashMapKey = PersistentSummary.hashMapKey(IntLongSummary)
  val hashMapValue = PersistentSummary.hashMapValue(IntLongSummary)
  val hashMapEntry = PersistentSummary.hashMapEntry(IntStringLongSummary)
  val vector = PersistentSummary.vector(IntLongSummary)

  property("Vector[Int].sum") = forAll { d: Vector[Vector[Int]] =>
    val x = d.flatten
    val y = x.drop(1)
    val z = x.drop(x.length / 2).dropRight(1)
    (x.map(_.toLong).sum == vector(x)) &&
    (y.map(_.toLong).sum == vector(y)) &&
    (z.map(_.toLong).sum == vector(z))
  }

  property("TreeSet[Int].sum") = forAll { x: TreeSet[Int] =>
    treeSet(x) == x.map(_.toLong).sum
  }

  property("TreeMap[Int, Int].keys.sum") = forAll { x: Map[Int, Int] =>
    treeMapKey(TreeMap(x.toSeq: _*)) == x.keys.map(_.toLong).sum
  }

  property("TreeMap[Int, Int].values.sum") = forAll { x: Map[Int, Int] =>
    treeMapValue(TreeMap(x.toSeq: _*)) == x.values.map(_.toLong).sum
  }

  property("TreeMap[Int, String].entries.sum") = forAll { x: Map[Int, String] =>
    treeMapEntry(TreeMap(x.toSeq: _*)) == x.map { case (k, v) => k.toLong + v.length.toLong }.sum
  }

  property("HashSet[Int].sum") = forAll { x: HashSet[Int] =>
    hashSet(x) == x.map(_.toLong).sum
  }

  property("HashMap[Int, Int].keys.sum") = forAll { x: Map[Int, Int] =>
    hashMapKey(HashMap(x.toSeq: _*)) == x.keys.map(_.toLong).sum
  }

  property("HashMap[Int, Int].values.sum") = forAll { x: Map[Int, Int] =>
    hashMapValue(HashMap(x.toSeq: _*)) == x.values.map(_.toLong).sum
  }

  property("HashMap[Int, String].entries.sum") = forAll { x: Map[Int, String] =>
    hashMapEntry(HashMap(x.toSeq: _*)) == x.map { case (k, v) => k.toLong + v.length.toLong }.sum
  }
}
