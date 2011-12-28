package com.randmath;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;


public class ShowProblem extends Activity{
	@Override
	   public void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	       
	       //Retrieving bundle from RandMathActivity
	       final Bundle bundle = this.getIntent().getExtras();
	       
	       //Retrieving strings from bundle
	       String area = bundle.getString("areaName");
	       String topic = bundle.getString("topicName");
	       
	        //Storing result of parseFile function into document
	        Document problemFile = parseFile();
	       
	        //Getting root element of document
	        Element docElem = problemFile.getDocumentElement();
	        
	        String problem = getProblemString(docElem,area,topic);
	        
	        
	        setContentView(R.layout.problem);
	        
	        //Using WebView to show problem .
	        WebView problemView = (WebView) findViewById(R.id.webview);
	        
	        String problemHtml = null;
	        
	        //Because of issues with rendering textblocks within mathjax,
	        //all word problems have to be rendered using <br/> instead of \\
	        //for newlines.
	        /*if(area == "Discrete Math"){
	        	problemHtml = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"" +
   					 "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">"+
   					 "<html style='background-color:#E6FEFE;font-size:x-large'>" +
   					 "<script type = 'text/javascript'  " +
   					 "src='http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML' >"+
   					 "</script>"+
   					 "<body>"+ 
   					 problem +
   					 "</body>" +
   					 "</html>";
	        }*/
	        
	        //Using MathJax JavaScript library to render LaTeX in WebView.
	        //\(...\) is wrapped around the problem LaTeX string for mathJax to 
	        //process it. backgroundcolor is same as @color/blue4 textcolor is @color/blue1
	        problemHtml = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"" +
	        					 "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">"+
	        		"<html style='background-color:#E6FEFE;font-size:large'>" +
	        		"<script type = 'text/javascript'  " +
	        		"src='http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML' >"+
	        		"</script>"+
	        		"<body>"+ 
	        		"\\("+problem +"\\)"+
	        		
	        		"</body>" +
	        		"</html>";
	        
	        
	      //Adding JavaScript functionality to WebView.
	        problemView.getSettings().setJavaScriptEnabled(true);
	        problemView.loadDataWithBaseURL(null, problemHtml, "text/html", "UTF-8", null);

	        
	}
	
	//Function to parse XML file
    public Document parseFile(){

    	
        //Parsing problemList.xml 
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        
        //Try/catch block for document building
        try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			//Try/catch block for parsing
			try {
				//Creating file
				URL url = new URL("http://swillis16.hostzi.com/problemList.xml");
	            InputStream stream = url.openStream();
				
				//Creating document to be returned
		    	Document doc = builder.parse(stream);
		    	return doc;
				
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
        
        return null;
    }

    //Function to display random problem contained in XML file. This function will use the areaName
    //and topicName passed in to create a nodelist from all problems in a topic, then randomly pick
    //from (0 to NodeList.size-1), and return the string contained within that problem node.
      public String getProblemString(Element root, String areaName, String topicName){
      	//Getting NodeList of all categories
      	NodeList catNodeList = root.getElementsByTagName("area");
      	
      	//Storing string values of categories
      	List <String> topicNames = new LinkedList<String>();
      	
      	//Node to store chosen area.
      	Element chosenArea = null;
      	
      	//Iterating through catNodeList to find matching areaName.
      	for(int i = 0;i < catNodeList.getLength();i++){
      		Element categoryElement = (Element)catNodeList.item(i);
      		
      		String catName = getElementValue(categoryElement, "areaName");
      		
      		//If category name is equal to string given, get current area and move down the DOM.
      		if(areaName.compareTo(catName) == 0){
      			chosenArea = (Element)catNodeList.item(i);
      			break;
      		}
      	}
      	
      	//Creating topic NodeList from chosen area.
      	NodeList topicList = chosenArea.getElementsByTagName("topic");
      	
        //Node to store chosen topic.
      	Element chosenTopic = null;
      	
      //Iterating through catNodeList to find matching topicName.
      	for(int i = 0;i < topicList.getLength();i++){
      		Element categoryElement = (Element)topicList.item(i);
      		
      		//Get name of topic 
      		String topName = getElementValue(categoryElement, "topicName");
      		
      		//If area name is equal to string given, get current area and move down the DOM.
      		if(topicName.compareTo(topName) == 0){
      			chosenTopic = (Element)topicList.item(i);
      			break;
      		}
      	}
      	
      	
      	//Storing all problems located under a topic in nodelist.
      	NodeList problemList = chosenTopic.getElementsByTagName("problem");
      	
      	//Creating random to generate random number between 0 and nodelist.size-1.
      	Random rand = new Random();
      	int randProblem = rand.nextInt(problemList.getLength()-1);
      	
      	//Retrieving problem located at random number generated.
      	Element problem = (Element)problemList.item(randProblem);
      	
      	//Return LaTeX string located within problem tags
      	return problem.getFirstChild().getNodeValue();
      }
    
  //Function to get text value if XML element exists.
    public String getElementValue(Element el, String tagName){
    	String textValue = null;
    	
    	NodeList nl =  el.getElementsByTagName(tagName);
    	
    	
    	if(nl != null && nl.getLength() > 0) {
			Node eleNode = nl.item(0);
			textValue = eleNode.getFirstChild().getNodeValue();
		}
    	
    	return textValue;
    	
    }
    
}
