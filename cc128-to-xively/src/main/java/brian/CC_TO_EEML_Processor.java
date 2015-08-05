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
public class CC_TO_EEML_Processor extends CCDataHandler {
	private static Hashtable<Integer, Integer> savedResults = new Hashtable<Integer, Integer>();
	private static Float savedTmpr = 0.0f;
	
	public CC_TO_EEML_Processor() {
	}

	/*
	 * Number of Sensors including the all house sensor. These will be numbered
	 * 0=all house
	 * 1
	 * 2
	 * 3
	 * etc
	 */
	private int numberOfSensors = 4;  //default to 4 because that's what I use today. Two more coming
	public int getNumberOfSensors() {
		return numberOfSensors;
	}
	public void setNumberOfSensors(int numberOfSensors) {
		this.numberOfSensors = numberOfSensors;
		System.out.println("***********************************************************************");
		System.out.println("cc_to_eeml_processor now set numberOfSensors (inc whole house) to: " + numberOfSensors);
		System.out.println("***********************************************************************");
	}

	public void process(Exchange exchange) {
		String incoming = exchange.getIn().getBody().toString();
		
		savedTmpr = getFloat("tmpr", incoming);
		Integer sensorId = getInteger("sensor", incoming);
		Integer watts = getInteger("watts", incoming);
		savedResults.put(sensorId, watts);
        
        String eeml = constructEEML();
        exchange.getIn().setBody(eeml);
        exchange.getIn().setHeader("CC_MSG_TYPE", "EEML");
	}//that's it!!!
	
	/**
	 * @see https://personal.xively.com/dev/docs/api/data/write/single_datapoint_to_each_datastream/ 
	 * @return
	 */
	private String constructEEML() {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<eeml>");
		sb.append("<environment>\n");		
//		sb.append("<data id=\"0\"><current_value>" + getStrf(savedTmpr) + "</current_value></data>");
//		sb.append("<data id=\"1\"><current_value>" + getStr(savedResults.get(new Integer(0))) + "</current_value></data>");
//		sb.append("<data id=\"2\"><current_value>" + getStr(savedResults.get(new Integer(1))) + "</current_value></data>");
//		sb.append("<data id=\"3\"><current_value>" + getStr(savedResults.get(new Integer(2))) + "</current_value></data>");
//		sb.append("<data id=\"4\"><current_value>" + getStr(savedResults.get(new Integer(3))) + "</current_value></data>");

		sb.append("<data id=\"0\"><current_value>" + getStrf(savedTmpr) + "</current_value></data>\n");
		for (int i=1; i<=numberOfSensors; i+=1) {
			sb.append("<data id=\""+ i + "\"><current_value>" + getStr(savedResults.get(i-1)) + "</current_value></data>\n");
		}
		sb.append("</environment>");
		sb.append("</eeml>");
		return sb.toString();
	}
	
	private String getStrf(Float f) {
		if (f == null) 
			return "0.0";
		else
			return f.toString();
	}
	private String getStr(Integer i) {
		if (i == null) 
			return "0";
		else
			return i.toString();
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
 *     <data id="1"><current_value>00252</current_value></data>
 *     <data id="2"><current_value>00452</current_value></data>
 *   </environment>
 * </eeml>
 */
	
}
