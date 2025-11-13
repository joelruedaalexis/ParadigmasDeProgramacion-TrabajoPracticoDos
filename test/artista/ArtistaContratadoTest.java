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

class ArtistaContratadoTest {
	ArtistaContratado guitarrista;
	int topeDeCanciones;
	int costoDeArtista;
	BandaHistorico redondos;

	@BeforeEach
	void setUp() throws Exception {
		topeDeCanciones = 2;
		costoDeArtista = 1000;
		redondos = new BandaHistorico("Patricio Rey y sus Redonditos de Ricota");
		guitarrista = new ArtistaContratado("Eduardo Beilinson", new ArrayList<>(Arrays.asList("guitarra electrica")),
				Arrays.asList(redondos), costoDeArtista, topeDeCanciones);
	}

	@Test
	void sePuedeInstanciarArtistaContratado() {
		BandaHistorico banda = new BandaHistorico("Linkin Park");
		String expectedNombre = "Chester Bennington";
		List<String> expectedRoles = Arrays.asList("voz principal");
		List<BandaHistorico> expectedBanda = Arrays.asList(banda);
		List<Cancion> expectedCancion = new ArrayList<>();
		double costo = 1000, expectedCosto = 1000;
		int maxCanciones = 3;
		ArtistaContratado artistaContratado = new ArtistaContratado("Chester Bennington",
				Arrays.asList("voz principal"), Arrays.asList(banda), costo, maxCanciones);
		assertNotNull(artistaContratado);
		assertEquals(expectedNombre, artistaContratado.getNombre());
		assertEquals(expectedRoles, artistaContratado.getRoles());
		assertEquals(expectedBanda, artistaContratado.getListaDeBandas());
		assertEquals(expectedCosto, artistaContratado.getCosto());
		assertEquals(expectedCancion, artistaContratado.getListaDeCancionesEnLasQueEstaAsignado());
		assertFalse(artistaContratado.estaAsignadoAUnaCancion());
		assertFalse(artistaContratado.perteneceADiscografica());
		assertFalse(artistaContratado.tieneDescuento());
	}

	@Test
	void artistaContratadoTieneDescuento() {
		ArtistaBase artistaBase = new ArtistaBase("Carlos Alberto Solari", List.of("voz principal"), List.of(redondos));
		double expectedCosto = costoDeArtista * 0.5;
		assertTrue(artistaBase.perteneceADiscografica());
		assertTrue(guitarrista.tieneDescuento(),
				"Debería ser verdadero porque tiene compartió banda con un artista base.");
		assertEquals(expectedCosto, guitarrista.getCosto(), "Al tener descuento, su costo debería ser un 50% menor.");
	}

	@Test
	void artistaContratadoDescuentoNoSeAcumula() {
		ArtistaBase artistaBase1 = new ArtistaBase("Carlos Alberto Solari", List.of("voz principal"),
				List.of(redondos));
		ArtistaBase artistaBase2 = new ArtistaBase("Sergio Dawi", List.of("saxofón"), List.of(redondos));
		double expectedCosto = costoDeArtista * 0.5;
		assertTrue(artistaBase1.perteneceADiscografica());
		assertTrue(artistaBase2.perteneceADiscografica());
		assertTrue(guitarrista.tieneDescuento());
		assertEquals(expectedCosto, guitarrista.getCosto());
	}

	@Test
	void sePuedeAsignarCancion() {
		Cancion cancion = new Cancion("Todo un palo", List.of("guitarra electrica"));
		int expectedMaxCanciones = topeDeCanciones - 1;
		List<Cancion> expectedCanciones = List.of(cancion);
		assertTrue(guitarrista.puedeSerAsignadoACancion());
		assertTrue(guitarrista.asignar(cancion));
		assertEquals(expectedCanciones, guitarrista.getListaDeCancionesEnLasQueEstaAsignado());
		assertTrue(guitarrista.estaAsignadoAUnaCancion());
		assertEquals(expectedMaxCanciones, guitarrista.maxCanciones);
	}

	@Test
	void noSePuedeAsignarCancionSiArtistaExcedeDelLimiteDeCanciones() {
		Cancion cancion1 = new Cancion("Todo un palo", List.of("guitarra electrica"));
		Cancion cancion2 = new Cancion("Un Pacman en el Savoy", List.of("guitarra electrica"));
		Cancion cancion3 = new Cancion("Masacre en el Puticlub", List.of("guitarra electrica"));
		int expectedMaxCanciones = topeDeCanciones - 2;
		List<Cancion> expectedCanciones = List.of(cancion1, cancion2);

		assertTrue(guitarrista.puedeSerAsignadoACancion());
		assertTrue(guitarrista.asignar(cancion1));
		assertTrue(guitarrista.asignar(cancion2));
		assertFalse(guitarrista.puedeSerAsignadoACancion());
		assertFalse(guitarrista.asignar(cancion3));
		assertEquals(expectedCanciones, guitarrista.getListaDeCancionesEnLasQueEstaAsignado());
		assertEquals(expectedMaxCanciones, guitarrista.maxCanciones);
	}

	@Test
	void noSePuedeAsignarCancionPorSerNula() {
		assertThrows(IllegalArgumentException.class, () -> guitarrista.asignar(null));
	}

	@Test
	void noSePuedeAsignarCancionRepetida() {
		Cancion cancion = new Cancion("Todo un palo", List.of("guitarra electrica"));
		assertTrue(guitarrista.asignar(cancion));
		assertFalse(guitarrista.asignar(cancion));
	}

	@Test
	void sePuedeDesignarCancionExistente() {
		Cancion cancion = new Cancion("Todo un palo", List.of("guitarra electrica"));
		int expectedMaxCanciones = topeDeCanciones;

		guitarrista.asignar(cancion);
		assertTrue(guitarrista.designar(cancion));
		assertFalse(guitarrista.estaAsignadoAUnaCancion());
		assertEquals(expectedMaxCanciones, guitarrista.maxCanciones);
	}

	@Test
	void noSePuedeDesignarCancionPorSerNula() {
		assertThrows(IllegalArgumentException.class, () -> guitarrista.designar(null),
				"Debería arrojar la exception IllegalArgumentException por tener a cancion como null");
	}

	@Test
	void noSePuedeDesignarCancionInexistente() {
		Cancion cancion1 = new Cancion("Todo un palo", List.of("guitarra electrica"));
		Cancion cancion2 = new Cancion("Esa estrella era mi lujo", List.of("saxofón", "guitarra electrica"));
		Cancion cancionInexistente = new Cancion("Vencedores Vencidos", List.of("voz principal", "guitarra electrica"));
		guitarrista.asignar(cancion1);
		guitarrista.asignar(cancion2);
		assertFalse(guitarrista.designar(cancionInexistente));
	}

	@Test
	void noPerteneceADiscografica() {
		assertFalse(guitarrista.perteneceADiscografica());
	}

	@Test
	void siPuedeSerAsignadoACancion() {
		assertTrue(guitarrista.puedeSerAsignadoACancion());
	}

	@Test
	void puedeEntrenarNuevoRol() {
		String nuevoRol = "saxofón";
		double expectedNuevoCosto = costoDeArtista * 1.5;
		assertTrue(guitarrista.entrenarNuevoRol(nuevoRol), "Deberia ser verdadero porque no tiene ese rol");
		assertTrue(guitarrista.getRoles().contains(nuevoRol),
				"Deberia ser verdadero porque se pudo entrenar un nuevo rol");
		assertEquals(expectedNuevoCosto, guitarrista.getCosto(), "Al tener un nuevo rol su costo aumenta un 50%");
	}

	@Test
	void entrenarNuevosRolesAumentaElCostoYTambienAplicaDescuentoSiComparteConArtistaBase() {
		ArtistaBase artistaBase1;
		String nuevoRol1 = "saxofón";
		String nuevoRol2 = "voz principal";
		double expectedNuevoCosto = costoDeArtista * 1.5 * 1.5;
		double expectedCostoConDescuento = expectedNuevoCosto * 0.5;
		guitarrista.entrenarNuevoRol(nuevoRol1);
		guitarrista.entrenarNuevoRol(nuevoRol2);
		assertEquals(expectedNuevoCosto, guitarrista.getCosto());

		artistaBase1 = new ArtistaBase("Carlos Alberto Solari", List.of("voz principal"), List.of(redondos));
		assertTrue(artistaBase1.perteneceADiscografica());
		assertEquals(expectedCostoConDescuento, guitarrista.getCosto());
	}

	@Test
	void noPuedeEntrenarRolYaExistente() {
		String rolExistente = "guitarra electrica";
		assertTrue(guitarrista.getRoles().contains(rolExistente));
		assertFalse(guitarrista.entrenarNuevoRol(rolExistente));
	}

	@Test
	void noPuedeEntrenarRolNull() {
		assertThrows(IllegalArgumentException.class, () -> guitarrista.entrenarNuevoRol(null));
	}

	@Test
	void toJsonFunciona() {
		JsonObject expectedArtistaJSON = new JsonObject();
		JsonArray rolesJSON = new JsonArray(guitarrista.getRoles().size());
		JsonArray bandasJSON = new JsonArray(guitarrista.getListaDeBandas().size());
		guitarrista.getRoles().forEach(rol -> rolesJSON.add(rol));
		guitarrista.getListaDeBandas().forEach(banda -> bandasJSON.add(banda.getNombre()));
		expectedArtistaJSON.addProperty("nombre", guitarrista.getNombre());
		expectedArtistaJSON.add("roles", rolesJSON);
		expectedArtistaJSON.add("bandas", bandasJSON);
		expectedArtistaJSON.addProperty("costo", guitarrista.costo);
		expectedArtistaJSON.addProperty("maxCanciones",
				guitarrista.maxCanciones + guitarrista.cancionesEnLasQueEstaAsignado.size());

		assertEquals(expectedArtistaJSON, guitarrista.toJson());
	}
}
