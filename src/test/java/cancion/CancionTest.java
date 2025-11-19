package cancion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import artista.ArtistaBase;
import artista.ArtistaContratado;
import artista.BandaHistorico;

class CancionTest {
	ArtistaContratado guitarristaContratado, guitarristaSolistaContratado;
	int topeDeCanciones, costoDeArtista;
	BandaHistorico redondos;
	Cancion cancion;
	ArtistaBase cantanteBase;

	@BeforeEach
	void setUp() throws Exception {
		topeDeCanciones = 2;
		costoDeArtista = 1000;
		redondos = new BandaHistorico("Patricio Rey y sus Redonditos de Ricota");
		guitarristaContratado = new ArtistaContratado("Eduardo Beilinson",
				new ArrayList<>(Arrays.asList("guitarra electrica")), Arrays.asList(redondos), costoDeArtista,
				topeDeCanciones);
		cantanteBase = new ArtistaBase("Carlos Alberto Solari", List.of("voz principal"), List.of(redondos));

		guitarristaSolistaContratado = new ArtistaContratado("Walter Giardino",
				new ArrayList<>(Arrays.asList("guitarra electrica")),
				Arrays.asList(new BandaHistorico("Walter Giardino")), costoDeArtista, topeDeCanciones);

		cancion = new Cancion("La Hija del Fletero",
				new ArrayList<>(List.of("guitarra electrica", "voz principal", "armónica")));
	}

	@Test
	void cancionSePuedeInstanciar() {
		List<String> expectedRoles = new ArrayList<>(List.of("guitarra electrica", "voz principal", "armónica"));
		int expectedCupos = expectedRoles.size();
		Map<String, Integer> expectedRolesFaltantesXCupos = new HashMap<>(expectedRoles.size());
		for (String rol : expectedRoles) {
			expectedRolesFaltantesXCupos.put(rol, 1);
		}
		String expectedTitulo = "La Hija del Fletero";
		Cancion cancion = new Cancion("La Hija del Fletero",
				new ArrayList<>(List.of("guitarra electrica", "voz principal", "armónica")));
		assertNotNull(cancion);
		assertEquals(expectedTitulo, cancion.getTitulo());
		assertEquals(expectedRoles, cancion.getRoles());
		assertEquals(expectedCupos, cancion.getCantDeCuposDisponibles());
		assertEquals(expectedRolesFaltantesXCupos, cancion.getRolesFaltantesXCupos());
		assertTrue(cancion.getListadoDeIntegrantes().isEmpty());
	}

	@Test
	void noSePuedeAgregarArtistaConRolEnNull() {
		Cancion cancion = new Cancion("La Hija del Fletero",
				new ArrayList<>(List.of("guitarra electrica", "voz principal", "armónica")));
		assertThrows(IllegalArgumentException.class, () -> cancion.agregarArtista(null, guitarristaContratado));
	}

	@Test
	void noSePuedeAgregarArtistaEnNull() {
		Cancion cancion = new Cancion("La Hija del Fletero",
				new ArrayList<>(List.of("guitarra electrica", "voz principal", "armónica")));
		assertThrows(IllegalArgumentException.class, () -> cancion.agregarArtista("armónica", null));

	}

	@Test
	void sePuedeAgregarArtistaBase() {
		List<String> expectedRolesFaltantes = new ArrayList<>(List.of("guitarra electrica", "armónica"));
		int expectedCupos = expectedRolesFaltantes.size();
		Map<String, Integer> expectedRolesFaltantesXCupos = new HashMap<>(expectedRolesFaltantes.size());
		for (String rol : expectedRolesFaltantes) {
			expectedRolesFaltantesXCupos.put(rol, 1);
		}
		assertTrue(cancion.agregarArtista("voz principal", cantanteBase));
		assertEquals(expectedRolesFaltantesXCupos, cancion.getRolesFaltantesXCupos());
		assertEquals(expectedCupos, cancion.getCantDeCuposDisponibles());
		assertTrue(cancion.artistaEstaAsignado(cantanteBase));
		int expectedCantidadIntegrantes = 1;
		assertEquals(expectedCantidadIntegrantes, cancion.getListadoDeIntegrantes().size());
		double expectedCosto = 0;
		assertEquals(expectedCosto, cancion.getCostoDeCancion());
		List<ArtistaBase> expectedIntegrantes = List.of(cantanteBase);
		assertEquals(expectedIntegrantes, cancion.getListadoDeIntegrantes());
	}

	@Test
	void sePuedeAgregarArtistaContratado() {
		List<String> expectedRolesFaltantes = new ArrayList<>(List.of("voz principal", "armónica"));
		int expectedCupos = expectedRolesFaltantes.size();
		Map<String, Integer> expectedRolesFaltantesXCupos = new HashMap<>(expectedRolesFaltantes.size());
		for (String rol : expectedRolesFaltantes) {
			expectedRolesFaltantesXCupos.put(rol, 1);
		}
		Cancion cancion = new Cancion("La Hija del Fletero",
				new ArrayList<>(List.of("guitarra electrica", "voz principal", "armónica")));

		assertTrue(cancion.agregarArtista("guitarra electrica", guitarristaSolistaContratado));
		assertEquals(expectedRolesFaltantesXCupos, cancion.getRolesFaltantesXCupos());
		assertEquals(expectedCupos, cancion.getCantDeCuposDisponibles());

		int expectedCantidadIntegrantes = 1;
		assertEquals(expectedCantidadIntegrantes, cancion.getListadoDeIntegrantes().size());
		double expectedCosto = costoDeArtista;
		assertEquals(expectedCosto, cancion.getCostoDeCancion());
	}

	@Test
	void noSePuedeAgregarArtistaARolInexistente() {
		String rolInexistenteEnCancion = "piano";
		ArtistaBase artista = new ArtistaBase("Nicholas Britell", List.of(rolInexistenteEnCancion),
				List.of(new BandaHistorico("Succession")));
		assertFalse(cancion.agregarArtista(rolInexistenteEnCancion, artista));
	}

	@Test
	void noSePuedeAgregarArtistaExistenteEnCancion() {
		assertTrue(cancion.agregarArtista("guitarra electrica", guitarristaSolistaContratado));
		assertFalse(cancion.agregarArtista("guitarra electrica", guitarristaSolistaContratado));
	}

	@Test
	void noSePuedeAgregarArtistaEnRolYaOcupado() {
		cancion.agregarArtista("guitarra electrica", guitarristaSolistaContratado);
		assertFalse(cancion.getRolesFaltantesXCupos().containsKey("guitarra electrica"));
		assertFalse(cancion.agregarArtista("guitarra electrica", guitarristaContratado));
	}

	@Test
	void noSePuedeAgregarArtistaPorExcederLimiteDeCanciones() {
		Cancion cancion2 = new Cancion("Rock para el Negro Atila", new ArrayList<>(List.of("guitarra electrica")));
		Cancion cancion3 = new Cancion("Un Ángel para tu Soledad", new ArrayList<>(List.of("guitarra electrica")));
		cancion.agregarArtista("guitarra electrica", guitarristaSolistaContratado);
		assertTrue(guitarristaSolistaContratado.puedeSerAsignadoACancion());
		cancion2.agregarArtista("guitarra electrica", guitarristaSolistaContratado);
		assertFalse(guitarristaSolistaContratado.puedeSerAsignadoACancion());
		assertFalse(cancion3.agregarArtista("guitarra electrica", guitarristaSolistaContratado));
	}

	@Test
	void noSePuedeQuitarArtistaEnNull() {
		assertThrows(IllegalArgumentException.class, () -> cancion.quitarArtista(null));
	}

	@Test
	void noSePuedeQuitarArtistaInexistente() {
		assertFalse(cancion.getListadoDeIntegrantes().contains(guitarristaContratado));
		assertFalse(cancion.quitarArtista(guitarristaContratado));
	}

	@Test
	void sePuedeQuitarArtistaExistente() {
		cancion.agregarArtista("guitarra electrica", guitarristaContratado);
		assertTrue(cancion.getListadoDeIntegrantes().contains(guitarristaContratado));
		assertTrue(cancion.quitarArtista(guitarristaContratado));
		assertFalse(cancion.getListadoDeIntegrantes().contains(guitarristaContratado));
	}

	@Test
	void elCostoSeAcumulaPorIntegrante() {
		ArtistaContratado armonicista = new ArtistaContratado("Ciro Martinez", List.of("armónica"),
				List.of(new BandaHistorico("Los piojos")), 5000, topeDeCanciones);
		double expectedPrecio = guitarristaContratado.getCosto() + armonicista.getCosto();
		cancion.agregarArtista("voz principal", cantanteBase);
		cancion.agregarArtista("guitarra electrica", guitarristaContratado);
		cancion.agregarArtista("armónica", armonicista);
		assertEquals(expectedPrecio, cancion.getCostoDeCancion());
	}
}
