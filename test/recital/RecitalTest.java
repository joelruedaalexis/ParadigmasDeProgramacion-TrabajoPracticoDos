package recital;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;

import artista.ArtistaBase;
import artista.BandaHistorico;
import cancion.Cancion;

class RecitalTest {
	Recital recital;
	List<Cancion> cancionero;
	List<String> roles;
	List<ArtistaBase> lineUp;
	Cancion cancion1, cancion2, cancion3, cancion4, cancion5;
	final String vozPrincipal = "voz principal", vozSecundaria = "voz secundaria",
			guitarraElectrica = "guitarra eléctrica", armonica = "armónica", bateria = "batería", piano = "piano",
			bajo = "bajo", saxofon = "saxofón", acordeon = "acordeón";
	ArtistaBase cantanteBase1, guitarristaBase1;
	ArtistaBase cantanteBase2;
	List<ArtistaBase> lineUpArtistaBase;

	@BeforeEach
	void setUp() throws Exception {
//		String[] rol = { "guitarra eléctrica", "armónica", "voz principal", "voz secundaria", "batería", "piano",
//				"bajo", "saxofón", "acordeón" };
//		roles = new ArrayList<>(Arrays.asList(rol));
		roles = new ArrayList<>(Arrays.asList(vozPrincipal, vozSecundaria, guitarraElectrica, armonica, bateria, piano,
				bajo, saxofon, acordeon));
		cancion1 = new Cancion("Hábil", new ArrayList<>(List.of(vozPrincipal, vozSecundaria, guitarraElectrica)));
//		cancion2 = new Cancion("Who's Back", new ArrayList<>(List.of("")));
//		cancion3 = new Cancion("Román", new ArrayList<>(List.of("")));
//		cancion4 = new Cancion("Monoblock", new ArrayList<>(List.of("")));
//		cancion5 = new Cancion("Crow", new ArrayList<>(List.of("")));
		BandaHistorico redondos = new BandaHistorico("Patricio Rey y sus Redonditos de Ricota");
		cantanteBase1 = new ArtistaBase("Carlos Alberto Solari", new ArrayList<>(Arrays.asList(vozPrincipal)),
				List.of(redondos, new BandaHistorico("Los Fundamentalistas del Aire Acondicionado")));
		guitarristaBase1 = new ArtistaBase("Eduardo Beilinson", new ArrayList<>(List.of(guitarraElectrica)),
				List.of(redondos));
		cantanteBase2 = new ArtistaBase("Agustin Cruz", new ArrayList<>(List.of(vozPrincipal, vozSecundaria)),
				List.of(new BandaHistorico("Acru")));
		lineUpArtistaBase = new ArrayList<>(List.of(cantanteBase1, guitarristaBase1, cantanteBase2));

		cancionero.addLast(cancion1);
	}

//	@Test
	void sePuedeInstanciarRecital() {

	}

	void contratacionExitosaDeArtistasBaseParaUnaCancion() {
		recital = new Recital(cancionero, lineUpArtistaBase, roles);
		int indexCancion1 = 0;
		TransaccionAsignacionDeCancion transaccion = recital.contratarArtistasParaUnaCancion(indexCancion1);
		assertTrue(transaccion.esTransaccionCommitted());
		assertEquals(lineUpArtistaBase, cancion1.getListadoDeIntegrantes());
		int expectedCuposDisponibles = 0;
		assertEquals(expectedCuposDisponibles, cancion1.getCantDeCuposDisponibles());
	}

	void contratacionFallidaPorNoTenerArtistasContratadosParaEntrenarRolesDeCancion() {
		recital = new Recital(cancionero, new ArrayList<>(List.of(cantanteBase1)), roles);
		int indexCancion1 = 0;
		TransaccionAsignacionDeCancion transaccion = recital.contratarArtistasParaUnaCancion(indexCancion1);
		assertFalse(transaccion.esTransaccionCommitted());
		assertFalse(transaccion.sePuedenEntrenarArtistasSuficientes());
		assertEquals(lineUpArtistaBase, cancion1.getListadoDeIntegrantes());
		int expectedCuposDisponibles = cancion1.getRoles().size();
		assertEquals(expectedCuposDisponibles, cancion1.getCantDeCuposDisponibles());
	}

}
