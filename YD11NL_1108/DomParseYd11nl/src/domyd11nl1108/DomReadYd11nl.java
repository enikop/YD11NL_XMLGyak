package domyd11nl1108;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DomReadYd11nl {
	
	public static void main(String[] args) {
		try {
			File xmlFile = new File("orarendYd11nl.xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(xmlFile);
			printDocument(document);
		} catch (IOException | SAXException | ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public static void printDocument(Document document) {
		String indentStr = "   ";
		int indent = 0;
		Element root = document.getDocumentElement();
		System.out.println(root.getNodeName());
		indent++;
		NodeList lessons = root.getElementsByTagName("ora");
		for(int i=0; i<lessons.getLength(); i++) {
			Element lesson = (Element)lessons.item(i);
			System.out.print(indentStr.repeat(indent)+"ora");
			NamedNodeMap attributes = lesson.getAttributes();
			printAttributes(attributes);
			
			Element subject = (Element) lesson.getElementsByTagName("targy").item(0);
			indent++;
			System.out.println(indentStr.repeat(indent)+"tantargy");
			indent++;
			System.out.println(indentStr.repeat(indent)+subject.getTextContent());
			indent--;
			System.out.println(indentStr.repeat(indent)+"tantargy");
			
			Element time = (Element) lesson.getElementsByTagName("idopont").item(0);
			printTime(time, indent, indentStr);
			
			Element place = (Element) lesson.getElementsByTagName("helyszin").item(0);
			System.out.println(indentStr.repeat(indent)+"helyszin");
			indent++;
			System.out.println(indentStr.repeat(indent)+place.getTextContent());
			indent--;
			System.out.println(indentStr.repeat(indent)+"helyszin");
			Element teacher = (Element) lesson.getElementsByTagName("oktato").item(0);
			System.out.println(indentStr.repeat(indent)+"oktato");
			indent++;
			System.out.println(indentStr.repeat(indent)+teacher.getTextContent());
			indent--;
			System.out.println(indentStr.repeat(indent)+"oktato");
			Element major = (Element) lesson.getElementsByTagName("szak").item(0);
			System.out.println(indentStr.repeat(indent)+"szak");
			indent++;
			System.out.println(indentStr.repeat(indent)+major.getTextContent());
			indent--;
			System.out.println(indentStr.repeat(indent)+"szak");
			indent--;
			System.out.println(indentStr.repeat(indent)+"ora");
			
		}
		System.out.println(root.getNodeName());
	}
	private static void printTime(Element time, int indent, String indentStr) {
		System.out.println(indentStr.repeat(indent)+"idopont");
		Element day = (Element) time.getElementsByTagName("nap").item(0);
		Element from = (Element) time.getElementsByTagName("tol").item(0);
		Element to = (Element) time.getElementsByTagName("ig").item(0);
		indent++;
		System.out.println(indentStr.repeat(indent)+"nap");
		indent++;
		System.out.println(indentStr.repeat(indent)+day.getTextContent());
		indent--;
		System.out.println(indentStr.repeat(indent)+"nap");
		System.out.println(indentStr.repeat(indent)+"tol");
		indent++;
		System.out.println(indentStr.repeat(indent)+from.getTextContent());
		indent--;
		System.out.println(indentStr.repeat(indent)+"tol");
		System.out.println(indentStr.repeat(indent)+"ig");
		indent++;
		System.out.println(indentStr.repeat(indent)+to.getTextContent());
		indent--;
		System.out.println(indentStr.repeat(indent)+"ig");
		indent--;
		System.out.println(indentStr.repeat(indent)+"idopont");
	}
	private static void printAttributes(NamedNodeMap attributes) {
		if(attributes.getLength()==0) {
			System.out.println();
		}
		else {
			System.out.print(" {");
			for(int i=0; i<attributes.getLength(); i++) {
				System.out.print(attributes.item(i).getNodeName()+"="+attributes.item(i).getNodeValue());
				if(i!=attributes.getLength()-1) {
					System.out.print(", ");
				}
			}
			System.out.println("}");
		}
		
	}

}