package com.rklaehn.persistentsummary

trait Tree[N, E] {
  def childCount(node: N): Int
  def child(node: N, index: Int): N
  def value(node: N): Option[E]
  def label(node: N): String
}
