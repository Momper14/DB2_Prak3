package couchdb;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;

public class ArtikelRepository extends CouchDbRepositorySupport<Artikel> {

	public ArtikelRepository(CouchDbConnector db) {
		super(Artikel.class, db);
	}

} 