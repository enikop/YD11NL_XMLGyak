package hu.domparse.yd11nl;

import java.io.File;
import java.text.ParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DomWriteYd11nl {
	public static void main(String[] args) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            
            // Kutyaiskola DOM fa felépítése
            createDogschool(document);
            
            // Kiírás konzolra és fájlba a DomRead osztályt felhasználva
            File newXmlFile = new File("XMLYd11nl1.xml");
            StreamResult xmlToWrite = new StreamResult(newXmlFile);
            DomReadYd11nl.writeDocument(document, xmlToWrite);
            StreamResult console = new StreamResult(System.out);
            System.out.println("A felépített dokumentum:\n");
            DomReadYd11nl.writeDocument(document, console);
            
        } catch (ParserConfigurationException | TransformerException | DOMException | ParseException e) {
            e.printStackTrace();
        }
    }
	
	private static void createDogschool(Document document) throws DOMException, ParseException {
		// Gyökérelem felvétele
        Element root = document.createElement("kutyaiskola");
        document.appendChild(root);

        // Kutyák felvétele
        root.appendChild(document.createComment("Kutyák, akik be vannak regisztrálva"));
        root.appendChild(createDog(document, "1", "11", "1000", "Bizsu", "yorkshire terrier", "2022-10-10", "kan"));
        root.appendChild(createDog(document, "2", "11", "1002", "Buksi", "németjuhász", "2021-05-01", "kan"));
        root.appendChild(createDog(document, "3", "12", "1001", "Bella", "magyar vizsla", "2023-04-23", "szuka"));
        root.appendChild(createDog(document, "4", "13", "1003", "Mister", "afgán agár", "2016-09-15", "kan"));

        // Tagság felvétele
        root.appendChild(document.createComment("Kutyák tagság adatai"));
        root.appendChild(createMembership(document, "1000", "igen", "2023-03-01", "kedvezményes"));
        root.appendChild(createMembership(document, "1001", null, "2023-10-01", "normál"));
        root.appendChild(createMembership(document, "1002", null, "2022-02-28", "normál"));
        root.appendChild(createMembership(document, "1003", "nem", "2022-01-01", "kedvezményes"));
        
        //Gazdik felvétele
        root.appendChild(document.createComment("Gazdik adatai"));
        root.appendChild(createOwner(document, "11", "Kiss Katalin", "+36701234987", new String[]{"kisskat@gmail.com", "kati@kkconsulting.hu", "zoltan.kiss@gmail.com"}, "1980"));
        root.appendChild(createOwner(document, "12", "Kovács Béláné Folt Irén", "+36208877665", new String[]{"incike@citromail.hu"}, "1961"));
        root.appendChild(createOwner(document, "13", "Petőfi Mór", "+36506060768", new String[]{"borok.mortol@gmail.com", "talprabaradlay@gmail.com"}, "1972"));

        //Foglalkozások felvétele
        root.appendChild(document.createComment("Foglalkozások"));
        root.appendChild(createTraining(document, "100", "51", new String[]{"elsősegély oktatás", "kutyapszichológia előadás", "engedelmesség tréning"}, "gyakorlóaréna", "2023-10-01", "14", "18"));
        root.appendChild(createTraining(document, "101", "53", new String[]{"agility foglalkozás", "activity foglalkozás"}, "akadálypálya", "2023-10-08", "15", "16"));
        root.appendChild(createTraining(document, "102", "51", new String[]{"engedelmesség tréning"}, "akadálypálya", "2023-10-15", "15", "17"));
        root.appendChild(createTraining(document, "103", "51", new String[]{"engedelmesség tréning"}, "akadálypálya", "2023-11-02", "10", "12"));
        
        //Részvételi adatok felvétele
        root.appendChild(document.createComment("Kutya-foglalkozás kapcsoló elemek avagy részvételi adatok"));
        root.appendChild(createParticipation(document, "1", "100", "4", null));
        root.appendChild(createParticipation(document, "2", "100", "5", "Rágócsont"));
        root.appendChild(createParticipation(document, "2", "101", "5", "Purina száraz kutyatáp"));
        root.appendChild(createParticipation(document, "3", "101", "2", "Rágócsont"));
        root.appendChild(createParticipation(document, "1", "102", "5", "Kislabda"));
        root.appendChild(createParticipation(document, "2", "102", "5", "Játszókötél"));
        root.appendChild(createParticipation(document, "1", "103", "3", null));
        root.appendChild(createParticipation(document, "2", "103", "3", null));
        root.appendChild(createParticipation(document, "3", "103", "5", "Kislabda"));
        
        //Trénerek felvétele
        root.appendChild(document.createComment("Kutyatrénerek adatai"));
        root.appendChild(createTrainer(document, "51", "Horváth Zebulon", new String[]{"horvathz@ebiskola.hu"}, "06709186254", "kutyapszichológia", "3600"));
        root.appendChild(createTrainer(document, "52", "Horváth Indigó", new String[]{"horvathi@ebiskola.hu"}, "06704567821", null, "2800"));
        root.appendChild(createTrainer(document, "53", "Polgár Annamária", new String[]{"polgar.annam.2@gmail.com", "polgara@ebiskola.hu", "polgara@citromail.hu"}, "+36700670891", "agility", "3250"));

	}

	//Új kutya elem készítése
    private static Element createDog(Document document, String dogId, String ownerId, String membership, String name, String breed, String birthDate, String gender) throws ParseException {
        Element dogElement = document.createElement("kutya");
        dogElement.setAttribute("kkód", dogId);
        dogElement.setAttribute("gazdi", ownerId);
        dogElement.setAttribute("tagság", membership);

        dogElement.appendChild(createTextElement(document, "név", name));
        dogElement.appendChild(createTextElement(document, "fajta", breed));
        dogElement.appendChild(createTextElement(document, "szül_dátum", birthDate));
        dogElement.appendChild(createTextElement(document, "nem", gender));

        return dogElement;
    }
    
    //Új tagság elem készítése
    private static Element createMembership(Document document, String memberId, String isActive, String firstDay, String rate) throws ParseException {
        Element membershipElement = document.createElement("tagság");
        membershipElement.setAttribute("tagkód", memberId);
        if (isActive != null) {
            membershipElement.setAttribute("aktív", isActive);
        }

        membershipElement.appendChild(createTextElement(document, "kezdőnap", firstDay));
        membershipElement.appendChild(createTextElement(document, "tarifa", rate));

        return membershipElement;
    }

    //Új gazdi elem készítése
    private static Element createOwner(Document document, String ownerId, String name, String phone, String[] emails, String birthYear) {
        Element ownerElement = document.createElement("gazdi");
        ownerElement.setAttribute("gkód", ownerId);

        ownerElement.appendChild(createTextElement(document, "név", name));
        ownerElement.appendChild(createContact(document, phone, emails));
        ownerElement.appendChild(createTextElement(document, "szül_év", birthYear));

        return ownerElement;
    }

    //Új foglalkozás elem készítése
    private static Element createTraining(Document document, String trainingId, String trainerId, String[] themes, String place, String date, String begin, String end) throws ParseException {
        Element trainingElement = document.createElement("foglalkozás");
        trainingElement.setAttribute("fkód", trainingId);
        trainingElement.setAttribute("vezető", trainerId);

        for (String t : themes) {
            trainingElement.appendChild(createTextElement(document, "téma", t.trim()));
        }
        trainingElement.appendChild(createTextElement(document, "helyszín", place));
        Element idopontElement = document.createElement("időpont");
        idopontElement.appendChild(createTextElement(document, "dátum", date));
        idopontElement.appendChild(createTextElement(document, "kezdet", begin));
        idopontElement.appendChild(createTextElement(document, "vég", end));
        trainingElement.appendChild(idopontElement);

        return trainingElement;
    }

    //Új részvétel elem készítése
    private static Element createParticipation(Document document, String dogId, String trainingId, String evaluation, String prize) {
        Element participationElement = document.createElement("részvétel");
        participationElement.setAttribute("kutya", dogId);
        participationElement.setAttribute("foglalkozás", trainingId);

        participationElement.appendChild(createTextElement(document, "értékelés", evaluation));
        if (prize != null) {
            participationElement.appendChild(createTextElement(document, "jutalom", prize));
        }

        return participationElement;
    }

    //Új tréner elem készítése
    private static Element createTrainer(Document document, String trainerId, String name, String emails[], String telefon, String expertise, String wage) {
        Element trainerElement = document.createElement("tréner");
        trainerElement.setAttribute("tkód", trainerId);

        trainerElement.appendChild(createTextElement(document, "név", name));
        trainerElement.appendChild(createContact(document, telefon, emails));
        if(expertise != null) {
        	trainerElement.appendChild(createTextElement(document, "szakterület", expertise));
        }
        trainerElement.appendChild(createTextElement(document, "órabér", wage));

        return trainerElement;
    }

    //Új elérhetőség elem készítése
    private static Element createContact(Document document, String phone, String[] emails) {
        Element contactElement = document.createElement("elérhetőség");

        for (String email : emails) {
            contactElement.appendChild(createTextElement(document, "email", email));
        }
        contactElement.appendChild(createTextElement(document, "telefon", phone));

        return contactElement;
    }
    
    //Új szöveges elem készítése
    private static Element createTextElement(Document document, String tagName, String textContent) {
        Element element = document.createElement(tagName);
        element.appendChild(document.createTextNode(textContent));
        return element;
    }
}

