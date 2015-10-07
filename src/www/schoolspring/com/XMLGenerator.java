package www.schoolspring.com;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.BufferedOutputStream;

import java.io.File;
 
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;


public class XMLGenerator extends TimerTask {

	public static void main(String[] args) {
	    TimerTask genXML = new XMLGenerator();
	    //perform the task once a day at 8 a.m., starting tomorrow morning
	    Timer timer = new Timer();
	    timer.scheduleAtFixedRate(genXML, getNightlyXMLTime(), fONCE_PER_DAY);

	}
	 @Override public void run(){
		    //toy implementation
	        try {

	        	Builder objBuild =new Builder(); 
	        	objBuild.BuildXML();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		  }
 
	  //expressed in milliseconds
	  private final static long fONCE_PER_DAY = 1000*60*60*24;

	  private final static int fONE_DAY = 0;
	  private final static int fFOUR_AM = 13;
	  private final static int fZERO_MINUTES = 01;

	  private static Date getNightlyXMLTime(){
	    Calendar tomorrow = new GregorianCalendar();
	    tomorrow.add(Calendar.DATE, fONE_DAY);
	    Calendar result = new GregorianCalendar(
	      tomorrow.get(Calendar.YEAR),
	      tomorrow.get(Calendar.MONTH),
	      tomorrow.get(Calendar.DATE),
	      fFOUR_AM,
	      fZERO_MINUTES
	    );
	    return result.getTime();
	  }
}

