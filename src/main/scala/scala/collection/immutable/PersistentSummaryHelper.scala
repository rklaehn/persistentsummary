package scala.collection.immutable

import com.google.common.cache.{CacheBuilderSpec, CacheLoader, CacheBuilder}
import com.rklaehn.persistentsummary.Summary
import RedBlackTree.Tree
import scala.collection.immutable.HashMap.HashTrieMap
import scala.collection.immutable.HashSet.HashTrieSet

object PersistentSummaryHelper {

  def hashSet[K, S](s: Summary[K, S], spec: CacheBuilderSpec): (HashSet[K]) ⇒ S = new HashSetSummarizer[K, S](s, spec)

  def hashMapKey[K, S](s: Summary[K, S], spec: CacheBuilderSpec): (HashMap[K, _]) ⇒ S = new HashMapKeySummarizer[K, S](s, spec)

  def hashMapValue[V, S](s: Summary[V, S], spec: CacheBuilderSpec): (HashMap[_, V]) ⇒ S = new HashMapValueSummarizer[V, S](s, spec)

  def hashMapEntry[K, V, S](s: Summary[(K, V), S], spec: CacheBuilderSpec): (HashMap[K, V]) ⇒ S = new HashMapEntrySummarizer[K, V, S](s, spec)

  def treeMapKey[K, S](s: Summary[K, S], spec: CacheBuilderSpec): (TreeMap[K, _] ⇒ S) = new TreeMapKeySummarizer[K, S](s, spec)

  def treeMapValue[V, S](s: Summary[V, S], spec: CacheBuilderSpec): (TreeMap[_, V] ⇒ S) = new TreeMapValueSummarizer[V, S](s, spec)

  def treeMapEntry[K, V, S](s: Summary[(K, V), S], spec: CacheBuilderSpec): (TreeMap[K, V] ⇒ S) = new TreeMapEntrySummarizer[K, V, S](s, spec)

  def treeSet[K, S](s: Summary[K, S], spec: CacheBuilderSpec): (TreeSet[K] ⇒ S) = new TreeSetSummarizer[K, S](s, spec)

  private def nullToGuard(x: AnyRef): AnyRef = if (x eq null) "null" else x

  private val treeMapAccessor = classOf[scala.collection.immutable.TreeMap[_, _]].getDeclaredField("tree")
  treeMapAccessor.setAccessible(true)

  private val treeSetAccessor = classOf[scala.collection.immutable.TreeSet[_]].getDeclaredField("tree")
  treeSetAccessor.setAccessible(true)

  private abstract class RBTreeSummarizerBase[X, S] {

    def leafSummary(tree: RedBlackTree.Tree[_, _]): S

    def spec: CacheBuilderSpec

    def summary: Summary[X, S]

    private[this] val emptySummaryAsAnyRef: AnyRef = summary.empty.asInstanceOf[AnyRef]

    private[this] val memo = CacheBuilder.from(spec).build[AnyRef, AnyRef](new CacheLoader[AnyRef, AnyRef] {
      override def load(tree: AnyRef): AnyRef = tree match {
        case tree: RedBlackTree.Tree[_, _] ⇒
          summary.combine3(apply0(tree.left), leafSummary(tree), apply0(tree.right)).asInstanceOf[AnyRef]
        case _ ⇒
          emptySummaryAsAnyRef
      }
    })

    protected def apply0(tree: AnyRef): S =
      memo.get(nullToGuard(tree)).asInstanceOf[S]
  }

  private final class TreeMapKeySummarizer[K, S](val summary: Summary[K, S], val spec: CacheBuilderSpec) extends RBTreeSummarizerBase[K, S] with (TreeMap[K, _] ⇒ S) {
    def leafSummary(tree: Tree[_, _]): S = summary.apply(tree.key.asInstanceOf[K])
    def apply(map: TreeMap[K, _]): S = apply0(treeMapAccessor.get(map))
  }

  private final class TreeMapValueSummarizer[V, S](val summary: Summary[V, S], val spec: CacheBuilderSpec) extends RBTreeSummarizerBase[V, S] with (TreeMap[_, V] ⇒ S) {
    def leafSummary(tree: Tree[_, _]): S = summary.apply(tree.value.asInstanceOf[V])
    def apply(map: TreeMap[_, V]): S = apply0(treeMapAccessor.get(map))
  }

  private final class TreeMapEntrySummarizer[K, V, S](val summary: Summary[(K, V), S], val spec: CacheBuilderSpec) extends RBTreeSummarizerBase[(K, V), S] with (TreeMap[K, V] ⇒ S) {
    def leafSummary(tree: Tree[_, _]): S = summary.apply((tree.key.asInstanceOf[K], tree.value.asInstanceOf[V]))
    def apply(map: TreeMap[K, V]): S = apply0(treeMapAccessor.get(map))
  }

  private final class TreeSetSummarizer[A, S](val summary: Summary[A, S], val spec: CacheBuilderSpec) extends RBTreeSummarizerBase[A, S] with (TreeSet[A] ⇒ S) {
    def leafSummary(tree: Tree[_, _]): S = summary.apply(tree.key.asInstanceOf[A])
    def apply(map: TreeSet[A]): S = apply0(treeSetAccessor.get(map))
  }

  private class HashSetSummarizer[K, S](summary: Summary[K, S], spec: CacheBuilderSpec) extends (HashSet[K] => S) {

    private[this] val memo = CacheBuilder.from(spec).build[HashSet[K], AnyRef](new CacheLoader[HashSet[K], AnyRef] {
      override def load(tree: HashSet[K]): AnyRef = aggregate(tree).asInstanceOf[AnyRef]
    })

    def aggregate(s: HashSet[K]): S = s match {
      case s: HashTrieSet[K] =>
        s.elems.foldLeft(summary.empty) {
          case (a, c) => summary.combine(a, apply0(c))
        }
      case _ =>
        s.foldLeft(summary.empty) {
          case (a, e) => summary.combine(a, summary.apply(e))
        }
    }

    def apply0(s: HashSet[K]): S =
      memo.get(s).asInstanceOf[S]

    def apply(s: HashSet[K]): S =
      apply0(s)
  }

  private abstract class HashMapSummarizerBase[X, S] {
    def summary: Summary[X, S]

    def spec: CacheBuilderSpec

    def elementSummary(e: (_, _)): S

    private[this] val memo = CacheBuilder.from(spec).build[HashMap[_, _], AnyRef](new CacheLoader[HashMap[_, _], AnyRef] {
      override def load(tree: HashMap[_, _]): AnyRef = aggregate(tree).asInstanceOf[AnyRef]
    })

    def aggregate(s: HashMap[_, _]): S = s match {
      case s: HashTrieMap[_, _] =>
        s.elems.foldLeft(summary.empty) {
          case (a, c) => summary.combine(a, apply0(c))
        }
      case _ =>
        s.foldLeft(summary.empty) {
          case (a, e) => summary.combine(a, elementSummary(e))
        }
    }

    def apply0(s: HashMap[_, _]): S =
      memo.get(s).asInstanceOf[S]
  }

  private final class HashMapValueSummarizer[V, S](val summary: Summary[V, S], val spec: CacheBuilderSpec) extends HashMapSummarizerBase[V, S] with (HashMap[_, V] => S) {
    override def elementSummary(e: (_, _)): S = summary.apply(e._2.asInstanceOf[V])
    override def apply(m: HashMap[_, V]): S = apply0(m)
  }

  private final class HashMapKeySummarizer[K, S](val summary: Summary[K, S], val spec: CacheBuilderSpec) extends HashMapSummarizerBase[K, S] with (HashMap[K, _] => S) {
    override def elementSummary(e: (_, _)): S = summary.apply(e._1.asInstanceOf[K])
    override def apply(m: HashMap[K, _]): S = apply0(m)
  }

  private final class HashMapEntrySummarizer[K, V, S](val summary: Summary[(K, V), S], val spec: CacheBuilderSpec) extends HashMapSummarizerBase[(K, V), S] with (HashMap[K, V] => S) {
    override def elementSummary(e: (_, _)): S = summary.apply(e.asInstanceOf[(K, V)])
    override def apply(m: HashMap[K, V]): S = apply0(m)
  }
}
