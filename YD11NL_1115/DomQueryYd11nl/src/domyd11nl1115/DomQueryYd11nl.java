package domyd11nl1115;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DomQueryYd11nl {
	
	public static void main(String[] args) {
		try {
			File xmlFile = new File("orarendYd11nl.xml");
			File bOutput = new File("elsoPeldany.xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(xmlFile);
			document.getDocumentElement().normalize();
			DOMQuery query = new DOMQuery();
			List<String> courseNames = query.getCourseNames(document);
			//a
			System.out.println("a) Kurzusok: "+courseNames+"\n");
			//b
			System.out.println("b) Az első elem:");
			String firstInstance = query.getFirst(document);
			System.out.println(firstInstance+"\n");
			FileWriter writer = new FileWriter(bOutput);
			writer.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n");
			writer.write(firstInstance);
			writer.close();
			//c
			System.out.println("c) Oktatók: "+query.getTeachers(document)+"\n");
			//d
			System.out.println("d) Kurzusok időpontjai: "+query.getTimes(document));
		} catch (IOException | SAXException | ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	private static class DOMQuery{
		public List<String> getCourseNames(Document document){
			List<String> courseNames = new ArrayList<>();
			Element root = document.getDocumentElement();
			NodeList courses = root.getElementsByTagName("ora");
			for(int i=0; i<courses.getLength(); i++) {
				Node course = courses.item(i);
				NodeList names = ((Element)course).getElementsByTagName("targy");
				for(int j=0; j<names.getLength(); j++) {
					if(!courseNames.contains(names.item(j).getTextContent())) {
						courseNames.add(names.item(j).getTextContent());
					}
				}
			}
			return courseNames;
		}
		public String getFirst(Document document) {
			Element root = document.getDocumentElement();
			Node firstCourse = root.getElementsByTagName("ora").item(0);
		
			return writeCourse(firstCourse);
		}
		public List<String> getTeachers(Document document){
			List<String> teacherNames = new ArrayList<>();
			Element root = document.getDocumentElement();
			NodeList courses = root.getElementsByTagName("ora");
			for(int i=0; i<courses.getLength(); i++) {
				Node course = courses.item(i);
				Node name = ((Element)course).getElementsByTagName("oktato").item(0);
				if(!teacherNames.contains(name.getTextContent())) {
					teacherNames.add(name.getTextContent());
				}
			}
			return teacherNames;
		}
		public List<String> getTimes(Document document){
			List<String> times = new ArrayList<>();
			Element root = document.getDocumentElement();
			NodeList courses = root.getElementsByTagName("ora");
			for(int i=0; i<courses.getLength(); i++) {
				Node course = courses.item(i);
				Node time = ((Element)course).getElementsByTagName("idopont").item(0);
				Element day = (Element)((Element)time).getElementsByTagName("nap").item(0);
				Element from = (Element)((Element)time).getElementsByTagName("tol").item(0);
				Element to = (Element) ((Element)time).getElementsByTagName("ig").item(0);
				times.add(day.getTextContent()+" "+from.getTextContent()+"-"+to.getTextContent());
			}
			return times;
		}
		private String writeCourse(Node courseIn) {
			String output = "";
			String indentStr="   ";
			int indent = 0;
			Element course = (Element)courseIn;
			output+=(indentStr.repeat(indent)+"<ora");
			NamedNodeMap attributes = course.getAttributes();
			output+=printAttributes(attributes);
			
			Element subject = (Element) course.getElementsByTagName("targy").item(0);
			indent++;
			output+=(indentStr.repeat(indent)+"<targy>"+subject.getTextContent()+"</targy>\n");
			
			Element time = (Element) course.getElementsByTagName("idopont").item(0);
			output+=printTime(time, indent, indentStr);
			
			Element place = (Element) course.getElementsByTagName("helyszin").item(0);
			output+=(indentStr.repeat(indent)+"<helyszin>"+place.getTextContent()+"</helyszin>\n");
			Element teacher = (Element) course.getElementsByTagName("oktato").item(0);
			output+=(indentStr.repeat(indent)+"<oktato>"+teacher.getTextContent()+"</oktato>\n");
			Element major = (Element) course.getElementsByTagName("szak").item(0);
			output+=(indentStr.repeat(indent)+"<szak>"+major.getTextContent()+"</szak>\n");
			indent--;
			output+=(indentStr.repeat(indent)+"</ora>");
			return output;
		}
		private static String printTime(Element time, int indent, String indentStr) {
			String output = "";
			output+=(indentStr.repeat(indent)+"<idopont>\n");
			Element day = (Element) time.getElementsByTagName("nap").item(0);
			Element from = (Element) time.getElementsByTagName("tol").item(0);
			Element to = (Element) time.getElementsByTagName("ig").item(0);
			indent++;
			output+=(indentStr.repeat(indent)+"<nap>"+day.getTextContent()+"</nap>\n");
			output+=(indentStr.repeat(indent)+"<tol>"+from.getTextContent()+"</tol>\n");
			output+=(indentStr.repeat(indent)+"<ig>"+to.getTextContent()+"</ig>\n");
			indent--;
			output+=(indentStr.repeat(indent)+"</idopont>\n");
			return output;
		}
		private static String printAttributes(NamedNodeMap attributes) {
			String output = "";
			if(attributes.getLength()==0) {
				output+=(">\n");
			}
			else {
				output+=(" ");
				for(int i=0; i<attributes.getLength(); i++) {
					output+=(attributes.item(i).getNodeName()+"=\""+attributes.item(i).getNodeValue()+"\"");
					if(i!=attributes.getLength()-1) {
						output+=(" ");
					}
				}
				output+=(">\n");
			}
			return output;
			
		}
	}

}
