package couchdb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

public class CouchArtikel13 {

	public static void main(String args[]) throws IOException {
		String format = "%-5s%-15s%-15s%-5s%n";

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		HttpClient httpClient = new StdHttpClient.Builder().url("http://feuerbach.nt.fh-koeln.de:5984").build();

		CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
		// CouchDbConnector db = dbInstance.createConnector("artikel13", true);
		CouchDbConnector db = new StdCouchDbConnector("artikel13", dbInstance);

		db.createDatabaseIfNotExists();

		ArtikelRepository repo = new ArtikelRepository(db);

		int choise;
		do {

			System.out.println("=================================================");
			System.out.println("=        1. ARTIKEL.csv einfügen                =");
			System.out.println("=        2. Alle Artikel aus CouchDB lesen      =");
			System.out.println("=        3. Artikel nach ID lesen               =");
			System.out.println("=        4. Artikel verändern                   =");
			System.out.println("=        5. Artikel löschen                     =");
			System.out.println("=        0. Exit                                =");
			System.out.println("=================================================");

			try {
				choise = Integer.parseInt(reader.readLine());
			} catch (NumberFormatException ex) {
				choise = -1;
			}
			System.out.println("\n");

			switch (choise) {
			case 1: {
				BufferedReader fileReader = new BufferedReader(new FileReader("ARTIKEL.csv"));
				String line;
				while ((line = fileReader.readLine()) != null && !line.equals("")) {
					Artikel artikel = new Artikel();
					String val[] = line.split(";");
					artikel.setArtnr(Integer.parseInt(val[0]));
					artikel.setArtbez(val[1]);
					artikel.setMge(val[2]);
					artikel.setPreis(Double.parseDouble(val[3]));

					for (int i = 4; i < val.length; i += 2) {
						BPos bPos = new BPos();
						bPos.setBestnr(Integer.parseInt(val[i]));
						bPos.setMenge(Integer.parseInt(val[i + 1]));
						artikel.getBPos().add(bPos);
					}
					repo.add(artikel);
				}

				fileReader.close();
				System.out.println("Fertig");
				break;
			}
			case 2: {
				List<Artikel> artList = repo.getAll();
				for (Artikel art : artList) {
					System.out.printf(format, art.getArtnr(), art.getArtbez(), art.getMge(), art.getPreis());
					for (BPos pos : art.getBPos()) {
						System.out.printf(format, "", pos.getBestnr(), pos.getMenge(), "");
					}

					System.out.println("");
				}
				break;
			}
			case 3: {
				try {
					Artikel artikel = artikelByID(repo);

					System.out.printf(format, artikel.getArtnr(), artikel.getArtbez(), artikel.getMge(),
							artikel.getPreis());
					for (BPos pos : artikel.getBPos()) {
						System.out.printf(format, "", pos.getBestnr(), pos.getMenge(), "");
					}
				} catch (DocumentNotFoundException e) {
					System.out.println("Dokument nicht gefunden!");
				}

				break;
			}
			case 4: {
				try {
					Artikel artikel = artikelByID(repo);
					artikel.setPreis(99.99);
					repo.update(artikel);
				} catch (DocumentNotFoundException e) {
					System.out.println("Dokument nicht gefunden!");
				}
				break;
			}
			case 5: {
				try {
					Artikel artikel = artikelByID(repo);
					repo.remove(artikel);
				} catch (DocumentNotFoundException e) {
					System.out.println("Dokument nicht gefunden!");
				}
				break;
			}
			case 0:
				System.out.println("Auf Wiedersehen");
				System.exit(0);
				break;
			default:
				System.out.println("Ungültige eingabe");
			}

			System.out.println("\n");
		} while (true);
	}

	private static Artikel artikelByID(ArtikelRepository repo) throws IOException, DocumentNotFoundException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("ID Eingeben: ");
		String id;
		do {
			id = reader.readLine();
		} while (id.equals(""));
		return repo.get(id);
	}
}