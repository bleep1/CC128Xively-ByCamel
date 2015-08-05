package brian;

import java.util.Date;

import org.apache.camel.Exchange;


/**
 * Current Cost raw serial data transform to Raw CSV data.
 * Only handle type=1 sensors...That's an electricty sensor...That's all my sensors
 * 
 * What comes in:
 * ==============
 * <msg><src>CC128-v1.31</src><dsb>01300</dsb><time>15:47:39</time><tmpr>26.4</tmpr><sensor>0</sensor><id>00077</id><type>1</type><ch1><watts>00723</watts></ch1></msg>
 *
 * What goes out:
 * ==============
 *  timestamp, dsb,    tmpr,  sensor,  id,   type,  watts1,  watts2,  watts3 
 * 
 * 
 * @author brian
 *
 */
public class CCToRawXForm extends CCDataHandler {
	
	
	public void transform(Exchange exchange) {
		String incoming = exchange.getIn().getBody().toString();
		 //  time, dsb,    tmpr,  sensor,  id,   type,  watts1,  watts2,  watts3 
		Integer dsb = getInteger("dsb", incoming);
		float savedTmpr = getFloat("tmpr", incoming);
		Integer sensorNum = getInteger("sensor", incoming);
		Integer id = getInteger("id", incoming);
		Integer type = getInteger("type", incoming);
		Integer ch1_watts = getWattsFromChannel(1, incoming);
		Integer ch2_watts = getWattsFromChannel(2, incoming);
		Integer ch3_watts = getWattsFromChannel(3, incoming);
		StringBuffer sb = new StringBuffer();
		sb
		.append("'" + Utils.sdf.format(new Date()) + "'")
		.append(", ")
		.append(dsb)
		.append(", ")
		.append(savedTmpr)
		.append(", ")
		.append(sensorNum)
		.append(", ")
		.append(id)
		.append(", ")
		.append(type)
		.append(", ")
		.append(ch1_watts)
		.append(", ")
		.append(ch2_watts)
		.append(", ")
		.append(ch3_watts);
        exchange.getIn().setBody(sb.toString());
	}

	
	
	/** 
	 * Test this class
	 * @param args
	 */
	public static void main(String args[]) {
		CCToRawXForm c = new CCToRawXForm();
		String in = "<msg><src>CC128-v1.31</src><dsb>01300</dsb><time>15:47:39</time><tmpr>26.4</tmpr><sensor>0</sensor><id>00077</id><type>1</type><ch1><watts>00723</watts></ch1></msg>";
		System.out.println("w=" + c.getWattsFromChannel(1, in));
	}
	
	
}
/*
 * SOME SAMPLE DATA in
 * ==================
 * <msg>
 *  <src>CC128-v1.31</src>
 *  <dsb>01300</dsb>
 *  <time>15:47:38</time>
 *  <tmpr>26.4</tmpr>
 *  <sensor>1</sensor>
 *  <id>00629</id>
 *  <type>1</type>
 *  <ch1> 
 *   <watts>00001</watts>
 *  </ch1>
 * </msg>
 * <msg><src>CC128-v1.31</src><dsb>01300</dsb><time>15:47:39</time><tmpr>26.4</tmpr><sensor>0</sensor><id>00077</id><type>1</type><ch1><watts>00723</watts></ch1></msg>
 * <msg><src>CC128-v1.31</src><dsb>01300</dsb><time>15:47:41</time><tmpr>26.4</tmpr><sensor>3</sensor><id>00513</id><type>1</type><ch1><watts>00150</watts></ch1></msg>
 *
 * SOME SAMPLE DATA out
 * ==================
 *  dsb, time,    tmpr,  sensorNum,  id,   type,  watts 
 * 1300, 2015-08-12T15:47:38, 26.4, 1, 00629, 1, 234 
 *  

*/