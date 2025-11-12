package artista;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BandaHistoricoTest {
	BandaHistorico divididos;
	ArtistaBase artistaBase;

	@BeforeEach
	void setUp() {
		divididos = new BandaHistorico("Divididos");
		artistaBase = new ArtistaBase("Ricardo Mollo", Arrays.asList("voz principal"), Arrays.asList(divididos));
	}

	@Test
	void instanciarBanda() {
		BandaHistorico rataBlanca = new BandaHistorico("Rata Blanca");
		assertNotNull(rataBlanca, "Se tendría que poder instanciar la clase");
		assertEquals("Rata Blanca", rataBlanca.getNombre(), "Los nombres deberían ser iguales.");
	}

	@Test
	void IllegalArgumentExceptionEnElNombreDeLaBanda() {
		assertThrows(IllegalArgumentException.class, () -> new BandaHistorico(null),
				"El constructor lanza un \"IllegalArgumentException\" porque el nombre es nulo");
		assertThrows(IllegalArgumentException.class, () -> new BandaHistorico(""),
				"El constructor lanza un \"IllegalArgumentException\" porque el nombre está vacio");
	}

	@Test
	void noSePuedeAgregarIntegranteComoNull() {
		assertThrows(IllegalArgumentException.class, () -> divididos.agregarIntegrante(null),
				"Es verdadero porque es el primer integrante");
	}

	@Test
	void sePuedeAgregarIntegrante() {
		divididos = new BandaHistorico("Divididos");
		artistaBase = new ArtistaBase("Ricardo Mollo", Arrays.asList("voz principal"),
				Arrays.asList(new BandaHistorico("Los redondos")));
		assertTrue("Es verdadero porque es el primer integrante", divididos.agregarIntegrante(artistaBase));
	}

	@Test
	void noSePuedeAgregarIntegranteYaExistente() {
		divididos = new BandaHistorico("Divididos");
		artistaBase = new ArtistaBase("Ricardo Mollo", Arrays.asList("voz principal"),
				Arrays.asList(new BandaHistorico("Los redondos")));
		assertTrue("Es verdadero porque es el primer integrante.", divididos.agregarIntegrante(artistaBase));
		assertFalse("Es falso porque es el integrante ya está agregado.", divididos.agregarIntegrante(artistaBase));
	}

	@Test
	void bandaNoTieneArtistaDeDiscografica() {
		divididos = new BandaHistorico("Divididos");
		new ArtistaContratado("Catriel Ciavarella", Arrays.asList("batería"), Arrays.asList(divididos), 1610, 3);
		boolean condicion = divididos.tieneArtistaDeDiscografica();
		assertFalse("Debería ser falso porque Catriel es un artista contratado.", condicion);
	}

	@Test
	void bandaTieneArtistaDeDiscografica() {
		new ArtistaContratado("Catriel Ciavarella", Arrays.asList("batería"), Arrays.asList(divididos), 1610, 3);
		boolean condicion = divididos.tieneArtistaDeDiscografica();
		assertTrue("Debería ser verdadero porque tiene a Ricardo Mollo que es un artista base.", condicion);
	}

}
