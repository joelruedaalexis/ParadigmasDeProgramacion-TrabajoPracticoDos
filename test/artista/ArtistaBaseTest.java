package artista;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cancion.Cancion;

class ArtistaBaseTest {
	ArtistaBase cantante;

//	Falta probar entrenarRol -> ¿Exception maybe?
	@BeforeEach
	void setUp() throws Exception {
		BandaHistorico divididos = new BandaHistorico("Divididos");
		cantante = new ArtistaBase("Ricardo Mollo", Arrays.asList("voz principal"), Arrays.asList(divididos));
	}

	@Test
	void sePuedeInstanciarArtistaBase() {
		BandaHistorico banda = new BandaHistorico("Rata Blanca");
		String expectedNombre = "Walter Giardino";
		List<String> expectedRoles = Arrays.asList("guitarra electrica");
		List<BandaHistorico> expectedBanda = Arrays.asList(banda);
		List<Cancion> expectedCancion = new ArrayList<>();
		double expectedCosto = 0;
		ArtistaBase artistaBase = new ArtistaBase("Walter Giardino", Arrays.asList("guitarra electrica"),
				Arrays.asList(banda));
		assertNotNull(artistaBase);
		assertEquals(expectedNombre, artistaBase.getNombre());
		assertEquals(expectedRoles, artistaBase.getRoles());
		assertEquals(expectedBanda, artistaBase.getListaDeBandas());
		assertEquals(expectedCosto, artistaBase.getCosto());
		assertEquals(expectedCancion, artistaBase.getListaDeCancionesEnLasQueEstaAsignado());
		assertFalse(artistaBase.estaAsignadoAUnaCancion());
		assertTrue(artistaBase.perteneceADiscografica());
	}

	@Test
	void artistaBaseTieneRol() {
		String rolExpected = "voz principal";
		ArtistaBase artista = new ArtistaBase("Palito ortega", Arrays.asList("voz principal"),
				List.of(new BandaHistorico("Las Manos de Filippi")));
		assertTrue(artista.tieneRol(rolExpected));
	}

	@Test
	void artistaBaseNoTieneRol() {
		String rolQueNoTieneArtista = "acordeón";
		ArtistaBase artista = new ArtistaBase("Palito ortega", Arrays.asList("voz principal"),
				List.of(new BandaHistorico("Las Manos de Filippi")));
		assertFalse(artista.tieneRol(rolQueNoTieneArtista));
	}

	@Test
	void artistaBaseTieneBandaHistorico() {
		BandaHistorico banda = new BandaHistorico("Las Manos de Filippi");
		String nombre = "Palito ortega";
		ArtistaBase artista = new ArtistaBase(nombre, Arrays.asList("voz principal", "bateria"), List.of(banda));
		banda.agregarIntegrante(artista);

		assertEquals(List.of(banda), artista.getListaDeBandas());
	}

	@Test
	void sePuedeAsignarCancionUnica() {
		Cancion cancion = new Cancion("Todo un palo", List.of("voz principal"));
		List<Cancion> expectedCanciones = List.of(cancion);
		assertTrue(cantante.asignar(cancion));
		assertEquals(expectedCanciones, cantante.getListaDeCancionesEnLasQueEstaAsignado());
		assertTrue(cantante.estaAsignadoAUnaCancion());
	}

	@Test
	void noSePuedeAsignarCancionPorSerNula() {
		assertThrows(IllegalArgumentException.class, () -> cantante.asignar(null));
	}

	@Test
	void noSePuedeAsignarCancionRepetida() {
		Cancion cancion = new Cancion("Todo un palo", List.of("voz principal"));
		assertTrue(cantante.asignar(cancion));
		assertFalse(cantante.asignar(cancion));
	}

	@Test
	void sePuedeDesignarCancionExistente() {
		Cancion cancion = new Cancion("Todo un palo", List.of("voz principal"));
		cantante.asignar(cancion);
		assertTrue(cantante.designar(cancion));
		assertFalse(cantante.estaAsignadoAUnaCancion());
	}

	@Test
	void noSePuedeDesignarCancionPorSerNula() {
		assertThrows(IllegalArgumentException.class, () -> cantante.designar(null),
				"Debería arrojar la exception IllegalArgumentException por tener a cancion como null");
	}

	@Test
	void noSePuedeDesignarCancionInexistente() {
		Cancion cancion = new Cancion("Todo un palo", List.of("voz principal"));
		Cancion cancion2 = new Cancion("Mas que nada", List.of("acordeón", "voz principal"));
		Cancion cancionInexistente = new Cancion("Mi enfermedad", List.of("armonica", "voz principal"));
		cantante.asignar(cancion);
		cantante.asignar(cancion2);
		assertFalse(cantante.designar(cancionInexistente));
	}

	@Test
	void siPuedeSerAsignadoACancion() {
		assertTrue(cantante.puedeSerAsignadoACancion());
	}

	@Test
	void toJsonFunciona() {
		JsonObject expectedArtistaJSON = new JsonObject();
		JsonArray rolesJSON = new JsonArray(cantante.getRoles().size());
		JsonArray bandasJSON = new JsonArray(cantante.getListaDeBandas().size());
		cantante.getRoles().forEach(rol -> rolesJSON.add(rol));
		cantante.getListaDeBandas().forEach(banda -> bandasJSON.add(banda.getNombre()));
		expectedArtistaJSON.addProperty("nombre", cantante.getNombre());
		expectedArtistaJSON.add("roles", rolesJSON);
		expectedArtistaJSON.add("bandas", bandasJSON);

		assertEquals(expectedArtistaJSON, cantante.toJson());
	}
}
