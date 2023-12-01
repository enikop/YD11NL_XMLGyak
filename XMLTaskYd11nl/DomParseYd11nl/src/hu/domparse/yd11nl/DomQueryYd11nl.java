package hu.domparse.yd11nl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DomQueryYd11nl {
	public static void main(String[] args) {
		try {
			Document document = DomReadYd11nl.parseXML("XMLYd11nl.xml");
			
			// 1. A 2022-ben vagy utána született kutyák adatai
			String dogData = getDogNamesBornAfter(document, 2022);
			System.out.println("\u001B[31m1. lekérdezés:\u001B[30m");
			System.out.println("A 2022-ben vagy utána született kutyák adatai: " + dogData);
			
			// 2. Az aktív tagságok száma
			System.out.println("\n\u001B[31m2. lekérdezés:\u001B[30m");
			System.out.println("Az aktív tagok száma: " + countActiveMembers(document));
			
			// 3. A trénerek órabéreinek átlaga
			System.out.println("\n\u001B[31m3. lekérdezés:\u001B[30m");
			System.out.println("A trénerek átlagos órabére: " + getAvgTrainerWage(document) + " Ft/óra");
			
			// 4. Az összes kiosztott jutalom típusa
			List<String> prizes = getAllPrizes(document);
			System.out.println("\n\u001B[31m4. lekérdezés:\u001B[30m");
			System.out.println("A kiosztott jutalmak típusai: "+prizes);
			
			// 5. Annak a foglalkozásnak (Azoknak a foglalkozásoknak) az adatai, amin a legtöbben vettek részt
			System.out.println("\n\u001B[31m5. lekérdezés:\u001B[30m");
			System.out.println("Azoknak a foglalkozásoknak az adatai, melyeken a legtöbben vettek részt:");
			System.out.println(getMostPopularTrainings(document));
			
			//6. Az adott nevű gazdi összes óradíj tartozása (normál tarifa = 3500 Ft, kedvezményes tarifa = 2000 Ft/óra)
			System.out.println("\n\u001B[31m6. lekérdezés:\u001B[30m");
			System.out.println("Kiss Katalin összes tartozása: "+getDebt(document, "Kiss Katalin")+" Ft");
	

		} catch (IOException | SAXException | ParserConfigurationException e) {
			System.out.println(e.getMessage());
		}
	}

	//Adott évet követően született kutyák adatai strukturáltan
	private static String getDogNamesBornAfter(Document document, int year) {
		String output = "";
		Element root = document.getDocumentElement();
		//Ciklus az összes kutyára
		NodeList dogs = root.getElementsByTagName("kutya");
		for (int i = 0; i < dogs.getLength(); i++) {
			Element dog = (Element) dogs.item(i);
			Element birthDateEl = (Element) dog.getElementsByTagName("szül_dátum").item(0);
			//A születési év kivétele a kutya születési dátumából
			int birthYear = Integer.parseInt(birthDateEl.getTextContent().split("-")[0].trim());
			//Ha a születési évnél nem nagyobb a paraméterként megadott év, akkor a kutya adatai a kimenetbe kerülnek
			if (birthYear >= year) {
				output+=DomReadYd11nl.formatElement(dog, 0);
			}
		}
		return output;
	}
	
	//Aktív tagságú kutyák száma
	private static int countActiveMembers(Document document) {
		int counter = 0;
		Element root = document.getDocumentElement();
		// Ciklus az összes tagságra
		NodeList memberships = root.getElementsByTagName("tagság");
		for (int i = 0; i < memberships.getLength(); i++) {
			Element membership = (Element) memberships.item(i);
			// Ha az aktív attribútum értéke nem nem, a számláló növelése
			// (üres is lehet, alapértelmezetten igen)
			if (!membership.getAttribute("aktív").equals("nem")) {
				counter++;
			}
		}
		return counter;
	}
	
	//A trénerek átlagfizetése Ft/órában megadva
	private static int getAvgTrainerWage(Document document) {
		int trainerCount = 0;
		int wageSum = 0;
		Element root = document.getDocumentElement();
		NodeList trainers = root.getElementsByTagName("tréner");
		// Trénerek számának meghatározása
		trainerCount = trainers.getLength();
		// Ciklus az összes trénerre
		for (int i = 0; i < trainers.getLength(); i++) {
			Element trainer = (Element) trainers.item(i);
			//Tréner órabérének hozzáadása az összeghez
			Element wage = (Element) trainer.getElementsByTagName("órabér").item(0);
			wageSum += Integer.parseInt(wage.getTextContent().trim());
		}
		//Átlag számítás, felfelé kerekítés egészre
		return (int) Math.ceil(wageSum / (1.0 * trainerCount));
	}
	
	//Az összes jutalom típus kilistázása
	private static List<String> getAllPrizes(Document document) {
		List<String> allPrizes = new ArrayList<>();
		Element root = document.getDocumentElement();
		NodeList participations = root.getElementsByTagName("részvétel");
		//Ciklus az összes részvételre
		for (int i = 0; i < participations.getLength(); i++) {
			Element participation = (Element) participations.item(i);
			//Ha létezik jutalom gyerekelem, és a tartalma még nincs a listában, akkor felvétel
			NodeList prizes = participation.getElementsByTagName("jutalom");
			if (prizes.getLength()>0) {
				Element prize = (Element) prizes.item(0);
				if(!allPrizes.contains(prize.getTextContent())) {
					allPrizes.add(prize.getTextContent());
				}
			}
		}
		return allPrizes;
	}
	
	//A legtöbb kutya részvételével lezajlott foglalkozások formázott kiírása
	private static String getMostPopularTrainings(Document document) {
		String output = "";
		Element root = document.getDocumentElement();
		NodeList trainings = root.getElementsByTagName("foglalkozás");
		// Megkeresni a részvételszám maximumát
		int max = 0;
		for (int i = 0; i < trainings.getLength(); i++) {
			Element training = (Element) trainings.item(i);
			int participationCount = getNumberOfParticipants(document, training.getAttribute("fkód"));
			if (participationCount > max) {
				max = participationCount;
			}
		}
		output += "A legtöbb résztvevő: " + max + "db.";
		// Megkeresni azokat a foglalkozásokat, amikre a maximummal egyenlő volt a részvételszám
		for (int i = 0; i < trainings.getLength(); i++) {
			Element training = (Element) trainings.item(i);
			int participationCount = getNumberOfParticipants(document, training.getAttribute("fkód"));
			if (participationCount == max) {
				//Felvenni a foglalkozást formázva a kimeneti stringbe
				output += DomReadYd11nl.formatElement(training, 0);
			}
		}
		return output;
	}
	
	//Az adott foglalkozáson résztvevő kutyák száma
	private static int getNumberOfParticipants(Document document, String trainingId) {
		int counter = 0;
		Element root = document.getDocumentElement();
		NodeList participations = root.getElementsByTagName("részvétel");
		// Ciklus az összes részvételre
		for (int i = 0; i < participations.getLength(); i++) {
			Element participation = (Element) participations.item(i);
			// Ha a megadott foglalkozásra vonatkozik a részvétel, a számláló növelése
			if (participation.getAttribute("foglalkozás").equals(trainingId)) {
				counter++;
			}
		}
		return counter;
	}
	
	//Adott nevű gazdi tartozásának kiszámítása
	private static Integer getDebt(Document document, String name) {
		int DISCOUNT_RATE = 2000;
		int NORMAL_RATE = 3500;
		int debt = 0;
		//Gazdi azonosító lekérése
		String ownerId = getOwnerId(document, name);
		if(ownerId == null) return null;
		//Ciklus a gazdi kutyáira
		List<String> dogIds = getOwnedDogIds(document, ownerId);
		for(String dogId: dogIds) {
			//Tartozás növelése részvételi_órák*tarifa értékkel
			int rate = (isDiscounted(document, dogId) ? DISCOUNT_RATE : NORMAL_RATE);
			debt += getTotalParticipationHours(document, dogId)*rate;
		}
		return debt;
	}
	
	//Adott nevű gazdi azonosítója
	private static String getOwnerId(Document document, String name) {
		Element root = document.getDocumentElement();
		//Ciklus az összes gazdira
		NodeList owners = root.getElementsByTagName("gazdi");
		for (int i = 0; i < owners.getLength(); i++) {
			Element owner = (Element)owners.item(i);
			//Ha a név a paraméterben megadott, akkor gkód visszaadása
			Element nameElement = (Element)owner.getElementsByTagName("név").item(0);
			if(name.equals(nameElement.getTextContent())) {
				return owner.getAttribute("gkód");
			}
		}
		return null;
	}
	
	//Adott azonosítójú gazdi kutyáinak azonosítói
	private static List<String> getOwnedDogIds(Document document, String ownerId) {
		List<String> dogIds = new ArrayList<>();
		Element root = document.getDocumentElement();
		//Ciklus az összes kutyára
		NodeList dogs = root.getElementsByTagName("kutya");
		for (int i = 0; i < dogs.getLength(); i++) {
			Element dog = (Element)dogs.item(i);
			//Ha a gazdi kódja a paraméterben megadott, akkor kkód felvétele a kimeneti listába
			if(dog.getAttribute("gazdi").equals(ownerId)) {
				dogIds.add(dog.getAttribute("kkód"));
			}
		}
		return dogIds;
	}
	
	//Egy adott azonosítójú kutyára kedvezményes tarifa vonatkozik-e
	private static Boolean isDiscounted(Document document, String dogId) {
		String membershipId = null;
		Element root = document.getDocumentElement();
		//Adott kutyához tartozó tagság id megkeresése
		NodeList dogs = root.getElementsByTagName("kutya");
		for (int i = 0; i < dogs.getLength(); i++) {
			Element dog = (Element)dogs.item(i);
			if(dog.getAttribute("kkód").equals(dogId)) {
				membershipId = dog.getAttribute("tagság");
				break;
			}
		}
		//Ciklus az összes tagságra
		NodeList memberships = root.getElementsByTagName("tagság");
		for (int i = 0; i < memberships.getLength(); i++) {
			Element membership = (Element)memberships.item(i);
			if(membership.getAttribute("tagkód").equals(membershipId)) {
				//A tarifa gyerekelem tartalmától függően visszatérés igazzal vagy hamissal
				String discount = membership.getElementsByTagName("tarifa").item(0).getTextContent();
				if(discount.equals("normál")) return false;
				else return true;
			}
		}
		return null;
	}

	//Egy adott kutya hány órányi foglalkozáson vett részt eddig összesen
	private static int getTotalParticipationHours(Document document, String dogId) {
		int hours = 0;
		List<String> trainingIds = new ArrayList<>();
		Element root = document.getDocumentElement();
		//Ciklus az összes részvételre, érintett foglalkozás kódok listázása
		NodeList participations = root.getElementsByTagName("részvétel");
		for (int i = 0; i < participations.getLength(); i++) {
			Element participation = (Element) participations.item(i);
			if (participation.getAttribute("kutya").equals(dogId)) {
				trainingIds.add(participation.getAttribute("foglalkozás"));
			}
		}
		//Ciklus az összes foglalkozásra
		NodeList trainings = root.getElementsByTagName("foglalkozás");
		for (int i = 0; i < trainings.getLength(); i++) {
			Element training = (Element) trainings.item(i);
			if (trainingIds.contains(training.getAttribute("fkód"))) {
				Element time = (Element) training.getElementsByTagName("időpont").item(0);
				Element from = (Element) time.getElementsByTagName("kezdet").item(0);
				Element to = (Element) time.getElementsByTagName("vég").item(0);
				//A foglalkozás hosszának hozzáadása a teljes óraszámhoz, ha a kutya részt vett
				hours += Integer.parseInt(to.getTextContent())-Integer.parseInt(from.getTextContent());
			}
		}
		return hours;
	}
}
