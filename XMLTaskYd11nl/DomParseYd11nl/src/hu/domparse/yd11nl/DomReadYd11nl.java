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
			//Kiírás az output fájlba
			writeDocument(document, newXmlStream);
			//Kiírás konzolra általános XML kiíróval
			System.out.println("A beolvasott dokumentum:\n\n"+formatXML(document));
			//Kiírás konzolra feladatspecifikus kiíróval
			//System.out.println("A beolvasott dokumentum:\n\n"+getStructuredDocument(document));
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
	
	//Kutyaiskola dokumentum strukturált tartalának visszaadása (feladat specifikus beolvasó)
	public static String getStructuredDocument(Document document) {
		//PI
		String output = "<?xml version=\""+document.getXmlVersion()+"\" encoding=\""+document.getXmlEncoding()+"\"?>\n";
		//Gyökérelem és gyerekelemei
		output += "<"+document.getDocumentElement().getNodeName()+">\n";
		output += getStructuredDogs(document, 1);
		output += getStructuredMemberships(document, 1);
		output += getStructuredOwners(document, 1);
		output += getStructuredTrainings(document, 1);
		output += getStructuredParticipations(document, 1);
		output += getStructuredTrainers(document, 1);
		output += "\n</"+document.getDocumentElement().getNodeName()+">";
		
		return output;
	}
	
	//Strukturált kutya elemek
	private static String getStructuredDogs(Document document, int indent) {
		String output = "";
		//Végigiterálunk az összes kutyán
		NodeList dogNodes = document.getElementsByTagName("kutya");
		for(int i=0; i<dogNodes.getLength(); i++) {
			Element dog = (Element) dogNodes.item(i);
			//Ha van az elem előtt comment, kiírjuk
			output += getComment(dog.getPreviousSibling(), indent);
			//Attribútumok
			String id = dog.getAttribute("kkód");
			String owner = dog.getAttribute("gazdi");
			String membership = dog.getAttribute("tagság");
			//Gyerekelemek
			Element name = (Element) dog.getElementsByTagName("név").item(0);
			Element breed = (Element) dog.getElementsByTagName("fajta").item(0);
			Element birthDate = (Element) dog.getElementsByTagName("szül_dátum").item(0);
			Element gender = (Element) dog.getElementsByTagName("nem").item(0);
			//Strukturált Stringbe írás
			output += indent(indent)+"<kutya kkód=\""+id+"\" gazdi=\""+owner+"\" tagság=\""+membership+"\">\n";
			indent++;
			output += indent(indent)+getStructuredSimpleElement("név", name.getTextContent());
			output += indent(indent)+getStructuredSimpleElement("fajta", breed.getTextContent());
			output += indent(indent)+getStructuredSimpleElement("szül_dátum", birthDate.getTextContent());
			output += indent(indent)+getStructuredSimpleElement("nem", gender.getTextContent());
			indent--;
			output += indent(indent)+"</kutya>\n";
		}
		return output;
	}
	
	//Strukturált tagság elemek
	private static String getStructuredMemberships(Document document, int indent) {
		String output = "";
		NodeList membershipNodes = document.getElementsByTagName("tagság");
		for(int i=0; i<membershipNodes.getLength(); i++) {
			Element membership = (Element) membershipNodes.item(i);
			//Ha van az elem előtt comment, kiírjuk
			output += getComment(membership.getPreviousSibling(), indent);
			//Attribútumok
			String id = membership.getAttribute("tagkód");
			String active = membership.getAttribute("aktív");
			//Ha van aktív attribútum, kiírjuk, egyébként üres String helyette
			String activity = active.isEmpty() ? "" :" aktív=\""+active+"\"";
			//Gyerekelemek
			Element firstDay = (Element) membership.getElementsByTagName("kezdőnap").item(0);
			Element rate = (Element) membership.getElementsByTagName("tarifa").item(0);
			//Strukturált Stringbe írás
			output += indent(indent)+"<tagság tagkód=\""+id+activity+">\n";
			indent++;
			output += indent(indent)+getStructuredSimpleElement("kezdőnap", firstDay.getTextContent());
			output += indent(indent)+getStructuredSimpleElement("tarifa", rate.getTextContent());
			indent--;
			output += indent(indent)+"</tagság>\n";
		}
		return output;
	}
	
	//Strukturált gazdi elemek
	private static String getStructuredOwners(Document document, int indent) {
		String output = "";
		NodeList ownerNodes = document.getElementsByTagName("gazdi");
		for(int i=0; i<ownerNodes.getLength(); i++) {
			Element owner = (Element) ownerNodes.item(i);
			//Ha van az elem előtt comment, kiírjuk
			output += getComment(owner.getPreviousSibling(), indent);
			//Attribútumok
			String id = owner.getAttribute("gkód");
			//Gyerekelemek
			Element name = (Element) owner.getElementsByTagName("név").item(0);
			Element contact = (Element) owner.getElementsByTagName("elérhetőség").item(0);
			Element birthYear = (Element) owner.getElementsByTagName("szül_év").item(0);
			//Strukturált Stringbe írás
			output += indent(indent)+"<gazdi gkód=\""+id+"\">\n";
			indent++;
			output += indent(indent)+getStructuredSimpleElement("név", name.getTextContent());
			output += getStructuredContact(contact, indent);
			output += indent(indent)+getStructuredSimpleElement("szül_év", birthYear.getTextContent());
			indent--;
			output += indent(indent)+"</gazdi>\n";
		}
		return output;
	}
	
	//Strukturált foglalkozás elemek
	private static String getStructuredTrainings(Document document, int indent) {
		String output = "";
		NodeList trainingNodes = document.getElementsByTagName("foglalkozás");
		for(int i=0; i<trainingNodes.getLength(); i++) {
			Element training = (Element) trainingNodes.item(i);
			//Ha van az elem előtt comment, kiírjuk
			output += getComment(training.getPreviousSibling(), indent);
			//Attribútumok
			String id = training.getAttribute("fkód");
			String trainer = training.getAttribute("vezető");
			//Gyerekelemek
			NodeList topics = training.getElementsByTagName("téma");
			Element place = (Element) training.getElementsByTagName("helyszín").item(0);
			Element time = (Element) training.getElementsByTagName("időpont").item(0);
			Element day = (Element) time.getElementsByTagName("dátum").item(0);
			Element begin = (Element) time.getElementsByTagName("kezdet").item(0);
			Element end = (Element) time.getElementsByTagName("vég").item(0);
			//Strukturált Stringbe írás
			output += indent(indent)+"<foglalkozás fkód=\""+id+"\" vezető=\""+trainer+"\">\n";
			indent++;
			//A téma többszörös elem, mindegyik előfordulását kiírjuk
			for(int j=0; j<topics.getLength(); j++) {
				output += indent(indent)+getStructuredSimpleElement("téma", ((Element)topics.item(j)).getTextContent());
			}
			output += indent(indent)+getStructuredSimpleElement("helyszín", place.getTextContent());
			//Az időpont gyerekelem kiírása elemtartalmával együtt
			output += indent(indent)+"<időpont>\n";
			indent++;
			output += indent(indent)+getStructuredSimpleElement("dátum", day.getTextContent());
			output += indent(indent)+getStructuredSimpleElement("kezdet", begin.getTextContent());
			output += indent(indent)+getStructuredSimpleElement("vég", end.getTextContent());
			indent--;
			output += indent(indent)+"</időpont>\n";
			indent--;
			output += indent(indent)+"</foglalkozás>\n";
		}
		return output;
	}

	//Strukturált részvétel elemek
	private static String getStructuredParticipations(Document document, int indent) {
		String output = "";
		NodeList participationNodes = document.getElementsByTagName("részvétel");
		for(int i=0; i<participationNodes.getLength(); i++) {
			Element participation = (Element) participationNodes.item(i);
			//Ha van az elem előtt comment, kiírjuk
			output += getComment(participation.getPreviousSibling(), indent);
			//Attribútumok
			String dog = participation.getAttribute("kutya");
			String training = participation.getAttribute("foglalkozás");
			//Gyerekelemek
			Element evaluation = (Element) participation.getElementsByTagName("értékelés").item(0);
			NodeList prize = participation.getElementsByTagName("jutalom");
			//Strukturált Stringbe írás
			output += indent(indent)+"<részvétel kutya=\""+dog+"\" foglalkozás=\""+training+"\">\n";
			indent++;
			output += indent(indent)+getStructuredSimpleElement("értékelés", evaluation.getTextContent());
			//A jutalom elem opcionális, csak akkor írjuk ki, ha létezik
			output += prize.getLength()==0 ? "" : indent(indent)+getStructuredSimpleElement("jutalom", ((Element)prize.item(0)).getTextContent());
			indent--;
			output += indent(indent)+"</részvétel>\n";
		}
		return output;
	}
	
	//Strukturált tréner elemek
	private static String getStructuredTrainers(Document document, int indent) {
		String output = "";
		NodeList trainerNodes = document.getElementsByTagName("tréner");
		for(int i=0; i<trainerNodes.getLength(); i++) {
			Element trainer = (Element) trainerNodes.item(i);
			//Ha van az elem előtt comment, kiírjuk
			output += getComment(trainer.getPreviousSibling(), indent);
			//Attribútumok
			String id = trainer.getAttribute("tkód");
			//Gyerekelemek
			Element name = (Element) trainer.getElementsByTagName("név").item(0);
			Element contact = (Element) trainer.getElementsByTagName("elérhetőség").item(0);
			NodeList expertise = trainer.getElementsByTagName("szakterület");
			Element wage = (Element) trainer.getElementsByTagName("órabér").item(0);
			//Strukturált Stringbe írás
			output += indent(indent)+"<tréner tkód=\""+id+"\">\n";
			indent++;
			output += indent(indent)+getStructuredSimpleElement("név", name.getTextContent());
			output += getStructuredContact(contact, indent);
			//A szakterület elem opcionális, csak akkor írjuk ki, ha létezik
			output += expertise.getLength() == 0 ? "" : indent(indent)+getStructuredSimpleElement("szakterület", expertise.item(0).getTextContent());
			output += indent(indent)+getStructuredSimpleElement("órabér", wage.getTextContent());
			indent--;
			output += indent(indent)+"</tréner>\n";
		}
		return output;
	}
	
	//Strukturált elérhetőség elem
	private static String getStructuredContact(Element contact, int indent) {
		String output = "";
		//Gyerekelemek
		NodeList emails = contact.getElementsByTagName("email");
		Element phone = (Element)contact.getElementsByTagName("telefon").item(0);
		//Strukturált Stringbe írás
		output += indent(indent)+"<elérhetőség>\n";
		indent++;
		//Az email többszörös elem, minden előfordulását kiírjuk
		for(int i=0; i<emails.getLength(); i++) {
			output+=indent(indent)+getStructuredSimpleElement("email", ((Element)emails.item(i)).getTextContent());
		}
		output += indent(indent)+getStructuredSimpleElement("telefon", phone.getTextContent());
		indent--;
		output += indent(indent)+"</elérhetőség>\n";
		return output;
	}

	//Szöveges tartalmú elem visszaadása strukturált formában
	private static String getStructuredSimpleElement(String tag, String content) {
		return "<"+tag+">"+content+"</"+tag+">\n";
	}
	
	//Komment csomópont Stringbe írása strukturáltan
	private static String getComment(Node examinedNode, int indent) {
		String output = "";
		if(examinedNode.getNodeType()==Node.COMMENT_NODE) {
			output="\n"+indent(indent)+"<!--"+((Comment)examinedNode).getData()+"-->\n";
		}
		return output;
	}
	
	//DOM fa visszaadása XML-ben formázva (általános XML beolvasó, színezett PI és kommentek)
	public static String formatXML(Document document) {
		//Prolog kiírva kék színnel, utána vissza feketére
		String pi = "\u001B[34m<?xml version=\""+document.getXmlVersion()+"\" encoding=\""+document.getXmlEncoding()+"\" ?>\u001B[30m";
		return pi+formatElement(document.getDocumentElement(), 0);
	}
	
	//Adott XML csomópontnak és tartalmának strukturált Stringgé konvertálása formázott kiíráshoz (általános XML beolvasó)
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
