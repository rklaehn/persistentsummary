package com.rklaehn.persistentsummary

/**
  * Typeclass for summaries.
  *
  * The empty element and the combine operation must form a monoid.
  *
  * @tparam A the element type
  * @tparam S the summary type
  */
trait Summary[A, S] {

  /**
    * the empty summary
    */
  def empty: S

  /**
    * create a summary for a single element
    * @param value the element to summarize
    * @return the summary
    */
  def apply(value: A): S

  /**
    * combine two summary values. This method must be associative
    * @param a a summary
    * @param b a summary
    * @return the combined summary
    */
  def combine(a: S, b: S): S

  /**
    * convenience method to combine three summaries.
    * This must be equivalent to combine(combine(a, b), c)
    * @param a a summary
    * @param b a summary
    * @param c a summary
    * @return the combined summary
    */
  def combine3(a: S, b: S, c: S): S = combine(combine(a, b), c)
}
