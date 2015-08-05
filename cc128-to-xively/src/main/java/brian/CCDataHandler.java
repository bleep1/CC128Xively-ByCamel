package brian;

abstract public class CCDataHandler {

	public CCDataHandler() {
		super();
	}

	public String getElement(String tag, String msg) {
		int start = msg.indexOf("<" + tag + ">") + tag.length() + 2;
		int end = msg.indexOf("</" + tag);
		String element = msg.substring(start, end);
		return element;
	}

	protected Float getFloat(String tag, String msg) {
		String element = getElement(tag, msg);
		Float f = new Float(element);
		if (f == null)
			return new Float(0.0f);
		else
			return f;
	}

	protected Integer getInteger(String tag, String msg) {
		String element = getElement(tag, msg);
		Integer i = new Integer(element);
		if (i == null)
			return new Integer(0);
		else
			return i;
	}

	public Integer getWattsFromChannel(int channel, String msg) {
		String ch = "ch" + channel;
		try {
			String chElement =  getElement(ch, msg);
			return getInteger( "watts", chElement);
		} catch (IndexOutOfBoundsException iobe) {
			return 0;
		}
	}

}