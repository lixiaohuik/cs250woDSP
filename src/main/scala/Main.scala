package CE
import Chisel._
import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer
import scala.util.Random
object Main {
  def main(args: Array[String]): Unit = {
    chiselMainTest(args,() => Module(new CE(p.ce))){c => new CETests(c)}
  }
}
