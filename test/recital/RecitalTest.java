package recital;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import artista.ArtistaBase;
import artista.ArtistaContratado;
import artista.BandaHistorico;
import artista.ComparadorArtistaPorNombre;
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
	ArtistaContratado bateristaContratado1;
	ArtistaContratado bajistaContratado1;
	ArtistaContratado cantanteContratado1;
	int maxCanciones;
	List<ArtistaBase> lineUpArtistaBase;
	List<ArtistaBase> lineUpArtistaContratado;

	@BeforeEach
	void setUp() throws Exception {
//		String[] rol = { "guitarra eléctrica", "armónica", "voz principal", "voz secundaria", "batería", "piano",
//				"bajo", "saxofón", "acordeón" };
//		roles = new ArrayList<>(Arrays.asList(rol));
		roles = new ArrayList<>(Arrays.asList(vozPrincipal, vozSecundaria, guitarraElectrica, armonica, bateria, piano,
				bajo, saxofon, acordeon));
		cancion1 = new Cancion("Hábil", new ArrayList<>(List.of(vozPrincipal, vozSecundaria, guitarraElectrica)));
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

		cancion2 = new Cancion("Who's Back", new ArrayList<>(List.of(vozPrincipal, bateria, bajo)));
		maxCanciones = 2;
		bateristaContratado1 = new ArtistaContratado("Walter Sidotti", new ArrayList<>(List.of(bateria)),
				List.of(redondos), 3500, maxCanciones);
		BandaHistorico sodaStereo = new BandaHistorico("Soda Stereo");
		bajistaContratado1 = new ArtistaContratado("Zera Bosio", new ArrayList<>(List.of(bajo)), List.of(sodaStereo),
				5000, maxCanciones);
		cantanteContratado1 = new ArtistaContratado("Gustavo Cerati", new ArrayList<>(List.of(vozPrincipal)),
				List.of(sodaStereo), 5000, maxCanciones);
//		lineUpArtistaContratado = new ArrayList<>(
//				List.of(bateristaContratado1, bajistaContratado1, cantanteContratado1));
		cancionero = new ArrayList<>();
	}

//	@Test
	void sePuedeInstanciarRecital() {

	}

	@Test
	void contratacionExitosaDeArtistasBaseParaUnaCancion() {
//		cancion1 = new Cancion("Hábil", new ArrayList<>(List.of(vozPrincipal, vozSecundaria, guitarraElectrica)));
		cancionero.addLast(cancion1);
		recital = new Recital(cancionero, lineUpArtistaBase, roles);
		int indexCancion1 = cancionero.indexOf(cancion1);
		TransaccionAsignacionDeCancion transaccion = recital.contratarArtistasParaUnaCancion(indexCancion1);
		assertTrue(transaccion.esTransaccionCommitted());
		lineUpArtistaBase.sort(new ComparadorArtistaPorNombre());
		List<ArtistaBase> integrantes = cancion1.getListadoDeIntegrantes();
		integrantes.sort(new ComparadorArtistaPorNombre());
		assertEquals(lineUpArtistaBase, integrantes);

		int expectedCuposDisponibles = 0;
		assertEquals(expectedCuposDisponibles, cancion1.getCantDeCuposDisponibles());
	}

	@Test
	void contratacionFallidaPorNoTenerArtistasContratadosParaEntrenarRolesDeCancion() {
		cancionero.addLast(cancion2);

		recital = new Recital(cancionero, lineUpArtistaBase, roles);
		int indexCancion2 = cancionero.indexOf(cancion2);
		TransaccionAsignacionDeCancion transaccion = recital.contratarArtistasParaUnaCancion(indexCancion2);
		assertFalse(transaccion.esTransaccionCommitted());
		assertFalse(transaccion.sePuedenEntrenarArtistasSuficientes());
	}

	@Test
	void contratacionExitosaDeArtistasContratadosParaUnaCancion() {
		cancionero.addLast(cancion2);
		lineUpArtistaContratado = new ArrayList<>(
				List.of(bateristaContratado1, bajistaContratado1, cantanteContratado1));
		recital = new Recital(cancionero, lineUpArtistaContratado, roles);
		int indexCancion2 = cancionero.indexOf(cancion2);
		TransaccionAsignacionDeCancion transaccion = recital.contratarArtistasParaUnaCancion(indexCancion2);
		assertTrue(transaccion.esTransaccionCommitted());
		lineUpArtistaContratado.sort(new ComparadorArtistaPorNombre());
		List<ArtistaBase> integrantes = cancion2.getListadoDeIntegrantes();
		integrantes.sort(new ComparadorArtistaPorNombre());
		assertEquals(lineUpArtistaContratado, integrantes);
		int expectedCuposDisponibles = 0;
		assertEquals(expectedCuposDisponibles, cancion2.getCantDeCuposDisponibles());
	}

	@Test
	void contratacionFallidaDeArtistasContratadosParaUnaCancionPorLimiteDeCanciones() {
		int expectedCuposDisponibles = 3;
		cancion3 = new Cancion("Román", new ArrayList<>(List.of(vozPrincipal, bateria, bajo)));
		cancion4 = new Cancion("Monoblock", new ArrayList<>(List.of(vozPrincipal, bateria, bajo)));
//		cancion5 = new Cancion("Crow", new ArrayList<>(List.of(vozPrincipal, bateria, bajo)));
		cancionero.addLast(cancion2);
		cancionero.addLast(cancion3);
		cancionero.addLast(cancion4);
//		cancionero.addLast(cancion5);
		int indexCancion1 = 0, indexCancion2 = 1, indexCancion3 = 2;
		lineUpArtistaContratado = new ArrayList<>(
				List.of(bateristaContratado1, bajistaContratado1, cantanteContratado1));
		recital = new Recital(cancionero, lineUpArtistaContratado, roles);
		assertTrue(recital.contratarArtistasParaUnaCancion(indexCancion1).esTransaccionCommitted());
		assertTrue(recital.contratarArtistasParaUnaCancion(indexCancion2).esTransaccionCommitted());
		TransaccionAsignacionDeCancion transaccion = recital.contratarArtistasParaUnaCancion(indexCancion3);
		assertFalse(transaccion.esTransaccionCommitted());
		assertFalse(transaccion.sePuedenEntrenarArtistasSuficientes());
		assertEquals(expectedCuposDisponibles, cancion4.getCantDeCuposDisponibles());

	}

	@Test
	void contratacionExitosaDeArtistasParaUnaCancionTeniendoQueHaberEntrenadoRolesFaltantes() {
		cancion2 = new Cancion("Román", new ArrayList<>(List.of(vozPrincipal, piano, piano, armonica, vozSecundaria)));
//		cancion5 = new Cancion("Crow", new ArrayList<>(List.of(vozPrincipal, bateria, bajo)));
		cancionero.addLast(cancion2);
//		cancionero.addLast(cancion5);
		int indexCancion2 = 0;
		lineUp = new ArrayList<>(List.of(bateristaContratado1, bajistaContratado1, cantanteContratado1, cantanteBase1,
				bajistaContratado1));
		recital = new Recital(cancionero, lineUp, roles);
		assertTrue(cancion2.getListadoDeIntegrantes().isEmpty());
		TransaccionAsignacionDeCancion transaccion = recital.contratarArtistasParaUnaCancion(indexCancion2);
		assertFalse(transaccion.esTransaccionCommitted());
		assertTrue(transaccion.sePuedenEntrenarArtistasSuficientes());
		transaccion.entrenarArtistasRecomendadosYAsignarLosCandidatos(TransaccionAsignacionDeCancion.SI);
		int expectedCuposDisponibles = 0;
		assertEquals(expectedCuposDisponibles, cancion2.getCantDeCuposDisponibles());
	}

	@Test
	void contratacionFallidaDeArtistasParaUnaCancionPorqueNoSeDeseaAEntrenarArtistas() {
		int expectedCuposDisponibles = 5;
		cancion2 = new Cancion("Román", new ArrayList<>(List.of(vozPrincipal, piano, piano, armonica, vozSecundaria)));
//		cancion5 = new Cancion("Crow", new ArrayList<>(List.of(vozPrincipal, bateria, bajo)));
		cancionero.addLast(cancion2);
//		cancionero.addLast(cancion5);
		int indexCancion2 = 0;
		lineUp = new ArrayList<>(List.of(bateristaContratado1, bajistaContratado1, cantanteContratado1, cantanteBase1,
				bajistaContratado1));
		recital = new Recital(cancionero, lineUp, roles);
		assertTrue(cancion2.getListadoDeIntegrantes().isEmpty());
		TransaccionAsignacionDeCancion transaccion = recital.contratarArtistasParaUnaCancion(indexCancion2);
		assertFalse(transaccion.esTransaccionCommitted());
		assertTrue(transaccion.sePuedenEntrenarArtistasSuficientes());
		transaccion.entrenarArtistasRecomendadosYAsignarLosCandidatos(TransaccionAsignacionDeCancion.NO);
		assertEquals(expectedCuposDisponibles, cancion2.getCantDeCuposDisponibles());
		assertTrue(cancion2.getListadoDeIntegrantes().isEmpty());
	}

	@Test
	void contratacionExitosaDeArtistasMezcladosParaUnaCancion() {
		int expectedCuposDisponibles = 0;
		cancion2 = new Cancion("Román", new ArrayList<>(List.of(vozPrincipal, vozPrincipal, bajo, bateria, piano)));
		////		cancion5 = new Cancion("Crow", new ArrayList<>(List.of(vozPrincipal, bateria, bajo)));
		cancionero.addLast(cancion2);
		////		cancionero.addLast(cancion5);
		int indexCancion2 = 0;
		ArtistaBase bajistaYPianistaContratado = new ArtistaBase("Kamasi Washington",
				new ArrayList<>(List.of(bajo, piano)),
				new ArrayList<>(List.of(new BandaHistorico("Kamasi Washington"))));
//		ArtistaContratado bajistaYPianistaContratado = new ArtistaContratado("Kamasi Washington",
		////				new ArrayList<>(List.of(bajo, piano)),
////				new ArrayList<>(List.of(new BandaHistorico("Kamasi Washington"))), 5000, maxCanciones);
		lineUp = new ArrayList<>(List.of(bateristaContratado1, bajistaContratado1, cantanteContratado1, cantanteBase1,
				cantanteBase2, bajistaYPianistaContratado));
		recital = new Recital(cancionero, lineUp, roles);
		assertTrue(cancion2.getListadoDeIntegrantes().isEmpty());
		TransaccionAsignacionDeCancion transaccion = recital.contratarArtistasParaUnaCancion(indexCancion2);
		assertTrue(transaccion.esTransaccionCommitted());
		assertEquals(expectedCuposDisponibles, cancion2.getCantDeCuposDisponibles());
		assertFalse(cancion2.artistaEstaAsignado(cantanteContratado1));
		double expectedCosto = bateristaContratado1.getCosto() + bajistaContratado1.getCosto()+bajistaYPianistaContratado.getCosto();
		assertEquals(expectedCosto,cancion2.getCostoDeCancion());
	}

}
