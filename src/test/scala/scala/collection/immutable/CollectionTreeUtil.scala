package scala.collection.immutable

import com.rklaehn.persistentsummary.Tree
import scala.collection.immutable.{RedBlackTree => RB}

object CollectionTreeUtil {

  def toGraphVizDot[K](node: TreeSet[K]): String = {
    val builder = new StringBuilder
    val tree = treeSet[K]
    builder.append("digraph G {\n")
    builder.append("""  aize="4,4"""" + "\n")
    builder.append("""  node [shape=box,style="filled, rounded",fillcolor="white"]""" + "\n")
    def id(node: AnyRef): String = {
      "n" + System.identityHashCode(node).toHexString
    }
    foreachNode(tree, node) { child ⇒
      val cid = id(child)
      val color = tree.label(child)
      val label = tree.value(child).map(_.toString).getOrElse("")
      builder.append(s"""  $cid [label="$label",color="$color"]""" + "\n")
    }
    foreachEdge(tree, node) { (parent, child) ⇒
      val pid = id(parent)
      val cid = id(child)
      builder.append(s"""  $pid -> $cid\n""")
    }
    builder.append("}")
    builder.result()
  }

  private def foreachNode[N, E, U](tree: Tree[N, E], node: N)(f: N ⇒ U): Unit = {
    f(node)
    for(i ← 0 until tree.childCount(node))
      foreachNode(tree, tree.child(node, i))(f)
  }

  private def foreachEdge[N, E, U](tree: Tree[N, E], node: N)(f: (N, N) ⇒ U): Unit = {
    for(i ← 0 until tree.childCount(node)) {
      val child = tree.child(node, i)
      f(node, child)
      foreachEdge(tree, child)(f)
    }
  }

  private val treeMapAccessor = classOf[scala.collection.immutable.TreeMap[_, _]].getDeclaredField("tree")
  treeMapAccessor.setAccessible(true)

  private val treeSetAccessor = classOf[scala.collection.immutable.TreeSet[_]].getDeclaredField("tree")
  treeSetAccessor.setAccessible(true)

  def treeSet[K]: Tree[AnyRef, K] = new WrappedTreeSet[K]

  private class WrappedTreeSet[K] extends WrappedTree[AnyRef, RB.Tree[K, Unit], K, RB.Tree[K, Unit]](redBlackTree[K, Unit]) {
    def wrapNode(node: AnyRef): RB.Tree[K, Unit] = node match {
      case node: TreeSet[K] ⇒ treeSetAccessor.get(node).asInstanceOf[RB.Tree[K, Unit]]
      case node: RB.Tree[K, Unit] ⇒ node
    }

    def unwrapElement(node: RB.Tree[K, Unit]): K = node.key

    def unwrapNode(node: RedBlackTree.Tree[K, Unit]) = node
  }

  private abstract class WrappedTree[N, WN, E, WE](underlying: Tree[WN, WE]) extends Tree[N, E] {

    def wrapNode(node: N): WN

    def unwrapNode(node: WN): N

    def unwrapElement(value: WE): E

    def childCount(node: N) = underlying.childCount(wrapNode(node))

    def value(node: N) = underlying.value(wrapNode(node)).map(unwrapElement)

    def child(node: N, index: Int) = unwrapNode(underlying.child(wrapNode(node), index))

    def label(node: N) = underlying.label(wrapNode(node))
  }

  private def redBlackTree[K, V]: Tree[RB.Tree[K, V], RB.Tree[K, V]] = new Tree[RB.Tree[K, V], RB.Tree[K, V]] {

    def childCount(tree: RB.Tree[K, V]) =
      if(tree eq null) 0
      else (if(tree.left ne null) 1 else 0) + (if(tree.right ne null) 1 else 0)

    def value(tree: RB.Tree[K, V]) =
      if(tree eq null) None else Some(tree)

    def child(tree: RB.Tree[K, V], index: Int) = {
      if(index < 0 || index > childCount(tree))
        throw new IndexOutOfBoundsException
      if(index == 0)
        if(tree.left ne null) tree.left else tree.right
      else
        tree.right
    }

    def label(tree: RB.Tree[K, V]) = tree match {
      case x: RB.RedTree[K, V] ⇒ "red"
      case x: RB.BlackTree[K, V] ⇒ "black"
      case _ ⇒ "empty"
    }
  }
}
