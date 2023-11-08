package domyd11nl1108;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DomWriteYd11nl {

	public static void main(String[] args) {
		try {
			File xmlFileInput = new File("orarendYd11nl.xml");
			File xmlFileOutput = new File("orarend1Yd11nl.xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(xmlFileInput);
			DomReadYd11nl.printDocument(document);
			writeDocumentToFile(document, xmlFileOutput);
		} catch (IOException | SAXException | ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}
	}
	public static void writeDocumentToFile(Document document, File output) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		DOMSource source = new DOMSource(document);
		StreamResult outFile = new StreamResult(output);
		transformer.transform(source, outFile);
	}

}
