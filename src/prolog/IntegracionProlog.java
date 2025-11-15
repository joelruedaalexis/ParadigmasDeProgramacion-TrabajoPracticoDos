package prolog;
import org.jpl7.*;

public class IntegracionProlog {
	
	public static void prueba() {

		Query q1 = new Query(
			    "consult",
			    new Term[] {new Atom("src/assets/datos.pl")}
			);

			System.out.println("consult datos.pl: " + q1.hasSolution());
			
			System.out.println("Absolute datos.pl: " + 
				    new java.io.File("src/assets/datos.pl").getAbsolutePath());
			
		Query q2 = 
				  new Query( 
				      "child_of", 
				      new Term[] {new Atom("joe"),new Atom("ralf")} 
				  );
				System.out.println( 
				  "child_of(joe,ralf) is " + 
				  ( q2.hasSolution() ? "provable" : "not provable" ) 
				);
					
		Query q3 = 
				  new Query( 
				      "descendent_of", 
				      new Term[] {new Atom("steve"),new Atom("ralf")} 
				  );
				System.out.println( 
				  "descendent_of(joe,ralf) is " + 
				  ( q3.hasSolution() ? "provable" : "not provable" ) 
				);





	}
}
