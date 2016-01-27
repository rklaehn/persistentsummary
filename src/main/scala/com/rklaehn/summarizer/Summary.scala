package com.rklaehn.summarizer

trait Summary[A, S] {

  def empty: S

  def apply(value: A): S

  def combine(a: S, b: S): S

  def combine3(a: S, b: S, c: S): S = combine(combine(a, b), c)
}
