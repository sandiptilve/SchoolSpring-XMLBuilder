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


public class Indeed {
	
	public void BuildXML()
	{
		System.out.println("Generating XML...");
        DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder;
        try {
            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();
            Element mainRootElement = doc.createElement("source");
            doc.appendChild(mainRootElement);
            
            // append child elements to root element
            mainRootElement.appendChild(BuildElements(doc, "publisher", "http://www.schoolspring.com"));
            mainRootElement.appendChild(BuildElements(doc, "publisherurl", "http://www.schoolspring.com"));
            mainRootElement.appendChild(BuildElements(doc, "lastBuildDate", "http://www.schoolspring.com"));
 
            // output DOM XML to console 
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
            DOMSource source = new DOMSource(doc);
            StreamResult console = new StreamResult(new StringWriter());
            transformer.transform(source, console);
    	    byte data[] = console.getWriter().toString().getBytes();
    	    Path p = Paths.get("D:\\Indeed.xml");

    	    try (OutputStream out = new BufferedOutputStream(
    	      Files.newOutputStream(p, CREATE, APPEND))) {
    	      out.write(data, 0, data.length);
    	      System.out.println("\n Created Successfully..");
    	    } catch (IOException x) {
    	      System.err.println(x);
    	    }
            System.out.println("\nXML DOM Created Successfully..");
 
        } catch (Exception e) {
            e.printStackTrace();
        }
        
	}
	 private static Node BuildElements(Document doc, String name, String value) {
	     Element node = doc.createElement(name);
	     node.appendChild(doc.createTextNode(value));
	     return node;
	 }
	public String GetJobData()
	{
		return "";
	}

}

