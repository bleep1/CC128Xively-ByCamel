package brian;

import org.apache.camel.Exchange;

/**
 * load a DB table from the CC agg data
 * 
 * What comes in:
 * ==============
 *  timestamp1, dsb,    tmpr,  sensor,  id,   type,  watts1,  watts2,  watts3 
 *  timestamp2, dsb,    tmpr,  sensor,  id,   type,  watts1,  watts2,  watts3 
 *  timestamp3, dsb,    tmpr,  sensor,  id,   type,  watts1,  watts2,  watts3 
 * 
 * What goes out:
 * ==============
 * 
 * 
 * 
 * 
 * INSERT INTO RAW_SENSOR_DATA 
 *         (SAMPLE_TIMESTAMP,DSB,TEMPERATURE,SENSOR_NUMBER,SENSOR_ID,CH1_WATTS,CH2_WATTS,CH3_WATTS) 
 * VALUES ( '2015-07-27 22:11:09.000' ,0,0,0,0,0,0,0,0), 
 *        ( '2015-07-27 22:11:09.123' ,1,2,3,4,5,6,7,8);
 *
 * 
 * 
CREATE TABLE RAW_SENSOR_DATA
(
   SAMPLE_TIMESTAMP timestamp PRIMARY KEY NOT NULL,
   DSB int,
   TEMPERATURE decimal(5,2),
   SENSOR_NUMBER int,
   SENSOR_ID int,
   SENSOR_TYPE int,
   CH1_WATTS int,
   CH2_WATTS int,
   CH3_WATTS int
)
;
CREATE UNIQUE INDEX PRIMARY ON RAW_SENSOR_DATA(SAMPLE_TIMESTAMP)
;

 * 
 * @author brian
 *
 */
public class CCAggToSQLInsertXForm {
	
	private StringBuilder payload = new StringBuilder();
	private boolean firstRecDone = false;
	
	public void transform(Exchange exchange) {
		String incoming = exchange.getIn().getBody(String.class);
		String[] inArray = incoming.split("\n");
		initTransform();
		for (String line : inArray) {
			processLine(line);
		}
		String rtnPayload = buildRtnPayload();
		exchange.getIn().setBody(rtnPayload);
	}
	
	private void initTransform() {
		payload = new StringBuilder();
		firstRecDone = false;
	}
	
	/**
	 * insert into table values 
	 * 
 * INSERT INTO RAW_SENSOR_DATA 
 *         (SAMPLE_TIMESTAMP,DSB,TEMPERATURE,SENSOR_NUMBER,SENSOR_ID,SENSOR_TYPE,VALUE1,VALUE2,VALUE3) 
 * VALUES ( '2015-07-27 22:11:09.000' ,0,0,0,0,0,0,0), 
 *        ( '2015-07-27 22:11:09.123' ,0,0,0,0,0,0,0);

    
	 * @param line
	 */
	private String buildRtnPayload() {
		StringBuilder sb = new StringBuilder("INSERT INTO `house_power`.`RAW_SENSOR_DATA` ");
		sb.append("(`SAMPLE_TIMESTAMP`, `DSB`, `TEMPERATURE`, `SENSOR_NUMBER`, `SENSOR_ID`, `SENSOR_TYPE`, `VALUE1`, `VALUE2`, `VALUE3`)")
		.append(" VALUES ")
		.append(payload)
		.append(";");
		return sb.toString();
	}
	
	
	
	
	private void processLine(String line) {
		if (firstRecDone) {
			payload.append(",\n");
		} else {
			firstRecDone = true;
		}
		payload.append("(" + line + ")");
	}
	


}
