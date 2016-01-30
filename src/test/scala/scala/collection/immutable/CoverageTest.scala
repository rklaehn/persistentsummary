package scala.collection.immutable

import com.rklaehn.persistentsummary.IntLongSummary
import com.rklaehn.persistentsummary.PersistentSummary.Config
import org.scalatest.FunSuite

class VectorSummarizerMiscTest extends FunSuite {

  val s = new PersistentSummaryHelper.VectorSummarizer[Int, Long](IntLongSummary, Config.default.spec)

  test("depthZeroSummary") {
    assert(s.aggregate(null, 0, 0, 0) === IntLongSummary.empty)
  }

  test("largeCollectionChildren") {
    val it = new VectorIterator[Int](0, 0)
    it.display5 = Array.empty[AnyRef]
    it.depth = 6
    assert(s.getChildren(it) eq it.display5)
  }
}
