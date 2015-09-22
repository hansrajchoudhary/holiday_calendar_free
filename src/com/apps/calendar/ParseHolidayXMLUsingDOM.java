package com.apps.calendar;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ParseHolidayXMLUsingDOM {
	private InputStream m_fileIS = null;

	String country;
	String tmpValue;
	DayEntry tmpEntry;

	public ParseHolidayXMLUsingDOM(InputStream file) {
		this.m_fileIS = file;
		parseDocument();
	}

	private void parseDocument() {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = builder.parse(m_fileIS);

			NodeList nodes = doc.getElementsByTagName("holiday");

			for (int i = 0; i < nodes.getLength(); i++) {

				Element element = (Element) nodes.item(i);

				NodeList country = element.getElementsByTagName("country");
				String countryStr = ((Element) country.item(0)).getFirstChild().getNodeValue();
				String codeStr = "";
				try{
					NodeList code = element.getElementsByTagName("code");
					codeStr = ((Element) code.item(0)).getFirstChild().getNodeValue();
				}
				catch(Exception e){}
				
				NodeList entries = element.getElementsByTagName("entry");
				for (int j = 0; j < entries.getLength(); j++) {
					Element line = (Element) entries.item(j);
					String dayStr="";
					String nameStr="holiday";
					
					NodeList day = line.getElementsByTagName("day");
					dayStr = ((Element) day.item(0)).getFirstChild().getNodeValue();
					
					//-------------//
					if(dayStr.split(" ")[1].length()==1)
					{
						dayStr = dayStr.split(" ")[0]+" 0"+dayStr.split(" ")[1];
					}
					//------------//
						
					try{
					NodeList name = line.getElementsByTagName("name");
					nameStr = ((Element) name.item(0)).getFirstChild().getNodeValue();
					}
					catch(Exception e)
					{
						//ignore
					}
					DbEntry de = new DbEntry();
					de.setCountry(countryStr);
					de.setCode(codeStr);
					de.setDay(dayStr);
					de.setHoliday(nameStr);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}