package banda;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import artista.Artista;
import artista.ArtistaBase;
import artista.ArtistaContratado;

class BandaHistoricoTest {
	BandaHistorico divididos;
	Artista artistaBase;

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
		assertTrue("Es verdadero porque es el primer integrante", divididos.agregarIntegrante(artistaBase));
	}

	@Test
	void noSePuedeAgregarIntegranteYaExistente() {
		assertTrue("Es verdadero porque es el primer integrante.", divididos.agregarIntegrante(artistaBase));
		assertFalse("Es falso porque es el integrante ya está agregado.", divididos.agregarIntegrante(artistaBase));
	}

	@Test
	void bandaNoTieneArtistaDeDiscografica() {
		Artista artistaContratado = new ArtistaContratado("Catriel Ciavarella", Arrays.asList("batería"),
				Arrays.asList(divididos), 1610, 3);
		assertTrue(divididos.agregarIntegrante(artistaContratado));
		boolean condicion = divididos.tieneArtistaDeDiscografica();
		assertFalse("Debería ser falso porque Catriel es un artista contratado.", condicion);
	}

	@Test
	void bandaTieneArtistaDeDiscografica() {
		Artista artistaContratado = new ArtistaContratado("Catriel Ciavarella", Arrays.asList("batería"),
				Arrays.asList(divididos), 1610, 3);
		assertTrue(divididos.agregarIntegrante(artistaContratado));
		assertTrue(divididos.agregarIntegrante(artistaBase));
		boolean condicion = divididos.tieneArtistaDeDiscografica();
		assertTrue("Debería ser verdadero porque tiene a Ricardo Mollo que es un artista base.", condicion);
	}

}
