package brian;

import java.util.Hashtable;

import org.apache.camel.Exchange;


/**
 * A transformer from CurrentCost MSGs to EEML messages for Xively.
 * 
 * I know it breaks the original goal of doing a Current Cost to Xively using java without
 * writing any java, but it seemed this was the simplest way to do this simple transform.
 * 
 * I also recognize that using statics like this is a little cheating, but for this 
 * purpose it does the job just fine.
 * 
 * 
 * savedResults is used to fudge data points between data upload messages. Each send
 * to Xively sends a full sets of data points instead of just the one it got from
 * the CurrentCost Envi.
 * 
 * I use to do this with the Arduino code and I prefer it because the google data plotter
 * needs matching data data-points.
 * 
 * @author brian
 *
 */
public class CC_TO_EEML_Processor {
	private static Hashtable<Integer, Integer> savedResults = new Hashtable<Integer, Integer>();
	private static Float savedTmpr = 0.0f;
	
	public CC_TO_EEML_Processor() {
	}

	public void process(Exchange exchange) {
		String incoming = exchange.getIn().getBody().toString();
		
		//pluck out the incoming new value and remember in the cachedResults
		//create the new outgoing message to send to Xively
		
		savedTmpr = getFloat("tmpr", incoming);
		Integer sensorId = getInteger("sensor", incoming);
		Integer watts = getInteger("watts", incoming);
		savedResults.put(sensorId, watts);
        
        String eeml = constructEEML();
        exchange.getIn().setBody(eeml);
	}//that's it!!!

	

	private Float getFloat(String tag, String msg) {
		String element = getElement(tag, msg);
		return new Float(element);
	}
	private Integer getInteger(String tag, String msg) {
		String element = getElement(tag, msg);
		return new Integer(element);
	}
	
	
	
	private String getElement(String tag, String msg) {
		int start = msg.indexOf("<" + tag + ">") + tag.length() + 2;
		int end = msg.indexOf("</" + tag);
		String element = msg.substring(start, end);
		return element;
	}
	
	private String constructEEML() {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<eeml>");
		sb.append("<environment>");		
		sb.append("<data id=\"0\"><current_value>" + savedTmpr + "</current_value></data>");
		sb.append("<data id=\"1\"><current_value>" + savedResults.get(new Integer(0)) + "</current_value></data>");
		sb.append("<data id=\"2\"><current_value>" + savedResults.get(new Integer(1)) + "</current_value></data>");
		sb.append("<data id=\"3\"><current_value>" + savedResults.get(new Integer(2)) + "</current_value></data>");
		sb.append("<data id=\"4\"><current_value>" + savedResults.get(new Integer(3)) + "</current_value></data>");
		sb.append("</environment>");
		sb.append("</eeml>");
		return sb.toString();
	}
	
/*
 * SOME SAMPLE DATA
 * 
 * <msg>
 * <src>CC128-v1.31</src>
 * <dsb>01300</dsb>
 * <time>15:47:38</time>
 * <tmpr>26.4</tmpr>
 * <sensor>1</sensor><id>00629</id><type>1</type><ch1><watts>00001</watts></ch1>
 * </msg>

 * <msg><src>CC128-v1.31</src><dsb>01300</dsb><time>15:47:39</time><tmpr>26.4</tmpr><sensor>0</sensor><id>00077</id><type>1</type><ch1><watts>00723</watts></ch1></msg>

 * <msg><src>CC128-v1.31</src><dsb>01300</dsb><time>15:47:41</time><tmpr>26.4</tmpr><sensor>3</sensor><id>00513</id><type>1</type><ch1><watts>00150</watts></ch1></msg>

 * 
 * 
 * 
 * 
 * 
 * <?xml version="1.0" encoding="UTF-8"?>
 * <eeml>
 *   <environment>
 *     <data id="0"><current_value>26.3</current_value></data>
 *     <data id="3"><current_value>00252</current_value></data>
 *   </environment>
 * </eeml>
 */
	
}
