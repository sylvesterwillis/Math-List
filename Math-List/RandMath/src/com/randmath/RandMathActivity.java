/********************************************************************
 * Author: Sylvester Willis                                         *
 * This program will allow a user to select from various categories *
 * of math problems. The categories are generated from an xml file, *
 * and the problems are rendered using the JLaTeXMath library after *
 * being red from the XML file.                                     *
 ********************************************************************/

package com.randmath;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;

public class RandMathActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Storing result of parseFile function into document
        Document problemFile = parseFile();
       
        //Getting root element of document
        Element docElem = problemFile.getDocumentElement();
        
        //Displaying category list
        List <String> catList = getCategoryList(docElem);
        
        setContentView(R.layout.main);
        
        //Initializing ListView
        final ListView categories = (ListView)findViewById(R.id.listView1);
        
        //Creating TextView for category list header
        TextView header = new TextView(this);
        header.setText(getString(R.string.areaHelp));
        header.setBackgroundResource(R.color.blue4);
        
        //Creating Header for ListView
        categories.addHeaderView(header,null,false);
        
        // Updating ListView using category List.
        ArrayAdapter <String>listAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, catList);
        
      //Setting adapter used for sections.
        categories.setAdapter(listAdapter);
        
        
        //Whenever a list item is pressed, a new activity will run to show the sections of the category chosen.
        categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
            	
            	//Getting text from listview item that was clicked
            	String clickedCategory = (String)categories.getAdapter().getItem(position);
            	
            	//Creating bundle to pass category name string to ShowSections activity
            	Bundle bundle = new Bundle();
            	bundle.putString("areaName", clickedCategory);

                Intent newActivity = new Intent(view.getContext(),ShowSections.class);  
                newActivity.putExtras(bundle);
                startActivity(newActivity);

                }
              });

    }
    
    
    //Function to display categories contained in XML file.
    public List getCategoryList(Element root){
    	//Getting NodeList of all categories
    	NodeList catNodeList = root.getElementsByTagName("area");
    	
    	//Storing string values of categories
    	List <String> categoryNames = new LinkedList<String>();
    	
    	//Moving category names from XML to LinkedList
    	for(int i = 0;i < catNodeList.getLength();i++){
    		Element catElement = (Element)catNodeList.item(i);
    		
    		//Getting string value of category and adding to list
    		categoryNames.add(getElementValue(catElement,"areaName"));
    		
    	}
    	
    	return categoryNames;
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
    
    //Function to get text value if XML element
    public String getElementValue(Element el, String tagName){
    	String textValue = null;
    	
    	NodeList nl = el.getElementsByTagName(tagName);
    	
    	
    	if(nl != null && nl.getLength() > 0) {
			Node eleNode = nl.item(0);
			textValue = eleNode.getFirstChild().getNodeValue();
		}
    	
    	return textValue;
    	
    }
}