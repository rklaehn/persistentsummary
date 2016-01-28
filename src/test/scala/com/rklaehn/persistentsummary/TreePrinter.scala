package com.rklaehn.persistentsummary

import scala.collection.immutable.{CollectionTreeUtil, TreeSet}
import scala.util.Random

object TreePrinter extends App {
  var t = TreeSet.empty[Int]
  val r = new Random()
  for(i ← 0 until 10)
    t += r.nextInt(100)
  val text = CollectionTreeUtil.toGraphVizDot(t)
  println(text)

  t += r.nextInt(100)
  val text2 = CollectionTreeUtil.toGraphVizDot(t)
  println(text2)
}
