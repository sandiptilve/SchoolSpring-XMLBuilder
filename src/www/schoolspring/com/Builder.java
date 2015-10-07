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

import org.jsoup.Jsoup;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;


public class Builder {
	
	public static void main(String[] args) {
		
		BuildXML();
	}
	
	public static void BuildXML()
	{
		System.out.println("Generating XML...");
        DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder;
        try {
            icBuilder = icFactory.newDocumentBuilder();
            Document docIndeed = icBuilder.newDocument();
            Element mainRootElement = docIndeed.createElement("source");
            docIndeed.appendChild(mainRootElement);
            
            // append child elements to root element
            mainRootElement.appendChild(BuildElements(docIndeed, "publisher", "http://www.schoolspring.com"));
            mainRootElement.appendChild(BuildElements(docIndeed, "publisherurl", "http://www.schoolspring.com"));
            mainRootElement.appendChild(BuildElements(docIndeed, "lastBuildDate", (new Date()).toString()));
            GetJobData(docIndeed,mainRootElement);
            // output DOM XML to console 
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
            DOMSource source = new DOMSource(docIndeed);
            StreamResult console = new StreamResult(new StringWriter());
            transformer.transform(source, console);
            AddToDB(console.getWriter().toString().getBytes());
    	    //byte data[] = console.getWriter().toString().getBytes();
    	    //Path p = Paths.get("D:\\Indeed.xml");

//    	    try (OutputStream out = new BufferedOutputStream(
//    	      Files.newOutputStream(p, CREATE))) {
//    	      out.write(data, 0, data.length);
//    	      System.out.println("\n Created Successfully..");
//    	    } catch (IOException x) {
//    	      System.err.println(x);
//    	    }
            System.out.println("\nXML DOM Created Successfully..");
 
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
        	
        }
        
	}
	 private static Node BuildElements(Document doc, String name, String value) {
	     Element node = doc.createElement(name);
	     node.appendChild(doc.createTextNode(value));
	     return node;
	 }
	public static void GetJobData(Document doc,Element Main)
	{

		
		int count=1;

			ArrayList<JobData> oJobs=GetFormattedData();
			for(JobData oJob:oJobs)
			{
			System.out.println(count);
			Element node = doc.createElement("job");
			node.appendChild(BuildElements(doc,"title","<![CDATA["+oJob.feedTitle+"]]>"));
			node.appendChild(BuildElements(doc,"date","<![CDATA[#DateFormat(dOutput.DATA[i][14],'ddd, dd mmm yyyy')# 00:00:00 GMT]]>"));
			node.appendChild(BuildElements(doc,"referencenumber","<![CDATA["+oJob.feedJobID+"]]>"));
			node.appendChild(BuildElements(doc,"url","<![CDATA[#dOutput.DATA[i][21]#]]>"));
			node.appendChild(BuildElements(doc,"company","<![CDATA["+oJob.feedEmployer+"]]>"));
			node.appendChild(BuildElements(doc,"city","<![CDATA["+oJob.feedCity+"]]>"));
			node.appendChild(BuildElements(doc,"state","<![CDATA["+oJob.feedState+"]]>"));
			node.appendChild(BuildElements(doc,"country","<![CDATA["+oJob.feedCountry+"]]>"));
			node.appendChild(BuildElements(doc,"postalcode","<![CDATA["+oJob.feedPostalCode+"]]>"));
			node.appendChild(BuildElements(doc,"description","<![CDATA["+oJob.feedDescription+"]]>"));
			node.appendChild(BuildElements(doc,"salary","<![CDATA["+oJob.feedSalary+"]]>"));
			node.appendChild(BuildElements(doc,"education","<![CDATA["+oJob.feedEducation+"]]>"));
			node.appendChild(BuildElements(doc,"jobtype","<![CDATA["+oJob.feedJobType+"]]>"));
			node.appendChild(BuildElements(doc,"category","<![CDATA["+oJob.feedCategory+"]]>"));
			node.appendChild(BuildElements(doc,"experience","<![CDATA["+oJob.feedExperience+"]]>"));
			Main.appendChild(node);
			count ++;
			}
			
	}
	public static ArrayList<JobData> GetFormattedData()
	{
		ArrayList<JobData> objArray =new ArrayList<JobData>();
		Connection conn=DBConnect.Connect("jdbc:mysql://us-mm-por-11.cleardb.com/ssv2","ssdb","cyPm0tP5b!");
		ResultSet rs=DBConnect.ExcecuteSQLQueryToResultSet(conn,GetJobQuery());
		try {
			while (rs.next()) {
				JobData objJob=new JobData();
				objJob.feedJobID=rs.getString(1);
				objJob.feedTitle=rs.getString(2).replaceAll("[\\x00-\\x1f]","");
				if(rs.getString("category_id")=="9" && ! objJob.feedTitle.toLowerCase().contains("teach") )
				{
					objJob.feedTitle=objJob.feedTitle+"Teacher";
				}
				if(rs.getString("experienceRequired")!=null && ! rs.getString("experienceRequired").isEmpty() && Float.parseFloat(rs.getString("experienceRequired"))!=0 )
				{
					objJob.feedExperience=rs.getString("experienceRequired")+" year"+ (Float.parseFloat(rs.getString("experienceRequired"))>1?"s":"");
					objJob.feedExpYears=rs.getString("experienceRequired");
				}
				else if(rs.getString("experienceRequired")=="0")
				{
					objJob.feedExperience="none";
					objJob.feedExpYears="0";
				}
				else
				{
					objJob.feedExperience="";
					objJob.feedExpYears="";
				}
				
				if(rs.getBoolean("pay_display") && !(rs.getString("pay_min").isEmpty() || rs.getString("pay_max").isEmpty() ))
				{
					if(!rs.getString("pay_min").isEmpty())
					{
						objJob.feedSalary="$";
						if(isStringInt(rs.getString("pay_min")))
						{
							objJob.feedSalary=objJob.feedSalary+rs.getString("pay_min");//Format int 999
						}
						else
						{
							objJob.feedSalary=objJob.feedSalary+rs.getString("pay_min");//Format float 999.00
						}
					}
					else
					{
						objJob.feedSalary="Up";
					}
					
					if(!rs.getString("pay_max").isEmpty())
					{
						objJob.feedSalary=objJob.feedSalary+" to $";
						if(isStringInt(rs.getString("pay_max")))
						{
							objJob.feedSalary=objJob.feedSalary+rs.getString("pay_max");//Format int 999
						}
						else
						{
							objJob.feedSalary=objJob.feedSalary+rs.getString("pay_max");//Format float 999.00
						}
					}
					objJob.feedSalary=objJob.feedSalary+" "+ rs.getString("JobPayType_Name");
					objJob.feedSalaryMin=rs.getString("pay_min");
					objJob.feedSalaryMax=rs.getString("pay_max");
				}
				else
				{
					objJob.feedSalary="";
					objJob.feedSalaryMin="";
					objJob.feedSalaryMax="";
				}
				
				if(rs.getString("locationtype_id")!="15" || rs.getString("locationtype_id")!="200")
				{
					objJob.feedEmployer=rs.getString("location_name");
					if(! rs.getString("DisplayLocation").isEmpty())
					{
						objJob.feedCity=rs.getString("DisplayLocation");
					}
					else
					{
						objJob.feedCity=rs.getString("loc_city");
					}
					objJob.feedPostalCode=rs.getString("loc_postalcode");
					objJob.feedStateID=rs.getString("loc_state_id");
					objJob.feedCountryID=rs.getString("loc_country_id");
				}
				else
				{
					objJob.feedEmployer=rs.getString("employer_name");
					objJob.feedCity=rs.getString("emp_city");
					objJob.feedPostalCode=rs.getString("emp_postalcode");
					objJob.feedStateID=rs.getString("emp_state_id");
					objJob.feedCountryID=rs.getString("emp_country_id");
				}
				objJob.feedCountry=rs.getString("country_code");
				if(objJob.feedCountryID=="1")
				{
					objJob.feedState=rs.getString("state_abbrev");
					objJob.utm_content=objJob.feedState;
				}
				else
				{
					objJob.feedState="";
					objJob.utm_content="Intl";
				}
				objJob.feedCategory="Education/Training";
				if(rs.getInt("degreetype_id")>0)
				{
					objJob.feedEducation=rs.getString("degreetype_name");
				}
				else
				{
					objJob.feedEducation="Not Specified";
				}
				
				if(rs.getString("description").length()<30)
				{
					if(!objJob.feedEmployer.isEmpty())
					{
						objJob.feedDescription=objJob.feedEmployer+"is";
					}
					else
					{
						objJob.feedDescription="We are";
					}
					objJob.feedDescription+=" seeking applicants for the position of "+objJob.feedTitle+"."+rs.getString("description");
				}
				else
				{
					objJob.feedDescription=rs.getString("description");
					/*remove email addresses*/
					//objJob.feedDescription=rs.getString("description").replaceAll("[a-z0-9!##$%&'\*\+\-/=\?\^_`{}\|~]+(\.[a-z0-9!##$%&'\*\+\-/=\?\^_`{}\|~]+)*@([a-z0-9-]+\.)+[a-z]{2,7}","(see original posting)");
				}
				objJob.feedDescription=Jsoup.parse(objJob.feedDescription).text();
				objJob.feedDescription=objJob.feedDescription.replaceAll("\\$[\\d\\.]+","xxx");
				objJob.feedDescription=objJob.feedDescription.replaceAll("[\\x00-\\x1f]","");
				
				/*feed file is getting too big. This is temporary until we can fix*/
				if(objJob.feedDescription.length()>500)
				{
				objJob.feedDescription=objJob.feedDescription.substring(0,500);
				}
				
				if(rs.getString("requirements")!=null)
				{
				objJob.feedRequirements=rs.getString("requirements").replaceAll("[\\x00-\\x1f]","");
				}
				objJob.feedJobID=rs.getString("job_id")+"-"+rs.getString("location_id");
//				SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
//				long diff= format.parse(rs.getString("post_date")).getTime()-(new Date()).getTime();
//				int Offset= (int) (diff/30);
//				//objJob.feedJobDate=format.parse(rs.getString("post_date"));
//				if(Offset>0)
//				{
//					
//				}
				objJob.feedURL="http://www.SchoolSpring.com/job/?#feedJobID#&src=#url.src#&utm_content=#utm_content#&utm_campaign=Job+Search+Engines";
				
//				<cfif StructKeyExists(url,"utm_source")>
//				<cfset feedURL &= "&utm_source=" & url.utm_source>
//			</cfif>
//			<cfif StructKeyExists(url,"utm_medium")>
//				<cfset feedURL &= "&utm_medium=" & url.utm_medium>
//			</cfif>
				
				if(objJob.feedEducation.contains("bachelor") && rs.getString("category_id") =="9" )
				{
					objJob.feedDescription+="o-o-o";
				}
				objJob.feedJobType=rs.getString("jobtype_name");
				
				objArray.add(objJob);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objArray;
	}
	public static String GetJobQuery()
	{
		String sqlQuery="select j.Job_id,j.job_title,j.pay_max,j.pay_min,j.experienceRequired,j.description,j.pay_display,j.DisplayLocation,j.requirements,j.post_date,j.close_date,jpt.jobpaytype_name,jt.jobtype_name,dt.degreetype_id,dt.degreetype_name,e.employer_name, e.city as emp_city, e.postal_code as emp_postalcode, e.state_id as emp_state_id, e.country_id as emp_country_id,"+
		"l.location_id, l.location_name, l.locationtype_id, l.city as loc_city, l.postal_code as loc_postalcode, l.state_id as loc_state_id, l.country_id as loc_country_id,"+
		"min(jcp.parent_jobcategory_id) as category_id,(select country_code from D_country where country_id=l.country_id ) as country_code,(select state_abbrev from D_state where state_id=l.state_id ) as state_abbrev "+
		"from e_job j "+
		"join e_employer e on (j.employer_id = e.employer_id and j.jobstatus_id = 30 and j.demo = 0) "+
		"join e_job_location jl on (j.job_id = jl.job_id) "+
		"join e_location l on (jl.location_id = l.location_id) "+
		"join d_degreetype dt on (j.degree_preferred = dt.degreetype_id) "+
		"join d_jobpaytype jpt on (j.jobpaytype_id = jpt.jobpaytype_id) "+
		"left join d_jobtype jt on (j.jobtype_id = jt.jobtype_id) "+
		"left join e_job_jobcategory jc on (j.job_id = jc.job_id) "+
		"left join d_jobcategory_parent jcp on (jc.jobcategory_id = jcp.jobcategory_id) "+
		"group by j.job_id, loc_postalcode "+
		"order by j.job_id Limit 10";
		return sqlQuery;
	}
	
	public static void AddToDB(byte[] bData)
	{
		Connection conn=DBConnect.Connect("jdbc:mysql://localhost:3306/feeds","root","");
		String sqlQuery="Insert Into feeds1(feed_type,file_content) values('indeed','"+bData+"')";
		DBConnect.ExcecuteInsert(conn, sqlQuery);
	}
	
	public static boolean isStringInt(String s)
	{
	    try
	    {
	        Integer.parseInt(s);
	        return true;
	    } catch (NumberFormatException ex)
	    {
	        return false;
	    }
	}

}

