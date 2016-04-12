package CE

import Chisel._
import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer
import scala.util.Random


/** Module that supports both fixed and floating point testing */
// assum have the pilot tone first?

case class CEParams (
    
   mu:		Double	= 1.0,
   alpha:	Double	= 1.0,
   pt_position: Int	= 2,
   frame_size: 	Int 	= 4,
   min_value:	Double  = -255.0,			// DSPFixed uses min value to determine bit width rather than actual bit width,
   max_value: 	Double 	= 255.0,				// DSPFixed uses max value to determine bit width rather than actual bit width,
   frac_width:	Int	= 32,				// DSPFixed has extra argument for fraction width,

  // At some point I'd like to make width > 1 so the pt_values should be vectors rather than one value once that happens
   pt_value_r: 	Int = 1,	
   pt_value_i: 	Int = 1,
   width: 		Int	= 5			// Currently unused since width is 1

)

class CE(p : CEParams) extends Module {

  val io = new Bundle {

  val signalIn_real = UInt(INPUT)
  val signalIn_imag = UInt(INPUT)
  val signalOut_real = UInt(OUTPUT)
  val signalOut_imag = UInt(OUTPUT)

}

  val pt_number = (math.ceil(p.frame_size.toDouble/p.pt_position.toDouble)).toInt 
  val stored_Weight_r = Vec.fill(pt_number){RegInit(UInt(1))}
  val stored_Weight_i = Vec.fill(pt_number){RegInit(UInt(1))}
  val tmp_weight_r = UInt(1)
  val tmp_weight_i = UInt(1)
 
//determine whether is pilot tone using the position comb type
  val sigCount = UInt(0)
  val PTCount = UInt(0)
  val IsPT = Bool(false) //no need here, but needed for other PT position
  when (sigCount =/= UInt(p.frame_size) ){
    sigCount := sigCount + UInt(1)
  }.otherwise{
    sigCount := UInt(0)
    PTCount := UInt(0)
  }
  when (sigCount%UInt(p.pt_position) === UInt(1) ){ //input is a PT
    IsPT := Bool(true)
    PTCount := PTCount + UInt(1)
    //do calculation here
    tmp_weight_r := stored_Weight_r(PTCount)
    tmp_weight_i := stored_Weight_i(PTCount)
    io.signalOut_real := tmp_weight_r * io.signalIn_real
    io.signalOut_imag := tmp_weight_i * io.signalIn_imag
    val error_r = p.pt_value_r - io.signalOut_real
    val error_i = p.pt_value_i - io.signalOut_imag
    val f_r = UInt(p.mu) * error_r * io.signalIn_real
    val f_i = UInt(p.mu) * error_i * io.signalIn_imag
    stored_Weight_r(PTCount) := UInt(p.alpha) * tmp_weight_r + f_r
    stored_Weight_i(PTCount) := UInt(p.alpha) * tmp_weight_i + f_i
  }.otherwise{
    IsPT := Bool(false)
    //do calculation here
    tmp_weight_r := stored_Weight_r(PTCount)
    tmp_weight_i := stored_Weight_i(PTCount)
    io.signalOut_real := tmp_weight_r * io.signalIn_real
    io.signalOut_imag := tmp_weight_i * io.signalIn_imag
  }
}
