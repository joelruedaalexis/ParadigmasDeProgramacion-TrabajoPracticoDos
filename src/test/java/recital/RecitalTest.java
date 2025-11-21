package recital;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import artista.ArtistaBase;
import artista.ArtistaContratado;
import artista.BandaHistorico;
import artista.ComparadorArtistaPorNombre;
import cancion.Cancion;

class RecitalTest {
	Recital recital;
	List<Cancion> repertorio;
	Set<String> roles;
	Cancion cancion,cancion1, cancion2, cancion3, cancion4, cancion5;
	final String vozPrincipal = "voz principal", vozSecundaria = "voz secundaria",
			guitarraElectrica = "guitarra eléctrica", armonica = "armónica", bateria = "batería", piano = "piano",
			bajo = "bajo", saxofon = "saxofón", acordeon = "acordeón";
	ArtistaBase cantanteBase, guitarristaBase, cantantePrincSecunBase;
	ArtistaContratado bateristaContratado, bajistaContratado, cantanteContratado;
	int maxCanciones;
	List<ArtistaBase> lineUp, lineUpArtistaBase, lineUpArtistaContratado;

	@BeforeEach
	void setUp() throws Exception {
		roles = new HashSet<>(Arrays.asList(vozPrincipal, vozSecundaria, guitarraElectrica, armonica, bateria, piano,
				bajo, saxofon, acordeon));
		cancion1 = Cancion.crearCancionSinIntegrantesAsignados("Hábil",
				new ArrayList<>(List.of(vozPrincipal, vozSecundaria, guitarraElectrica)));
		BandaHistorico redondos = new BandaHistorico("Patricio Rey y sus Redonditos de Ricota");
		cantanteBase = new ArtistaBase("Carlos Alberto Solari", new ArrayList<>(Arrays.asList(vozPrincipal)),
				List.of(redondos, new BandaHistorico("Los Fundamentalistas del Aire Acondicionado")));
		guitarristaBase = new ArtistaBase("Eduardo Beilinson", new ArrayList<>(List.of(guitarraElectrica)),
				List.of(redondos));
		cantantePrincSecunBase = new ArtistaBase("Agustin Cruz", new ArrayList<>(List.of(vozPrincipal, vozSecundaria)),
				List.of(new BandaHistorico("Acru")));
		lineUpArtistaBase = new ArrayList<>(List.of(cantanteBase, guitarristaBase, cantantePrincSecunBase));

		cancion2 = Cancion.crearCancionSinIntegrantesAsignados("Who's Back",
				new ArrayList<>(List.of(vozPrincipal, bateria, bajo)));
		maxCanciones = 2;
		bateristaContratado = new ArtistaContratado("Walter Sidotti", new ArrayList<>(List.of(bateria)),
				List.of(redondos), 3500, maxCanciones);
		BandaHistorico sodaStereo = new BandaHistorico("Soda Stereo");
		bajistaContratado = new ArtistaContratado("Zeta Bosio", new ArrayList<>(List.of(bajo)), List.of(sodaStereo),
				5000, maxCanciones);
		cantanteContratado = new ArtistaContratado("Gustavo Cerati", new ArrayList<>(List.of(vozPrincipal)),
				List.of(sodaStereo), 5000, maxCanciones);
		repertorio = new ArrayList<>();
	}

//	rolesFaltantasParaCancion = 1
	@Test
	void faltanTodosLosRolesParaUnaCancion() {
		cancion = Cancion.crearCancionSinIntegrantesAsignados("Crawling",
				new ArrayList<>(List.of(vozPrincipal, bajo, bateria, vozPrincipal)));
		repertorio.addLast(cancion);
		lineUp = new ArrayList<>(List.of(cantanteBase, cantanteContratado, bajistaContratado, bateristaContratado));
		recital = new Recital(repertorio, lineUp, roles);
		int index = 0;
		Map<String, Integer> expectedRolesFaltantes = new HashMap<>();
		expectedRolesFaltantes.put(vozPrincipal, 2);
		expectedRolesFaltantes.put(bajo, 1);
		expectedRolesFaltantes.put(bateria, 1);
		assertEquals(expectedRolesFaltantes, recital.cantDeRolesFaltantesParaUnaCancion(index));
	}

	@Test
	void noFaltanNingunRolParaUnaCancion() {
		cancion = Cancion.crearCancionSinIntegrantesAsignados("Crawling",
				new ArrayList<>(List.of(vozPrincipal, bajo, bateria, vozPrincipal)));
		repertorio.addLast(cancion);
		lineUp = new ArrayList<>(List.of(cantanteBase, cantanteContratado, bajistaContratado, bateristaContratado));
		recital = new Recital(repertorio, lineUp, roles);
		int index = 0;
		assertTrue(recital.contratarArtistasParaUnaCancion(index).esTransaccionCommitted());
		Map<String, Integer> expectedRolesFaltantes = new HashMap<>();
		assertEquals(expectedRolesFaltantes, recital.cantDeRolesFaltantesParaUnaCancion(index));
		assertTrue(recital.cantDeRolesFaltantesParaUnaCancion(index).isEmpty());
	}

	@Test
	void faltaUnRolParaUnaCancion() {
		cancion = Cancion.crearCancionSinIntegrantesAsignados("Crawling",
				new ArrayList<>(List.of(vozPrincipal, bajo, bateria, vozPrincipal)));
		repertorio.addLast(cancion);
		lineUp = new ArrayList<>(List.of(cantanteBase, cantanteContratado, bajistaContratado, bateristaContratado));
		recital = new Recital(repertorio, lineUp, roles);
		int index = 0;
		assertTrue(recital.contratarArtistasParaUnaCancion(index).esTransaccionCommitted());
		recital.quitarArtistaDeCancion(index, index);
		Map<String, Integer> expectedRolesFaltantes = new HashMap<>();
		expectedRolesFaltantes.put(vozPrincipal, 1);
		assertEquals(expectedRolesFaltantes, recital.cantDeRolesFaltantesParaUnaCancion(index));
	}

	@Test
	void noSePuedeSaberCuantosRolesFaltanPorIndiceInvalido() {
		cancion = Cancion.crearCancionSinIntegrantesAsignados("Crawling",
				new ArrayList<>(List.of(vozPrincipal, bajo, bateria, vozPrincipal)));
		repertorio.addLast(cancion);
		lineUp = new ArrayList<>(List.of(cantanteBase, cantanteContratado, bajistaContratado, bateristaContratado));
		recital = new Recital(repertorio, lineUp, roles);
		int indexNegativo = -1, indexFueraDelLimiteSUperior = Integer.MAX_VALUE;
		assertThrows(IllegalArgumentException.class, () -> recital.cantDeRolesFaltantesParaUnaCancion(indexNegativo));
		assertThrows(IllegalArgumentException.class,
				() -> recital.cantDeRolesFaltantesParaUnaCancion(indexFueraDelLimiteSUperior));
	}

//	rolesFaltantesParaTodasLasCanciones = 2,
	@Test
	void faltanTodosLosRolesParaTodasLasCancionesPorNoTenerNingunArtistaBases() {
		cancion1 = Cancion.crearCancionSinIntegrantesAsignados("Crawling",
				new ArrayList<>(List.of(vozPrincipal, bajo, bateria, vozPrincipal)));
		cancion2 = Cancion.crearCancionSinIntegrantesAsignados("La Casa del Sol Naciente",
				new ArrayList<>(List.of(vozPrincipal, bajo, bateria, vozSecundaria)));
		repertorio.addLast(cancion1);
		repertorio.addLast(cancion2);
		lineUp = new ArrayList<>(List.of(cantanteContratado, bajistaContratado, bateristaContratado));
		recital = new Recital(repertorio, lineUp, roles);
		Map<String, Integer> expectedRolesFaltantes = new HashMap<>();
		expectedRolesFaltantes.put(vozPrincipal, 3);
		expectedRolesFaltantes.put(bajo, 2);
		expectedRolesFaltantes.put(bateria, 2);
		expectedRolesFaltantes.put(vozSecundaria, 1);
		assertEquals(expectedRolesFaltantes, recital.cantDeRolesFaltantesParaTodasLasCanciones(),
				"Al no haber artistas bases en el line up no podremos completar los roles faltantes con ellos.");
	}

	@Test
	void estanTodosLosRolesCubiertosEnTodasLasCancionesEnLineUpSoloConArtistasBases() {
		cancion1 = Cancion.crearCancionSinIntegrantesAsignados("Crawling",
				new ArrayList<>(List.of(vozPrincipal, guitarraElectrica, vozSecundaria)));
		cancion2 = Cancion.crearCancionSinIntegrantesAsignados("Heavy is the Crown",
				new ArrayList<>(List.of(vozPrincipal, guitarraElectrica, vozPrincipal)));
		repertorio.addLast(cancion1);
		repertorio.addLast(cancion2);
		lineUp = new ArrayList<>(List.of(cantantePrincSecunBase, cantanteBase, guitarristaBase));
		recital = new Recital(repertorio, lineUp, roles);
		Map<String, Integer> expectedRolesFaltantes = new HashMap<>();
		assertEquals(expectedRolesFaltantes, recital.cantDeRolesFaltantesParaTodasLasCanciones(),
				"Todos los roles deberian estár cubiertos pos los 3 artistas bases.");
		assertTrue(recital.cantDeRolesFaltantesParaTodasLasCanciones().isEmpty());
	}

	@Test
	void estanTodosLosRolesCubiertosEnTodasLasCancionesEnLineUpMezcladoConArtistasBasesYContratados() {
		cancion1 = Cancion.crearCancionSinIntegrantesAsignados("Crawling",
				new ArrayList<>(List.of(vozPrincipal, guitarraElectrica, vozSecundaria)));
		cancion2 = Cancion.crearCancionSinIntegrantesAsignados("Heavy is the Crown",
				new ArrayList<>(List.of(vozPrincipal, guitarraElectrica, vozPrincipal)));
		repertorio.addLast(cancion1);
		repertorio.addLast(cancion2);
		lineUp = new ArrayList<>(
				List.of(cantantePrincSecunBase, cantanteContratado, cantanteBase, bajistaContratado, guitarristaBase));
		recital = new Recital(repertorio, lineUp, roles);
		Map<String, Integer> expectedRolesFaltantes = new HashMap<>();
		assertEquals(expectedRolesFaltantes, recital.cantDeRolesFaltantesParaTodasLasCanciones(),
				"Todos los roles deberian estár cubiertos pos los 3 artistas bases.");
		assertTrue(recital.cantDeRolesFaltantesParaTodasLasCanciones().isEmpty());
	}

	@Test
	void noEstanTodosLosRolesCubiertosEnTodasLasCancionesPorNoTenerBajistasNiSaxofonistasBases() {
		cancion1 = Cancion.crearCancionSinIntegrantesAsignados("Crawling",
				new ArrayList<>(List.of(vozPrincipal, guitarraElectrica, vozSecundaria, bajo)));
		cancion2 = Cancion.crearCancionSinIntegrantesAsignados("Heavy is the Crown",
				new ArrayList<>(List.of(vozPrincipal, guitarraElectrica, vozPrincipal, saxofon)));
		cancion3 = Cancion.crearCancionSinIntegrantesAsignados("Signo Marte",
				new ArrayList<>(List.of(vozPrincipal, guitarraElectrica, vozSecundaria, bajo)));
		cancion4 = Cancion.crearCancionSinIntegrantesAsignados("Broken Relief",
				new ArrayList<>(List.of(vozPrincipal, guitarraElectrica, vozPrincipal, saxofon)));
		repertorio.addLast(cancion1);
		repertorio.addLast(cancion2);
		repertorio.addLast(cancion3);
		repertorio.addLast(cancion4);
		lineUp = new ArrayList<>(
				List.of(cantantePrincSecunBase, cantanteContratado, cantanteBase, bajistaContratado, guitarristaBase));
		recital = new Recital(repertorio, lineUp, roles);
		Map<String, Integer> expectedRolesFaltantes = new HashMap<>();
		expectedRolesFaltantes.put(bajo, 2);
		expectedRolesFaltantes.put(saxofon, 2);
		assertEquals(expectedRolesFaltantes, recital.cantDeRolesFaltantesParaTodasLasCanciones(),
				"Todos los roles deberian estár cubiertos pos los 3 artistas bases.");
	}

	@Test
	void estanTodosLosRolesCubiertosEnTodasLasCancionesQueSeLesQuitóArtistas() {
		cancion1 = Cancion.crearCancionSinIntegrantesAsignados("Crawling",
				new ArrayList<>(List.of(vozPrincipal, guitarraElectrica, vozSecundaria)));
		cancion2 = Cancion.crearCancionSinIntegrantesAsignados("Heavy is the Crown",
				new ArrayList<>(List.of(vozPrincipal, guitarraElectrica, vozPrincipal)));
		cancion3 = Cancion.crearCancionSinIntegrantesAsignados("Signo Marte",
				new ArrayList<>(List.of(vozPrincipal, guitarraElectrica, vozSecundaria)));
		cancion4 = Cancion.crearCancionSinIntegrantesAsignados("Broken Relief",
				new ArrayList<>(List.of(vozPrincipal, guitarraElectrica, vozPrincipal)));
		repertorio.addLast(cancion1);
		repertorio.addLast(cancion2);
		repertorio.addLast(cancion3);
		repertorio.addLast(cancion4);
		lineUp = new ArrayList<>(
				List.of(cantantePrincSecunBase, cantanteContratado, cantanteBase, bajistaContratado, guitarristaBase));
		recital = new Recital(repertorio, lineUp, roles);

		recital.quitarArtistaDeTodasLasCanciones(guitarristaBase.getNombre());
		recital.quitarArtistaDeTodasLasCanciones(cantantePrincSecunBase.getNombre());

		Map<String, Integer> expectedRolesFaltantes = new HashMap<>();
		assertEquals(expectedRolesFaltantes, recital.cantDeRolesFaltantesParaTodasLasCanciones());
		assertTrue(recital.cantDeRolesFaltantesParaTodasLasCanciones().isEmpty());
	}

//	contratarArtistasParaUnaCancion = 3
	@Test
	void contratacionExitosaDeArtistasBaseParaUnaCancion() {
		repertorio.addLast(cancion1);
		recital = new Recital(repertorio, lineUpArtistaBase, roles);
		int indexCancion1 = repertorio.indexOf(cancion1);
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
		repertorio.addLast(cancion2);
		recital = new Recital(repertorio, lineUpArtistaBase, roles);
		int indexCancion2 = repertorio.indexOf(cancion2);
		TransaccionAsignacionDeCancion transaccion = recital.contratarArtistasParaUnaCancion(indexCancion2);
		assertFalse(transaccion.esTransaccionCommitted());
		assertEquals(EstadoDeTransaccion.CANCELADA, transaccion.getEstadoDeTransaccion());
		assertFalse(transaccion.sePuedenEntrenarArtistasSuficientes());
	}

	@Test
	void contratacionExitosaDeArtistasContratadosParaUnaCancion() {
		repertorio.addLast(cancion2);
		lineUpArtistaContratado = new ArrayList<>(List.of(bateristaContratado, bajistaContratado, cantanteContratado));
		recital = new Recital(repertorio, lineUpArtistaContratado, roles);
		int indexCancion2 = repertorio.indexOf(cancion2);
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
		cancion3 = Cancion.crearCancionSinIntegrantesAsignados("Román",
				new ArrayList<>(List.of(vozPrincipal, bateria, bajo)));
		cancion4 = Cancion.crearCancionSinIntegrantesAsignados("Monoblock",
				new ArrayList<>(List.of(vozPrincipal, bateria, bajo)));
		repertorio.addLast(cancion2);
		repertorio.addLast(cancion3);
		repertorio.addLast(cancion4);
		int indexCancion1 = 0, indexCancion2 = 1, indexCancion3 = 2;
		lineUpArtistaContratado = new ArrayList<>(List.of(bateristaContratado, bajistaContratado, cantanteContratado));
		recital = new Recital(repertorio, lineUpArtistaContratado, roles);
		assertTrue(recital.contratarArtistasParaUnaCancion(indexCancion1).esTransaccionCommitted());
		assertTrue(recital.contratarArtistasParaUnaCancion(indexCancion2).esTransaccionCommitted());
		TransaccionAsignacionDeCancion transaccion = recital.contratarArtistasParaUnaCancion(indexCancion3);
		assertFalse(transaccion.esTransaccionCommitted());
		assertFalse(transaccion.sePuedenEntrenarArtistasSuficientes());
		assertEquals(expectedCuposDisponibles, cancion4.getCantDeCuposDisponibles());

	}

	@Test
	void contratacionExitosaDeArtistasParaUnaCancionTeniendoQueHaberEntrenadoRolesFaltantes() {
		cancion2 = Cancion.crearCancionSinIntegrantesAsignados("Román",
				new ArrayList<>(List.of(vozPrincipal, piano, piano, armonica, vozSecundaria)));
		repertorio.addLast(cancion2);
		int indexCancion2 = 0;
		lineUp = new ArrayList<>(
				List.of(bateristaContratado, bajistaContratado, cantanteContratado, cantanteBase, bajistaContratado));
		recital = new Recital(repertorio, lineUp, roles);
		assertTrue(cancion2.getListadoDeIntegrantes().isEmpty());
		TransaccionAsignacionDeCancion transaccion = recital.contratarArtistasParaUnaCancion(indexCancion2);
		assertFalse(transaccion.esTransaccionCommitted());
		assertTrue(transaccion.sePuedenEntrenarArtistasSuficientes());
		transaccion.entrenarArtistasRecomendadosYAsignarLosCandidatos(OpcionDeTransaccion.SI);
		int expectedCuposDisponibles = 0;
		assertEquals(expectedCuposDisponibles, cancion2.getCantDeCuposDisponibles());
	}

	@Test
	void contratacionFallidaDeArtistasParaUnaCancionPorqueNoSeDeseaAEntrenarArtistas() {
		int expectedCuposDisponibles = 5;
		cancion2 = Cancion.crearCancionSinIntegrantesAsignados("Román",
				new ArrayList<>(List.of(vozPrincipal, piano, piano, armonica, vozSecundaria)));
		repertorio.addLast(cancion2);
		int indexCancion2 = 0;
		lineUp = new ArrayList<>(
				List.of(bateristaContratado, bajistaContratado, cantanteContratado, cantanteBase, bajistaContratado));
		recital = new Recital(repertorio, lineUp, roles);
		assertTrue(cancion2.getListadoDeIntegrantes().isEmpty());
		TransaccionAsignacionDeCancion transaccion = recital.contratarArtistasParaUnaCancion(indexCancion2);
		assertFalse(transaccion.esTransaccionCommitted());
		assertTrue(transaccion.sePuedenEntrenarArtistasSuficientes());
		transaccion.entrenarArtistasRecomendadosYAsignarLosCandidatos(OpcionDeTransaccion.NO);
		assertEquals(expectedCuposDisponibles, cancion2.getCantDeCuposDisponibles());
		assertTrue(cancion2.getListadoDeIntegrantes().isEmpty());
		assertThrows(IllegalStateException.class,
				() -> transaccion.entrenarArtistasRecomendadosYAsignarLosCandidatos(OpcionDeTransaccion.SI));
	}

	@Test
	void contratacionExitosaDeArtistasMezcladosParaUnaCancion() {
		int expectedCuposDisponibles = 0;
		cancion2 = Cancion.crearCancionSinIntegrantesAsignados("Román",
				new ArrayList<>(List.of(vozPrincipal, vozPrincipal, bajo, bateria, piano)));
		repertorio.addLast(cancion2);
		int indexCancion2 = 0;
		ArtistaBase bajistaYPianistaContratado = new ArtistaBase("Kamasi Washington",
				new ArrayList<>(List.of(bajo, piano)),
				new ArrayList<>(List.of(new BandaHistorico("Kamasi Washington"))));
		lineUp = new ArrayList<>(List.of(bateristaContratado, bajistaContratado, cantanteContratado, cantanteBase,
				cantantePrincSecunBase, bajistaYPianistaContratado));
		recital = new Recital(repertorio, lineUp, roles);
		assertTrue(cancion2.getListadoDeIntegrantes().isEmpty());
		TransaccionAsignacionDeCancion transaccion = recital.contratarArtistasParaUnaCancion(indexCancion2);
		assertTrue(transaccion.esTransaccionCommitted());
		assertEquals(expectedCuposDisponibles, cancion2.getCantDeCuposDisponibles());
		assertFalse(cancion2.artistaEstaAsignado(cantanteContratado));
		double expectedCosto = bateristaContratado.getCosto() + bajistaContratado.getCosto()
				+ bajistaYPianistaContratado.getCosto();
		assertEquals(expectedCosto, cancion2.getCostoDeCancion());
	}

	@Test
	void contratacionExitosaDeManeraEficienteDeArtistasMezcladosParaUnaCancion() {
		int expectedCuposDisponibles = 0;
		cancion2 = Cancion.crearCancionSinIntegrantesAsignados("Román",
				new ArrayList<>(List.of(vozPrincipal, vozPrincipal, bajo, bateria, piano, saxofon)));
		repertorio.addLast(cancion2);
		int indexCancion2 = 0;
		ArtistaBase bajistaYPianistaContratado = new ArtistaBase("Kamasi Washington",
				new ArrayList<>(List.of(bajo, piano)),
				new ArrayList<>(List.of(new BandaHistorico("Kamasi Washington"))));
		ArtistaContratado saxofonistaBarato = new ArtistaContratado("Lisa Simpsons",
				new ArrayList<>(List.of(saxofon, armonica)),
				new ArrayList<>(List.of(new BandaHistorico("Lisa Simpsons"))), 5000, maxCanciones);
		ArtistaContratado saxofonistaCaro = new ArtistaContratado("Bleeding Gums Murphy",
				new ArrayList<>(List.of(saxofon, armonica)),
				new ArrayList<>(List.of(new BandaHistorico("Bleeding Gums Murphy"))), 99999999, maxCanciones);

		lineUp = new ArrayList<>(List.of(bateristaContratado, bajistaContratado, cantanteContratado, cantanteBase,
				cantantePrincSecunBase, bajistaYPianistaContratado, saxofonistaCaro, saxofonistaBarato));
		recital = new Recital(repertorio, lineUp, roles);
		assertTrue(cancion2.getListadoDeIntegrantes().isEmpty());
		TransaccionAsignacionDeCancion transaccion = recital.contratarArtistasParaUnaCancion(indexCancion2);
		assertTrue(transaccion.esTransaccionCommitted());
		assertEquals(expectedCuposDisponibles, cancion2.getCantDeCuposDisponibles());
		assertFalse(cancion2.artistaEstaAsignado(cantanteContratado));
		assertFalse(cancion2.artistaEstaAsignado(saxofonistaCaro));
		assertTrue(cancion2.artistaEstaAsignado(saxofonistaBarato));
		double expectedCosto = bateristaContratado.getCosto() + bajistaContratado.getCosto()
				+ bajistaYPianistaContratado.getCosto() + saxofonistaBarato.getCosto();
		assertEquals(expectedCosto, cancion2.getCostoDeCancion());
	}

//	contratarArtistasParaTodasLasCanciones = 4
	@Test
	void contratacionExitosaDeTodasLasCancionesPorArtistasBases() {
		int expectedCuposDisponibles = 0;
		cancion2 = Cancion.crearCancionSinIntegrantesAsignados("Román",
				new ArrayList<>(List.of(vozPrincipal, vozPrincipal, guitarraElectrica)));
		cancion3 = Cancion.crearCancionSinIntegrantesAsignados("220",
				new ArrayList<>(List.of(vozPrincipal, vozSecundaria, guitarraElectrica)));
		repertorio.addLast(cancion2);
		repertorio.addLast(cancion3);
		lineUp = new ArrayList<>(List.of(cantantePrincSecunBase, guitarristaBase, cantanteBase));
		recital = new Recital(repertorio, lineUp, roles);
		assertTrue(cancion2.getListadoDeIntegrantes().isEmpty());
		TransaccionAsignacionDeTodasLasCanciones transaccion = recital.contratarArtistasParaTodasLasCanciones();
		assertNotNull(transaccion);
		assertTrue(transaccion.esTransaccionCommitted());
		assertEquals(expectedCuposDisponibles, cancion2.getCantDeCuposDisponibles());
		assertEquals(expectedCuposDisponibles, cancion3.getCantDeCuposDisponibles());
		double expectedCosto = 0;
		assertEquals(expectedCosto, cancion2.getCostoDeCancion());
		assertEquals(expectedCosto, cancion3.getCostoDeCancion());
	}

	@Test
	void contratacionExitosaDeTodasLasCancionesPorArtistasContratados() {
		ArtistaBase bajistaYPianistaContratado = new ArtistaBase("Kamasi Washington",
				new ArrayList<>(List.of(bajo, piano)),
				new ArrayList<>(List.of(new BandaHistorico("Kamasi Washington"))));
		int expectedCuposDisponibles = 0;
		cancion2 = Cancion.crearCancionSinIntegrantesAsignados("Román",
				new ArrayList<>(List.of(bajo, piano, vozPrincipal, bateria)));
		cancion3 = Cancion.crearCancionSinIntegrantesAsignados("220",
				new ArrayList<>(List.of(bajo, bajo, vozPrincipal, bateria)));
		repertorio.addLast(cancion2);
		repertorio.addLast(cancion3);
		ArtistaContratado saxofonistaBarato = new ArtistaContratado("Lisa Simpsons",
				new ArrayList<>(List.of(saxofon, armonica)),
				new ArrayList<>(List.of(new BandaHistorico("Lisa Simpsons"))), 5000, maxCanciones);
		ArtistaContratado saxofonistaCaro = new ArtistaContratado("Bleeding Gums Murphy",
				new ArrayList<>(List.of(saxofon, armonica)),
				new ArrayList<>(List.of(new BandaHistorico("Bleeding Gums Murphy"))), 99999999, maxCanciones);
		lineUp = new ArrayList<>(List.of(bajistaYPianistaContratado, bajistaContratado, cantanteContratado,
				bateristaContratado, saxofonistaCaro, saxofonistaBarato));
		recital = new Recital(repertorio, lineUp, roles);
		assertTrue(cancion2.getListadoDeIntegrantes().isEmpty());
		TransaccionAsignacionDeTodasLasCanciones transaccion = recital.contratarArtistasParaTodasLasCanciones();
		assertNotNull(transaccion);
		assertTrue(transaccion.esTransaccionCommitted());
		assertEquals(expectedCuposDisponibles, cancion2.getCantDeCuposDisponibles());
		assertEquals(expectedCuposDisponibles, cancion3.getCantDeCuposDisponibles());
		double expectedCosto = bajistaYPianistaContratado.getCosto() + bajistaContratado.getCosto()
				+ cantanteContratado.getCosto() + bateristaContratado.getCosto();
		assertEquals(expectedCosto, cancion2.getCostoDeCancion());
		assertEquals(expectedCosto, cancion3.getCostoDeCancion());
	}

	@Test
	void contratacionExitosaDeTodasLasCancionesPorEntrenarAArtistasContratados() {
		ArtistaBase bajistaYPianistaContratado = new ArtistaBase("Kamasi Washington",
				new ArrayList<>(List.of(bajo, piano)),
				new ArrayList<>(List.of(new BandaHistorico("Kamasi Washington"))));
		int expectedCuposDisponibles = 0;
		cancion2 = Cancion.crearCancionSinIntegrantesAsignados("Román", new ArrayList<>(List.of(armonica, saxofon)));
		cancion3 = Cancion.crearCancionSinIntegrantesAsignados("220",
				new ArrayList<>(List.of(piano, armonica, saxofon, bateria)));
		repertorio.addLast(cancion2);
		repertorio.addLast(cancion3);
		lineUp = new ArrayList<>(
				List.of(bajistaContratado, cantanteContratado, bateristaContratado, bajistaYPianistaContratado));
		recital = new Recital(repertorio, lineUp, roles);
		assertTrue(cancion2.getListadoDeIntegrantes().isEmpty());
		TransaccionAsignacionDeTodasLasCanciones transaccion = recital.contratarArtistasParaTodasLasCanciones();
		assertTrue(transaccion.sePuedenEntrenarParaTodosLosRoles(), "SARASa");
		transaccion.entrenarArtistasRecomendadosYAsignarLosCandidatos(OpcionDeTransaccion.SI);
		assertTrue(transaccion.esTransaccionCommitted());
		assertEquals(expectedCuposDisponibles, cancion2.getCantDeCuposDisponibles());
		assertEquals(expectedCuposDisponibles, cancion3.getCantDeCuposDisponibles());
	}

//	quitarArtista
	@Test
	void sePuedeQuitarArtistaBaseDeUnaCancion() {
		int expectedCuposDisponibles = 1;
		cancion2 = Cancion.crearCancionSinIntegrantesAsignados("Crawling",
				new ArrayList<>(List.of(vozPrincipal, vozPrincipal, bajo, bateria)));
		repertorio.addLast(cancion2);
		int indexCancion = 0, indexArtista = 0;
		lineUp = new ArrayList<>(List.of(cantanteBase, cantanteContratado, bajistaContratado, bateristaContratado));
		recital = new Recital(repertorio, lineUp, roles);
		assertTrue(recital.contratarArtistasParaUnaCancion(indexCancion).esTransaccionCommitted());
		ArtistaBase expectedArtistaQuitado = cancion2.getListadoDeIntegrantes().getFirst();
		assertTrue(cancion2.artistaEstaAsignado(expectedArtistaQuitado));
		recital.quitarArtistaDeCancion(indexArtista, indexCancion);
		assertFalse(cancion2.artistaEstaAsignado(expectedArtistaQuitado));
		assertEquals(expectedCuposDisponibles, cancion2.getCantDeCuposDisponibles());
	}

	@Test
	void sePuedeQuitarArtistaContratadoDeUnaCancion() {
		int expectedCuposDisponibles = 1;
		cancion2 = Cancion.crearCancionSinIntegrantesAsignados("Crawling",
				new ArrayList<>(List.of(vozPrincipal, vozPrincipal, bajo, bateria)));
		repertorio.addLast(cancion2);
		int indexCancion = 0, indexArtista;
		lineUp = new ArrayList<>(List.of(cantanteBase, cantanteContratado, bajistaContratado, bateristaContratado));
		recital = new Recital(repertorio, lineUp, roles);
		double expectedCostoSinCantanteContratado = bajistaContratado.getCosto() + bateristaContratado.getCosto();
		assertTrue(recital.contratarArtistasParaUnaCancion(indexCancion).esTransaccionCommitted());
		indexArtista = cancion2.getListadoDeIntegrantes().indexOf(cantanteContratado);
		ArtistaBase expectedArtistaQuitado = cantanteContratado;
		assertTrue(cancion2.artistaEstaAsignado(expectedArtistaQuitado));
		assertEquals(expectedCostoSinCantanteContratado + cantanteContratado.getCosto(), cancion2.getCostoDeCancion());
		recital.quitarArtistaDeCancion(indexArtista, indexCancion);
		assertFalse(cancion2.artistaEstaAsignado(expectedArtistaQuitado));
		assertEquals(expectedCuposDisponibles, cancion2.getCantDeCuposDisponibles());
		assertEquals(expectedCostoSinCantanteContratado, cancion2.getCostoDeCancion());
	}

	@Test
	void noSePuedeQuitarArtistaDeUnaCancionIngresandoUnIndiceInvalido() {
		cancion2 = Cancion.crearCancionSinIntegrantesAsignados("Crawling",
				new ArrayList<>(List.of(vozPrincipal, vozPrincipal, bajo, bateria)));
		repertorio.addLast(cancion2);
		lineUp = new ArrayList<>(List.of(cantanteBase, cantanteContratado, bajistaContratado, bateristaContratado));
		recital = new Recital(repertorio, lineUp, roles);
		int indexCancion = 0, indexArtista = 0;
		assertTrue(recital.contratarArtistasParaUnaCancion(indexCancion).esTransaccionCommitted());
		int indexNegativoInvalido = -1, indexInvalidoCancion = Integer.MAX_VALUE,
				indexInvalidoArtista = Integer.MAX_VALUE;
		assertThrows(IllegalArgumentException.class,
				() -> recital.quitarArtistaDeCancion(indexArtista, indexNegativoInvalido));
		assertThrows(IllegalArgumentException.class,
				() -> recital.quitarArtistaDeCancion(indexNegativoInvalido, indexCancion));
		assertThrows(IllegalArgumentException.class,
				() -> recital.quitarArtistaDeCancion(indexArtista, indexInvalidoCancion));
		assertThrows(IllegalArgumentException.class,
				() -> recital.quitarArtistaDeCancion(indexInvalidoArtista, indexCancion));
	}

	@Test
	void noSePuedeQuitarArtistaDeTodasLasCancionesIngresandoUnNombreNull() {
		cancion2 = Cancion.crearCancionSinIntegrantesAsignados("Crawling",
				new ArrayList<>(List.of(vozPrincipal, vozPrincipal, bajo, bateria)));
		repertorio.addLast(cancion2);
		lineUp = new ArrayList<>(List.of(cantanteBase, cantanteContratado, bajistaContratado, bateristaContratado));
		recital = new Recital(repertorio, lineUp, roles);
		int indexCancion = 0;
		assertTrue(recital.contratarArtistasParaUnaCancion(indexCancion).esTransaccionCommitted());
		assertThrows(IllegalArgumentException.class, () -> recital.quitarArtistaDeTodasLasCanciones(null));
	}

	@Test
	void noSePuedeQuitarArtistaDeTodasLasCancionesSiNoExisteEnElLineUp() {
		cancion1 = Cancion.crearCancionSinIntegrantesAsignados("Crawling",
				new ArrayList<>(List.of(vozPrincipal, bajo, bateria)));
		cancion2 = Cancion.crearCancionSinIntegrantesAsignados("Crawling",
				new ArrayList<>(List.of(vozPrincipal, bajo, bateria)));
		repertorio.addLast(cancion1);
		repertorio.addLast(cancion2);
		lineUp = new ArrayList<>(List.of(cantanteBase, cantanteContratado, bajistaContratado, bateristaContratado));
		recital = new Recital(repertorio, lineUp, roles);

		assertThrows(RuntimeException.class, () -> recital.quitarArtistaDeTodasLasCanciones("No soy un artista"));
	}

	@Test
	void noSePuedeQuitarArtistaDeTodasLasCancionesSiNoEstaAsignadoANinguna() {
		cancion1 = Cancion.crearCancionSinIntegrantesAsignados("Crawling",
				new ArrayList<>(List.of(vozPrincipal, bajo, bateria)));
		cancion2 = Cancion.crearCancionSinIntegrantesAsignados("Breaking the Habit",
				new ArrayList<>(List.of(vozPrincipal, bajo, bateria)));
		repertorio.addLast(cancion1);
		repertorio.addLast(cancion2);
		lineUp = new ArrayList<>(List.of(cantanteBase, cantanteContratado, bajistaContratado, bateristaContratado));
		recital = new Recital(repertorio, lineUp, roles);

		int indexCancion1 = 0, indexCancion2 = 1;
		assertTrue(recital.contratarArtistasParaUnaCancion(indexCancion1).esTransaccionCommitted());
		assertTrue(recital.contratarArtistasParaUnaCancion(indexCancion2).esTransaccionCommitted());
		assertFalse(recital.quitarArtistaDeTodasLasCanciones(cantanteContratado.getNombre()));
	}

	@Test
	void sePuedeQuitarArtistaDeTodasLasCanciones() {
		cancion1 = Cancion.crearCancionSinIntegrantesAsignados("Crawling",
				new ArrayList<>(List.of(vozPrincipal, bajo, bateria)));
		cancion2 = Cancion.crearCancionSinIntegrantesAsignados("Eres Un@ Mas",
				new ArrayList<>(List.of(vozPrincipal, bajo, bateria)));
		repertorio.addLast(cancion1);
		repertorio.addLast(cancion2);
		lineUp = new ArrayList<>(List.of(cantanteBase, cantanteContratado, bajistaContratado, bateristaContratado));
		recital = new Recital(repertorio, lineUp, roles);
		int indexCancion1 = 0, indexCancion2 = 1;
		assertTrue(recital.contratarArtistasParaUnaCancion(indexCancion1).esTransaccionCommitted());
		assertTrue(recital.contratarArtistasParaUnaCancion(indexCancion2).esTransaccionCommitted());
		assertEquals(repertorio.size(), cantanteBase.getListaDeCancionesEnLasQueEstaAsignado().size());
		assertTrue(cantanteBase.getListaDeCancionesEnLasQueEstaAsignado().containsAll(repertorio));
		assertTrue(repertorio.containsAll(cantanteBase.getListaDeCancionesEnLasQueEstaAsignado()));
		assertTrue(cantanteBase.estaAsignadoAlmenosAUnaCancion());
		assertTrue(recital.quitarArtistaDeTodasLasCanciones(cantanteBase.getNombre()));
		assertFalse(cantanteBase.estaAsignadoAlmenosAUnaCancion());
	}

	@Test
	void sePuedeQuitarArtistaContratadoDelLineUp() {
		cancion1 = Cancion.crearCancionSinIntegrantesAsignados("Crawling",
				new ArrayList<>(List.of(vozPrincipal, bajo, bateria)));
		cancion2 = Cancion.crearCancionSinIntegrantesAsignados("Eres Un@ Mas",
				new ArrayList<>(List.of(vozPrincipal, bajo, bateria)));
		repertorio.addLast(cancion1);
		repertorio.addLast(cancion2);
		lineUp = new ArrayList<>(List.of(cantanteBase, cantanteContratado, bajistaContratado, bateristaContratado));
		recital = new Recital(repertorio, lineUp, roles);

		int index = lineUp.indexOf(bajistaContratado), indexCancion1 = 0, indexCancion2 = 1;
		assertTrue(recital.contratarArtistasParaUnaCancion(indexCancion1).esTransaccionCommitted());
		assertTrue(recital.contratarArtistasParaUnaCancion(indexCancion2).esTransaccionCommitted());

		assertEquals(repertorio.size(), cantanteBase.getListaDeCancionesEnLasQueEstaAsignado().size());
		assertTrue(repertorio.containsAll(cantanteBase.getListaDeCancionesEnLasQueEstaAsignado()));
		assertTrue(cantanteBase.getListaDeCancionesEnLasQueEstaAsignado().containsAll(repertorio));
		assertTrue(bajistaContratado.estaAsignadoAlmenosAUnaCancion());
		assertTrue(recital.quitarArtistaDelLineUp(index));
		assertFalse(bajistaContratado.estaAsignadoAlmenosAUnaCancion());
		assertFalse(lineUp.contains(bajistaContratado));
	}

	@Test
	void noSePuedeQuitarArtistaContratadoDelLineUpSiElIndiceEsInvalido() {
		cancion1 = Cancion.crearCancionSinIntegrantesAsignados("Crawling",
				new ArrayList<>(List.of(vozPrincipal, bajo, bateria)));
		repertorio.addLast(cancion1);
		lineUp = new ArrayList<>(List.of(cantanteBase, cantanteContratado, bajistaContratado, bateristaContratado));
		recital = new Recital(repertorio, lineUp, roles);

		int indexNegativo = -1, indexSuperiorAlLimiteSuperior = Integer.MAX_VALUE;
		assertThrows(RuntimeException.class, () -> recital.quitarArtistaDelLineUp(indexNegativo));
		assertThrows(RuntimeException.class, () -> recital.quitarArtistaDelLineUp(indexSuperiorAlLimiteSuperior));
	}

	@Test
	void noSePuedeQuitarArtistaBaseDelLineUp() {
		cancion1 = Cancion.crearCancionSinIntegrantesAsignados("Crawling",
				new ArrayList<>(List.of(vozPrincipal, bajo, bateria)));
		repertorio.addLast(cancion1);
		lineUp = new ArrayList<>(List.of(cantanteBase, cantanteContratado, bajistaContratado, bateristaContratado));
		recital = new Recital(repertorio, lineUp, roles);
		int index = lineUp.indexOf(cantanteBase);
		assertFalse(recital.quitarArtistaDelLineUp(index));
	}
}
