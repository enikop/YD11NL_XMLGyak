package hu.domparse.yd11nl;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DomModifyYd11nl {
	public static void main(String[] args) {
		try {
			Document document = DomReadYd11nl.parseXML("XMLYd11nl.xml");
			
			//1. Az akadálypálya helyszín átírása I. pályára
			System.out.println("\u001B[31m1. módosítás\u001B[30m");
			modifyTrainingPlace(document, "akadálypálya", "I. pálya");
			//2. A Bizsu nevű kutya tagságának passzívra állítása.
			System.out.println("\n\u001B[31m2. módosítás\u001B[30m");
			setToPassive(document, "Bizsu");
			//3. Kovács Béláné Folt Irén átírása Folt Irénre és születési évének átírása 1959-re
			System.out.println("\n\u001B[31m3. módosítás\u001B[30m");
			modifyOwnerNameAndBirth(document, "Kovács Béláné Folt Irén", "Folt Irén", "1959");
			//4. A 3. tréner 2. email címének törlése.
			System.out.println("\n\u001B[31m4. módosítás\u001B[30m");
			deleteTrainerEmail(document, 3, 2);
			//5. Új foglalkozás felvétele automatikusan generált kóddal (max(fkód)+1)
			System.out.println("\n\u001B[31m5. módosítás\u001B[30m");
			insertTraining(document, "52", new String[] {"engedelmesség tréning", "kutyatulajdonos alapismeretek"}, "hátsó udvar", "2023-11-10", "17", "19");
			
			System.out.println("\n\u001B[31mA módosított dokumentum:\u001B[31m");
			System.out.println(DomReadYd11nl.formatXML(document));
			
		} catch (IOException | SAXException | ParserConfigurationException e) {
			System.out.println(e.getMessage());
		}
	}
	
	//Egy foglalkozás helyszín nevének átírása minden előfordulásnál
	private static void modifyTrainingPlace(Document document, String oldPlaceName, String newPlaceName) {
		//Az összes adott nevű helyszínnel rendelkező foglalkozás megkeresése
		Element root = document.getDocumentElement();
		NodeList trainings = root.getElementsByTagName("foglalkozás");
		System.out.println("ELŐTTE:");
		for(int i=0; i<trainings.getLength(); i++) {
			Element training = (Element)trainings.item(i);
			//Eredeti foglalkozások kiírása
			System.out.println(DomReadYd11nl.formatElement(training, 0).stripLeading());
			Element place = (Element)training.getElementsByTagName("helyszín").item(0);
			if(place.getTextContent().equals(oldPlaceName)) {
				//A helyszínek átírása
				place.setTextContent(newPlaceName);
				training.insertBefore(document.createComment("Átírt helyszín"), place);
			}
		}
		//A módosítás hatásának kiírása
		System.out.println("UTÁNA:");
		for(int i=0; i<trainings.getLength(); i++) {
			Element training = (Element)trainings.item(i);
			System.out.println(DomReadYd11nl.formatElement(training, 0).stripLeading());
		}
		
	}
	
	//Egy adott nevű kutya státuszának passziválása
	private static void setToPassive(Document document, String dogName) {
		//A dogName nevű kutya tagságának kódját lekérjük
		String membershipId = "";
		Element root = document.getDocumentElement();
		NodeList dogs = root.getElementsByTagName("kutya");
		for(int i=0; i<dogs.getLength(); i++) {
			Element dog = (Element)dogs.item(i);
			Element name = (Element)dog.getElementsByTagName("név").item(0);
			if(name.getTextContent().equals(dogName)) {
				membershipId = dog.getAttribute("tagság");
			}
		}
		//Az adott kódú tagságot passziváljuk és kiírjuk az előtte-utána összehasonlítást
		NodeList memberships = root.getElementsByTagName("tagság");
		for(int i=0; i<memberships.getLength(); i++) {
			Element membership = (Element)memberships.item(i);
			if(membership.getAttribute("tagkód").equals(membershipId)) {
				System.out.println("ELŐTTE:"+DomReadYd11nl.formatElement(membership, 0));
				membership.setAttribute("aktív", "nem");
				membership.appendChild(document.createComment("Passzivált kutya"));
				System.out.println("UTÁNA:"+DomReadYd11nl.formatElement(membership, 0));
			}
		}	
	}
	
	//Egy adott nevű gazdi nevének és születési évének megváltoztatása
	private static void modifyOwnerNameAndBirth(Document document, String oldName, String newName, String newBirthYear) {
		//Végigmegyünk az összes gazdin, akinek a neve oldName, azt newName-re változtatjuk
		Element root = document.getDocumentElement();
		NodeList owners = root.getElementsByTagName("gazdi");
		for(int i=0; i<owners.getLength(); i++) {
			Element owner = (Element)owners.item(i);
			Element name = (Element)owner.getElementsByTagName("név").item(0);
			if(name.getTextContent().equals(oldName)) {
				//A név és a születési év átírása és előtte-utána adatok kiírása
				System.out.println("ELŐTTE:"+DomReadYd11nl.formatElement(owner, 0));
				owner.insertBefore(document.createComment("Átírt név és születési év"), name);
				name.setTextContent(newName);
				Element birthYear = (Element)owner.getElementsByTagName("szül_év").item(0);
				birthYear.setTextContent(newBirthYear);
				System.out.println("UTÁNA:"+DomReadYd11nl.formatElement(owner, 0));
			}
		}
	}

	//Egy adott sorszámú tréner adott sorszámú email címének törlése, ha létezik
	private static void deleteTrainerEmail(Document document, int trainerNum, int emailNum) {
		//Az adott sorszámú tréner megkeresése
		Element root = document.getDocumentElement();
		NodeList trainers = root.getElementsByTagName("tréner");
		if(trainers.getLength()>= trainerNum) {
			Element trainer = (Element)trainers.item(trainerNum-1);
			//Tréner kiírása változtatás előtt
			System.out.println("ELŐTTE:"+DomReadYd11nl.formatElement(trainer, 0));
			//Adott sorszámú email megkeresése
			Element contact = (Element)trainer.getElementsByTagName("elérhetőség").item(0);
			NodeList emails = contact.getElementsByTagName("email");
			if(emails.getLength()>= emailNum) {
				Element email = (Element)emails.item(emailNum-1);
				//Email törlése
				contact.insertBefore(document.createComment("Törölt email helye"), email);
				contact.removeChild(email);
			}
			//Tréner kiírása változtatás után
			System.out.println("UTÁNA:"+DomReadYd11nl.formatElement(trainer, 0));
		}
	}

	//Foglalkozás felvétele adott adatokkal, automatikusan generált fkód értékkel
	private static void insertTraining(Document document, String trainerId, String[] themes, String place, String date, String begin, String end) {
		//Új foglalkozás kódjának kiszámítása
		int maxCode = 1;
		Node next = null;
		Element root = document.getDocumentElement();
		NodeList trainings = root.getElementsByTagName("foglalkozás");
		for(int i=0; i<trainings.getLength(); i++) {
			Element training = (Element)trainings.item(i);
			int id = Integer.parseInt(training.getAttribute("fkód").trim());
			if(id>maxCode) {
				maxCode = id;
			}
			//A ciklus végére a nextben az utolsó foglalkozásra rákövetkező elem lesz
			next = training.getNextSibling();
			
		}
		String trainingId = Integer.toString(maxCode + 1);		
		//Új foglalkozás létrehozása
		Element newTraining = document.createElement("foglalkozás");
		newTraining.setAttribute("fkód", trainingId);
        newTraining.setAttribute("vezető", trainerId);
        for (String t : themes) {
            newTraining.appendChild(createTextElement(document, "téma", t.trim()));
        }
        newTraining.appendChild(createTextElement(document, "helyszín", place));
        Element idopontElement = document.createElement("időpont");
        idopontElement.appendChild(createTextElement(document, "dátum", date));
        idopontElement.appendChild(createTextElement(document, "kezdet", begin));
        idopontElement.appendChild(createTextElement(document, "vég", end));
        newTraining.appendChild(idopontElement);
        //Beillesztés a foglalkozások közé az utolsó helyre, a next elé
        root.insertBefore(document.createComment("Új foglalkozás"), next);
        root.insertBefore(newTraining, next);
        //Az új foglalkozás adatainak kiírása
        System.out.println("ÚJ FOGLALKOZÁS:"+DomReadYd11nl.formatElement(newTraining, 0));
	}

	//Text node készítése
	private static Element createTextElement(Document document, String tagName, String textContent) {
	        Element element = document.createElement(tagName);
	        element.appendChild(document.createTextNode(textContent));
	        return element;
	}
}
