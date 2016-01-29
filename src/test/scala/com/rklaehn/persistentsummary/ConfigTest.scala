package com.rklaehn.persistentsummary

import com.rklaehn.persistentsummary.PersistentSummary.Config
import org.scalatest.FunSuite

class ConfigTest extends FunSuite {

  test("config gets picked up from implicit scope") {
    implicit val config: Config = null
    intercept[NullPointerException] {
      PersistentSummary.treeSet(IntLongSummary)
    }
  }
}
