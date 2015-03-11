# CC128Xively-ByCamel
Current Cost 128 to Xively using Apache Camel on a Odroid ARM board

So I've heard this Apache Camel thing is powerful and easy to use, and my old Arduino-cc128 rig is getting dusty and old. So I layed down a new challenge: 

* Can I re-develop the CC-Envi to Xively interface using Apache Camel.  
* Even more, can I do this without writing a single line of java code even though Camel is an integration library using java.

Well it turns out it is do-able. Here is the few lines of code you need to connect your Current Cost Envi to Xively via a Raspberry Pi - or in my case an Odroid (http://www.hardkernel.com/main/products/prdt_info.php?g_code=G141578608433).

# What it does:

Uses an Apache Camel route to receive incoming streaming data from a file
Route then:

* Recieve Envi data as a Stream from a file. (in my case a named pipe to decouple the real source. That means i have to cat /dev/ttyUSB0 > /somedir/mypipe.txt ) -> 
* splt on MSG tag -> 
* XML-transform using XSLT from Envi-XML to Xively-EEML-XML  -> 
* send to Xively using MQTT

An ASCII art of the this can be found at https://github.com/bleep1/CC128Xively-UsingCamel/wiki 


# What you need:
* Current Cost 128 Envi:  http://currentcost.com/product-envi.html  
* Data cable for CC128 Envi (This is a serial to USB cable. You might be able to hack one from old unwanted junk)
  I got my CC128 Envi parts from http://www.smartnow.com.au/ 
* Ordoid or Raspberry Pi.  I use Odroid because they are the same cost as RaPi's byt have a lot more Bang.
* Java installed on the Odroid
* Apache Mavan installed on the Odroid
* Xively account from www.Xively.com
* This source code.

# Getting it going:

1) On the Xively account make a new Feed. The number of datastreams should be at least the number of CC-Envi sensors+1 you have. In my case I monitor whole house Power plus a few key power points in the house. (Such as Server power usage :(  
* The +1 is because the CC-Envi also reports current temperature at the device so we may as well log that too.

2) plug in the Envi Serial cable

3) Download this code

4) Edit the Camel Route chaninging
* your API-Key.  
* your feed_id.
* (leave no spaces in the URIs)

5) mvn camel:run

5a) Remeber Envi serial is 57600   so do a stty -F /dev/ttyUSB0 57600
5b) Remember if you are streaming from a named pipe instead of from /dev/ttyUSB0 then you have to cat ttyUSB0 to pipe

6) Hey presto, see your Envi data up on Xively!!!!



# What does this show
I'm amazed that I used 
* MQTT, 
* XSLT, 
* Logging in Log4j, 
* a Linux serial port and 
* Apache Camel

All these libraries without writing one line of Java. Even though they are all Java based.
 

# To do:
* make a cleaner install package/


Note: Some people seem to call a Current Cost Envi  a Current Cost CC128.
