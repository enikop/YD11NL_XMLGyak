package SaxYD11NL;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XsdYD11NL {
	public static void main(String[] args) {
		try {			
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(new File("YD11NL_kurzusfelvetel.xsd"));
			SAXParserFactory saxFactory = SAXParserFactory.newInstance();
			saxFactory.setSchema(schema);
			SAXParser parser = saxFactory.newSAXParser();
			parser.parse("YD11NL_kurzusfelvetel.xml", new ValidationHandler());
			System.out.println("XSD Validation successful");

		}catch(SAXParseException parseExc) {
			System.out.println("Unsuccessful validation: "+parseExc.getMessage());
		}catch (SAXException | IOException | ParserConfigurationException e ) {
			e.printStackTrace();
		}
	}
	private static class ValidationHandler extends DefaultHandler{
		@Override
		public void error(SAXParseException e) throws SAXException {
			throw e;
		}
	}
}
