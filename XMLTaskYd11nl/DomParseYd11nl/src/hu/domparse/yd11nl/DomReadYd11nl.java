package hu.domparse.yd11nl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DomReadYd11nl {
	public static void main(String[] args) {
		try {
			//Output XML file megadása
			File newXmlFile = new File("XMLReadYd11nl.xml");
			StreamResult newXmlStream = new StreamResult(newXmlFile);
			//XML dokumentum beolvasása
			Document document = parseXML("XMLYd11nl.xml");
			//Kiírás az output fájlba és a konzolra
			writeDocument(document, newXmlStream);
			System.out.println("A beolvasott dokumentum:\n\n"+formatXML(document));	
		} catch (IOException | SAXException | ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}
	}
	
	//Adott nevű XML dokumentumból Document készítése
	public static Document parseXML(String fileName) throws SAXException, IOException, ParserConfigurationException {
		File xmlFile = new File(fileName);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(xmlFile);
		Node root = document.getDocumentElement();
		root.normalize();
		cleanDocument(root);
		return document;
	}
	
	//Üres node-ok törlése a dokumentumból (sortörések miatti)
	private static void cleanDocument(Node root) {
		NodeList nodes = root.getChildNodes();
		List<Node> toDelete = new ArrayList<>();
		for(int i=0; i<nodes.getLength(); i++) {
			if(nodes.item(i).getNodeType()==Node.TEXT_NODE && nodes.item(i).getTextContent().strip().equals("")) {
				toDelete.add(nodes.item(i));
			}else {
				cleanDocument(nodes.item(i));
			}
		}
		for(Node node: toDelete) {
			root.removeChild(node);
		}
	}
	
	//DOM fa tartalmának kiírása megadott streambe transformerrel
	public static void writeDocument(Document document, StreamResult output) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		
		DOMSource source = new DOMSource(document);
		transformer.transform(source, output);
	}
	
	//DOM fa visszaadása XML-ben formázva
	public static String formatXML(Document document) {
		//Prolog kiírva kék színnel, utána vissza feketére
		String pi = "\u001B[34m<?xml version=\""+document.getXmlVersion()+"\" encoding=\""+document.getXmlEncoding()+"\" ?>\u001B[30m";
		return pi+formatElement(document.getDocumentElement(), 0);
	}
	
	//Adott XML csomópontnak és tartalmának strukturált Stringgé konvertálása formázott kiíráshoz
	public static String formatElement(Node node, int indent) {
		//Ha node nem elem, üres Stringgel térünk vissza
		if (node.getNodeType() != Node.ELEMENT_NODE) {
			return "";
		}
		//Egyébként felépítjük az xml elemet
		String output = "\n";
		output += indent(indent)+"<" + ((Element) node).getTagName();
		//Attribútumok formázott felvétele, ha vannak
		if (node.hasAttributes()) {
			for (int i = 0; i < node.getAttributes().getLength(); i++) {
				Node attribute = node.getAttributes().item(i);
				output += " " + attribute.getNodeName() + "=\"" + attribute.getNodeValue() + "\"";
			}
		}
		output += ">";
		//Gyerekelemek feldolgozása
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			//Szöveges tartalom
			if(children.item(i).getNodeType()==Node.TEXT_NODE) return output+=node.getTextContent()+"</" + ((Element) node).getTagName() + ">";
			//Gyerekelem
			if(children.item(i).getNodeType()==Node.ELEMENT_NODE)output+=formatElement(children.item(i), indent+1);
			//Komment (zöld színnel)
			if(children.item(i).getNodeType()==Node.COMMENT_NODE) output+="\n"+indent(indent+1)+"\u001B[32m<!--"+((Comment)children.item(i)).getData()+"-->\u001B[30m";
		}
		output+="\n"+indent(indent)+"</" + ((Element) node).getTagName() + ">";

		return output;
	}
	
	//Tabulálás
	private static String indent(int indent) {
		return "   ".repeat(indent);
	}

}
