//package CE
import Chisel._
//import scala.io.Source
//import java.io._


// WARNING: MULTIPLE CLOCK DOMAINS NOT SUPPORTED. POKED LOGIC MUST BE CLOCKED! (For VerilogTB to function)

/** Module tester that allows switching between fixed and floating point testing */
class CETests(c: CE) extends Tester(c) {

//val r_test_input = Source.fromFile("r_test.txt").getLines.toArray
//val i_test_input = Source.fromFile("i_test.txt").getLines.toArray
//
//val r_writer = new PrintWriter(new File("r_output.txt"))
//val i_writer = new PrintWriter(new File("i_output.txt"))
//for (i <- 0 until r_test_input.length){
//          poke(c.io.signalIn_real, r_test_input(i).toDouble)
//          poke(c.io.signalIn_imag, i_test_input(i).toDouble)
//	  var r_output:Double = peek(c.io.signalOut_real)
//	  var i_output:Double = peek(c.io.signalOut_imag)
//	  r_writer.write(r_output.toString)
//	  i_writer.write(i_output.toString)
//	  step(1)
//	  }
//r_writer.close()
//i_writer.close()
poke(c.io.signalIn_real, UInt(2))
poke(c.io.signalIn_imag, UInt(2))
expect(c.io.signalOut_real, UInt(1))
expect(c.io.signalOut_imag, UInt(1))
step (1)
poke(c.io.signalIn_real, UInt(2))
poke(c.io.signalIn_imag, UInt(2))
expect(c.io.signalOut_real, UInt(1))
expect(c.io.signalOut_imag, UInt(1))
step (1)
poke(c.io.signalIn_real, UInt(2))
poke(c.io.signalIn_imag, UInt(2))
expect(c.io.signalOut_real, UInt(1))
expect(c.io.signalOut_imag, UInt(1))
step (1)

}
