package com.rklaehn.summarizer

import org.scalacheck.Properties
import org.scalacheck.Prop._

import scala.collection.immutable._

object IntLongSummary extends Summary[Int, Long] {
  override def empty: Long = 0
  override def combine(a: Long, b: Long): Long = a + b
  override def apply(value: Int): Long = value
}

object IntIntLongSummary extends Summary[(Int, Int), Long] {
  override def empty: Long = 0
  override def combine(a: Long, b: Long): Long = a + b
  override def apply(value: (Int, Int)): Long = value._1.toLong + value._2.toLong
}

object SummarizerCheck extends Properties("Summarizer") {

  val treeSet = Summarizer.treeSet(IntLongSummary)
  val treeMapKey = Summarizer.treeMapKey(IntLongSummary)
  val treeMapValue = Summarizer.treeMapValue(IntLongSummary)
  val treeMapEntry = Summarizer.treeMapEntry(IntIntLongSummary)
  val hashSet = Summarizer.hashSet(IntLongSummary)
  val hashMapKey = Summarizer.hashMapKey(IntLongSummary)
  val hashMapValue = Summarizer.hashMapValue(IntLongSummary)
  val hashMapEntry = Summarizer.hashMapEntry(IntIntLongSummary)

  property("TreeSet[Int].sum") = forAll { x: TreeSet[Int] =>
    treeSet(x) == x.map(_.toLong).sum
  }

  property("TreeMap[Int, Int].keys.sum") = forAll { x: Map[Int, Int] =>
    treeMapKey(TreeMap(x.toSeq: _*)) == x.keys.map(_.toLong).sum
  }

  property("TreeMap[Int, Int].values.sum") = forAll { x: Map[Int, Int] =>
    treeMapValue(TreeMap(x.toSeq: _*)) == x.values.map(_.toLong).sum
  }

  property("TreeMap[Int, Int].entries.sum") = forAll { x: Map[Int, Int] =>
    treeMapEntry(TreeMap(x.toSeq: _*)) == x.map { case (k, v) => k.toLong + v.toLong }.sum
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

  property("HashMap[Int, Int].entries.sum") = forAll { x: Map[Int, Int] =>
    hashMapEntry(HashMap(x.toSeq: _*)) == x.map { case (k, v) => k.toLong + v.toLong }.sum
  }
}
