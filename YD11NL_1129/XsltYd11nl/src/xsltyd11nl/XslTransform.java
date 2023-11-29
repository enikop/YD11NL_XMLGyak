package xsltyd11nl;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XslTransform {


	    public static void main(String[] args) {
	        try {
	        	//1. feladat
	            String xmlInput = "hallgatoYD11NL.xml";
	            String xsltInputHTML = "hallgatoYD11NL.xsl";
	            String xsltInputXML = "hallgatoYD11NLxml.xsl";
	            String outputResult = "hallgatoYD11NL.html";
	            String outputResultXML = "hallgatoYD11NL.out.xml";

	            TransformerFactory transformerFactory = TransformerFactory.newInstance();

	            Transformer transformer = transformerFactory.newTransformer(new StreamSource(xsltInputHTML));
	            transformer.transform(new StreamSource(xmlInput), new StreamResult(outputResult));
	            
	            transformer = transformerFactory.newTransformer(new StreamSource(xsltInputXML));
	            transformer.transform(new StreamSource(xmlInput), new StreamResult(outputResultXML));
	            
	            //2. feladat
	            xmlInput = "orarendYD11NL.xml";
	            xsltInputHTML = "orarendYD11NL.xsl";
	            xsltInputXML = "orarendYD11NLxml.xsl";
	            outputResult = "orarendYD11NL.html";
	            outputResultXML = "orarendYD11NL.out.xml";

	            transformer = transformerFactory.newTransformer(new StreamSource(xsltInputHTML));
	            transformer.transform(new StreamSource(xmlInput), new StreamResult(outputResult));
	            
	            transformer = transformerFactory.newTransformer(new StreamSource(xsltInputXML));
	            transformer.transform(new StreamSource(xmlInput), new StreamResult(outputResultXML));

	            System.out.println("Sikeres XSLT transzformáció, eredmény mentve.");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
}
