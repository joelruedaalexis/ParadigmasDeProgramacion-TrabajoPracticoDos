package recital;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import artista.ComparadorArtistaPorNombre;
import cancion.Cancion;

class RecitalTest {
	Recital recital;
	List<Cancion> repertorio;
	List<String> roles;
	List<ArtistaBase> lineUp;
	Cancion cancion1, cancion2, cancion3, cancion4, cancion5;
	final String vozPrincipal = "voz principal", vozSecundaria = "voz secundaria",
			guitarraElectrica = "guitarra eléctrica", armonica = "armónica", bateria = "batería", piano = "piano",
			bajo = "bajo", saxofon = "saxofón", acordeon = "acordeón";
	ArtistaBase cantanteBase1, guitarristaBase1;
	ArtistaBase cantantePrincSecunBase2;
	ArtistaContratado bateristaContratado1;
	ArtistaContratado bajistaContratado1;
	ArtistaContratado cantanteContratado1;
	int maxCanciones;
	List<ArtistaBase> lineUpArtistaBase;
	List<ArtistaBase> lineUpArtistaContratado;

	@BeforeEach
	void setUp() throws Exception {
		roles = new ArrayList<>(Arrays.asList(vozPrincipal, vozSecundaria, guitarraElectrica, armonica, bateria, piano,
				bajo, saxofon, acordeon));
		cancion1 = new Cancion("Hábil", new ArrayList<>(List.of(vozPrincipal, vozSecundaria, guitarraElectrica)));
		BandaHistorico redondos = new BandaHistorico("Patricio Rey y sus Redonditos de Ricota");
		cantanteBase1 = new ArtistaBase("Carlos Alberto Solari", new ArrayList<>(Arrays.asList(vozPrincipal)),
				List.of(redondos, new BandaHistorico("Los Fundamentalistas del Aire Acondicionado")));
		guitarristaBase1 = new ArtistaBase("Eduardo Beilinson", new ArrayList<>(List.of(guitarraElectrica)),
				List.of(redondos));
		cantantePrincSecunBase2 = new ArtistaBase("Agustin Cruz", new ArrayList<>(List.of(vozPrincipal, vozSecundaria)),
				List.of(new BandaHistorico("Acru")));
		lineUpArtistaBase = new ArrayList<>(List.of(cantanteBase1, guitarristaBase1, cantantePrincSecunBase2));

		cancion2 = new Cancion("Who's Back", new ArrayList<>(List.of(vozPrincipal, bateria, bajo)));
		maxCanciones = 2;
		bateristaContratado1 = new ArtistaContratado("Walter Sidotti", new ArrayList<>(List.of(bateria)),
				List.of(redondos), 3500, maxCanciones);
		BandaHistorico sodaStereo = new BandaHistorico("Soda Stereo");
		bajistaContratado1 = new ArtistaContratado("Zera Bosio", new ArrayList<>(List.of(bajo)), List.of(sodaStereo),
				5000, maxCanciones);
		cantanteContratado1 = new ArtistaContratado("Gustavo Cerati", new ArrayList<>(List.of(vozPrincipal)),
				List.of(sodaStereo), 5000, maxCanciones);
		repertorio = new ArrayList<>();
	}

//	@Test
	void sePuedeInstanciarRecital() {

	}

	@Test
	void contratacionExitosaDeArtistasBaseParaUnaCancion() {
//		cancion1 = new Cancion("Hábil", new ArrayList<>(List.of(vozPrincipal, vozSecundaria, guitarraElectrica)));
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
		assertFalse(transaccion.sePuedenEntrenarArtistasSuficientes());
	}

	@Test
	void contratacionExitosaDeArtistasContratadosParaUnaCancion() {
		repertorio.addLast(cancion2);
		lineUpArtistaContratado = new ArrayList<>(
				List.of(bateristaContratado1, bajistaContratado1, cantanteContratado1));
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
		cancion3 = new Cancion("Román", new ArrayList<>(List.of(vozPrincipal, bateria, bajo)));
		cancion4 = new Cancion("Monoblock", new ArrayList<>(List.of(vozPrincipal, bateria, bajo)));
//		cancion5 = new Cancion("Crow", new ArrayList<>(List.of(vozPrincipal, bateria, bajo)));
		repertorio.addLast(cancion2);
		repertorio.addLast(cancion3);
		repertorio.addLast(cancion4);
//		cancionero.addLast(cancion5);
		int indexCancion1 = 0, indexCancion2 = 1, indexCancion3 = 2;
		lineUpArtistaContratado = new ArrayList<>(
				List.of(bateristaContratado1, bajistaContratado1, cantanteContratado1));
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
		cancion2 = new Cancion("Román", new ArrayList<>(List.of(vozPrincipal, piano, piano, armonica, vozSecundaria)));
//		cancion5 = new Cancion("Crow", new ArrayList<>(List.of(vozPrincipal, bateria, bajo)));
		repertorio.addLast(cancion2);
//		cancionero.addLast(cancion5);
		int indexCancion2 = 0;
		lineUp = new ArrayList<>(List.of(bateristaContratado1, bajistaContratado1, cantanteContratado1, cantanteBase1,
				bajistaContratado1));
		recital = new Recital(repertorio, lineUp, roles);
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
		repertorio.addLast(cancion2);
//		cancionero.addLast(cancion5);
		int indexCancion2 = 0;
		lineUp = new ArrayList<>(List.of(bateristaContratado1, bajistaContratado1, cantanteContratado1, cantanteBase1,
				bajistaContratado1));
		recital = new Recital(repertorio, lineUp, roles);
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
		repertorio.addLast(cancion2);
		////		cancionero.addLast(cancion5);
		int indexCancion2 = 0;
		ArtistaBase bajistaYPianistaContratado = new ArtistaBase("Kamasi Washington",
				new ArrayList<>(List.of(bajo, piano)),
				new ArrayList<>(List.of(new BandaHistorico("Kamasi Washington"))));
//		ArtistaContratado bajistaYPianistaContratado = new ArtistaContratado("Kamasi Washington",
		////				new ArrayList<>(List.of(bajo, piano)),
////				new ArrayList<>(List.of(new BandaHistorico("Kamasi Washington"))), 5000, maxCanciones);
		lineUp = new ArrayList<>(List.of(bateristaContratado1, bajistaContratado1, cantanteContratado1, cantanteBase1,
				cantantePrincSecunBase2, bajistaYPianistaContratado));
		recital = new Recital(repertorio, lineUp, roles);
		assertTrue(cancion2.getListadoDeIntegrantes().isEmpty());
		TransaccionAsignacionDeCancion transaccion = recital.contratarArtistasParaUnaCancion(indexCancion2);
		assertTrue(transaccion.esTransaccionCommitted());
		assertEquals(expectedCuposDisponibles, cancion2.getCantDeCuposDisponibles());
		assertFalse(cancion2.artistaEstaAsignado(cantanteContratado1));
		double expectedCosto = bateristaContratado1.getCosto() + bajistaContratado1.getCosto()
				+ bajistaYPianistaContratado.getCosto();
		assertEquals(expectedCosto, cancion2.getCostoDeCancion());
	}

	@Test
	void sePuedeQuitarArtistaBaseDeUnaCancion() {
		int expectedCuposDisponibles = 1;
		cancion2 = new Cancion("Crawling", new ArrayList<>(List.of(vozPrincipal, vozPrincipal, bajo, bateria)));
		repertorio.addLast(cancion2);
		int indexCancion = 0, indexArtista = 0;
		lineUp = new ArrayList<>(List.of(cantanteBase1, cantanteContratado1, bajistaContratado1, bateristaContratado1));
		recital = new Recital(repertorio, lineUp, roles);
//		double expectedCosto = 
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
		cancion2 = new Cancion("Crawling", new ArrayList<>(List.of(vozPrincipal, vozPrincipal, bajo, bateria)));
		repertorio.addLast(cancion2);
		int indexCancion = 0, indexArtista;
		lineUp = new ArrayList<>(List.of(cantanteBase1, cantanteContratado1, bajistaContratado1, bateristaContratado1));
		recital = new Recital(repertorio, lineUp, roles);
		double expectedCostoSinCantanteContratado = bajistaContratado1.getCosto() + bateristaContratado1.getCosto();
		assertTrue(recital.contratarArtistasParaUnaCancion(indexCancion).esTransaccionCommitted());
		indexArtista = cancion2.getListadoDeIntegrantes().indexOf(cantanteContratado1);
		ArtistaBase expectedArtistaQuitado = cantanteContratado1;
		assertTrue(cancion2.artistaEstaAsignado(expectedArtistaQuitado));
		assertEquals(expectedCostoSinCantanteContratado + cantanteContratado1.getCosto(), cancion2.getCostoDeCancion());
		recital.quitarArtistaDeCancion(indexArtista, indexCancion);
		assertFalse(cancion2.artistaEstaAsignado(expectedArtistaQuitado));
		assertEquals(expectedCuposDisponibles, cancion2.getCantDeCuposDisponibles());
		assertEquals(expectedCostoSinCantanteContratado, cancion2.getCostoDeCancion());
	}

	@Test
	void noSePuedeQuitarArtistaDeUnaCancionIngresandoUnIndiceInvalido() {
		cancion2 = new Cancion("Crawling", new ArrayList<>(List.of(vozPrincipal, vozPrincipal, bajo, bateria)));
		repertorio.addLast(cancion2);
		lineUp = new ArrayList<>(List.of(cantanteBase1, cantanteContratado1, bajistaContratado1, bateristaContratado1));
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
		cancion2 = new Cancion("Crawling", new ArrayList<>(List.of(vozPrincipal, vozPrincipal, bajo, bateria)));
		repertorio.addLast(cancion2);
		lineUp = new ArrayList<>(List.of(cantanteBase1, cantanteContratado1, bajistaContratado1, bateristaContratado1));
		recital = new Recital(repertorio, lineUp, roles);
		int indexCancion = 0;
		assertTrue(recital.contratarArtistasParaUnaCancion(indexCancion).esTransaccionCommitted());
		assertThrows(IllegalArgumentException.class, () -> recital.quitarArtistaDeTodasLasCanciones(null));
	}

	@Test
	void noSePuedeQuitarArtistaDeTodasLasCancionesSiNoExisteEnElLineUp() {
		cancion1 = new Cancion("Crawling", new ArrayList<>(List.of(vozPrincipal, bajo, bateria)));
		cancion2 = new Cancion("Crawling", new ArrayList<>(List.of(vozPrincipal, bajo, bateria)));
		repertorio.addLast(cancion1);
		repertorio.addLast(cancion2);
		lineUp = new ArrayList<>(List.of(cantanteBase1, cantanteContratado1, bajistaContratado1, bateristaContratado1));
		recital = new Recital(repertorio, lineUp, roles);

		assertThrows(RuntimeException.class, () -> recital.quitarArtistaDeTodasLasCanciones("No soy un artista"));
	}

	@Test
	void noSePuedeQuitarArtistaDeTodasLasCancionesSiNoEstaAsignadoANinguna() {
		cancion1 = new Cancion("Crawling", new ArrayList<>(List.of(vozPrincipal, bajo, bateria)));
		cancion2 = new Cancion("Crawling", new ArrayList<>(List.of(vozPrincipal, bajo, bateria)));
		repertorio.addLast(cancion1);
		repertorio.addLast(cancion2);
		lineUp = new ArrayList<>(List.of(cantanteBase1, cantanteContratado1, bajistaContratado1, bateristaContratado1));
		recital = new Recital(repertorio, lineUp, roles);

		int indexCancion1 = 0, indexCancion2 = 1;
		assertTrue(recital.contratarArtistasParaUnaCancion(indexCancion1).esTransaccionCommitted());
		assertTrue(recital.contratarArtistasParaUnaCancion(indexCancion2).esTransaccionCommitted());
		assertFalse(recital.quitarArtistaDeTodasLasCanciones(cantanteContratado1.getNombre()));
	}

	@Test
	void sePuedeQuitarArtistaDeTodasLasCanciones() {
		cancion1 = new Cancion("Crawling", new ArrayList<>(List.of(vozPrincipal, bajo, bateria)));
		cancion2 = new Cancion("Eres Un@ Mas", new ArrayList<>(List.of(vozPrincipal, bajo, bateria)));
		repertorio.addLast(cancion1);
		repertorio.addLast(cancion2);
		lineUp = new ArrayList<>(List.of(cantanteBase1, cantanteContratado1, bajistaContratado1, bateristaContratado1));
		recital = new Recital(repertorio, lineUp, roles);

		int indexCancion1 = 0, indexCancion2 = 1;
		assertTrue(recital.contratarArtistasParaUnaCancion(indexCancion1).esTransaccionCommitted());
		assertTrue(recital.contratarArtistasParaUnaCancion(indexCancion2).esTransaccionCommitted());
		assertEquals(repertorio, cantanteBase1.getListaDeCancionesEnLasQueEstaAsignado());
		assertTrue(cantanteBase1.estaAsignadoAlmenosAUnaCancion());
		assertTrue(recital.quitarArtistaDeTodasLasCanciones(cantanteBase1.getNombre()));
		assertFalse(cantanteBase1.estaAsignadoAlmenosAUnaCancion());
	}

	@Test
	void sePuedeQuitarArtistaContratadoDelLineUp() {
		cancion1 = new Cancion("Crawling", new ArrayList<>(List.of(vozPrincipal, bajo, bateria)));
		cancion2 = new Cancion("Eres Un@ Mas", new ArrayList<>(List.of(vozPrincipal, bajo, bateria)));
		repertorio.addLast(cancion1);
		repertorio.addLast(cancion2);
		lineUp = new ArrayList<>(List.of(cantanteBase1, cantanteContratado1, bajistaContratado1, bateristaContratado1));
		recital = new Recital(repertorio, lineUp, roles);

		int index = lineUp.indexOf(bajistaContratado1), indexCancion1 = 0, indexCancion2 = 1;
		assertTrue(recital.contratarArtistasParaUnaCancion(indexCancion1).esTransaccionCommitted());
		assertTrue(recital.contratarArtistasParaUnaCancion(indexCancion2).esTransaccionCommitted());

		assertEquals(repertorio, cantanteBase1.getListaDeCancionesEnLasQueEstaAsignado());
		assertTrue(bajistaContratado1.estaAsignadoAlmenosAUnaCancion());
		assertTrue(recital.quitarArtistaDelLineUp(index));
		assertFalse(bajistaContratado1.estaAsignadoAlmenosAUnaCancion());
		assertFalse(lineUp.contains(bajistaContratado1));
	}

	@Test
	void noSePuedeQuitarArtistaContratadoDelLineUpSiElIndiceEsInvalido() {
		cancion1 = new Cancion("Crawling", new ArrayList<>(List.of(vozPrincipal, bajo, bateria)));
		repertorio.addLast(cancion1);
		lineUp = new ArrayList<>(List.of(cantanteBase1, cantanteContratado1, bajistaContratado1, bateristaContratado1));
		recital = new Recital(repertorio, lineUp, roles);

		int indexNegativo = -1, indexSuperiorAlLimiteSuperior = Integer.MAX_VALUE;
		assertThrows(RuntimeException.class, () -> recital.quitarArtistaDelLineUp(indexNegativo));
		assertThrows(RuntimeException.class, () -> recital.quitarArtistaDelLineUp(indexSuperiorAlLimiteSuperior));
	}

	@Test
	void noSePuedeQuitarArtistaBaseDelLineUp() {
		cancion1 = new Cancion("Crawling", new ArrayList<>(List.of(vozPrincipal, bajo, bateria)));
		repertorio.addLast(cancion1);
		lineUp = new ArrayList<>(List.of(cantanteBase1, cantanteContratado1, bajistaContratado1, bateristaContratado1));
		recital = new Recital(repertorio, lineUp, roles);
		int index = lineUp.indexOf(cantanteBase1);
		assertFalse(recital.quitarArtistaDelLineUp(index));
	}

//	rolesFaltantasParaCancion = 1
	@Test
	void faltanTodosLosRolesParaUnaCancion() {
		cancion1 = new Cancion("Crawling", new ArrayList<>(List.of(vozPrincipal, bajo, bateria, vozPrincipal)));
		repertorio.addLast(cancion1);
		lineUp = new ArrayList<>(List.of(cantanteBase1, cantanteContratado1, bajistaContratado1, bateristaContratado1));
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
		cancion1 = new Cancion("Crawling", new ArrayList<>(List.of(vozPrincipal, bajo, bateria, vozPrincipal)));
		repertorio.addLast(cancion1);
		lineUp = new ArrayList<>(List.of(cantanteBase1, cantanteContratado1, bajistaContratado1, bateristaContratado1));
		recital = new Recital(repertorio, lineUp, roles);
		int index = 0;
		assertTrue(recital.contratarArtistasParaUnaCancion(index).esTransaccionCommitted());
		Map<String, Integer> expectedRolesFaltantes = new HashMap<>();
		assertEquals(expectedRolesFaltantes, recital.cantDeRolesFaltantesParaUnaCancion(index));
		assertTrue(recital.cantDeRolesFaltantesParaUnaCancion(index).isEmpty());
	}

	@Test
	void faltaUnRolParaUnaCancion() {
		cancion1 = new Cancion("Crawling", new ArrayList<>(List.of(vozPrincipal, bajo, bateria, vozPrincipal)));
		repertorio.addLast(cancion1);
		lineUp = new ArrayList<>(List.of(cantanteBase1, cantanteContratado1, bajistaContratado1, bateristaContratado1));
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
		cancion1 = new Cancion("Crawling", new ArrayList<>(List.of(vozPrincipal, bajo, bateria, vozPrincipal)));
		repertorio.addLast(cancion1);
		lineUp = new ArrayList<>(List.of(cantanteBase1, cantanteContratado1, bajistaContratado1, bateristaContratado1));
		recital = new Recital(repertorio, lineUp, roles);
		int indexNegativo = -1, indexFueraDelLimiteSUperior = Integer.MAX_VALUE;
		assertThrows(IllegalArgumentException.class, () -> recital.cantDeRolesFaltantesParaUnaCancion(indexNegativo));
		assertThrows(IllegalArgumentException.class,
				() -> recital.cantDeRolesFaltantesParaUnaCancion(indexFueraDelLimiteSUperior));
	}

//	rolesFaltantesParaTodasLasCanciones = 2,
	@Test
	void faltanTodosLosRolesParaTodasLasCancionesPorNoTenerNingunArtistaBases() {
		cancion1 = new Cancion("Crawling", new ArrayList<>(List.of(vozPrincipal, bajo, bateria, vozPrincipal)));
		cancion2 = new Cancion("La Casa del Sol Naciente",
				new ArrayList<>(List.of(vozPrincipal, bajo, bateria, vozSecundaria)));
		repertorio.addLast(cancion1);
		repertorio.addLast(cancion2);
		lineUp = new ArrayList<>(List.of(cantanteContratado1, bajistaContratado1, bateristaContratado1));
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
		cancion1 = new Cancion("Crawling", new ArrayList<>(List.of(vozPrincipal, guitarraElectrica, vozSecundaria)));
		cancion2 = new Cancion("Heavy is the Crown",
				new ArrayList<>(List.of(vozPrincipal, guitarraElectrica, vozPrincipal)));
		repertorio.addLast(cancion1);
		repertorio.addLast(cancion2);
		lineUp = new ArrayList<>(List.of(cantantePrincSecunBase2, cantanteBase1, guitarristaBase1));
		recital = new Recital(repertorio, lineUp, roles);
		Map<String, Integer> expectedRolesFaltantes = new HashMap<>();
		assertEquals(expectedRolesFaltantes, recital.cantDeRolesFaltantesParaTodasLasCanciones(),
				"Todos los roles deberian estár cubiertos pos los 3 artistas bases.");
		assertTrue(recital.cantDeRolesFaltantesParaTodasLasCanciones().isEmpty());
	}

	@Test
	void estanTodosLosRolesCubiertosEnTodasLasCancionesEnLineUpMezcladoConArtistasBasesYContratados() {
		cancion1 = new Cancion("Crawling", new ArrayList<>(List.of(vozPrincipal, guitarraElectrica, vozSecundaria)));
		cancion2 = new Cancion("Heavy is the Crown",
				new ArrayList<>(List.of(vozPrincipal, guitarraElectrica, vozPrincipal)));
		repertorio.addLast(cancion1);
		repertorio.addLast(cancion2);
		lineUp = new ArrayList<>(List.of(cantantePrincSecunBase2, cantanteContratado1, cantanteBase1,
				bajistaContratado1, guitarristaBase1));
		recital = new Recital(repertorio, lineUp, roles);
		Map<String, Integer> expectedRolesFaltantes = new HashMap<>();
		assertEquals(expectedRolesFaltantes, recital.cantDeRolesFaltantesParaTodasLasCanciones(),
				"Todos los roles deberian estár cubiertos pos los 3 artistas bases.");
		assertTrue(recital.cantDeRolesFaltantesParaTodasLasCanciones().isEmpty());
	}

	@Test
	void noEstanTodosLosRolesCubiertosEnTodasLasCancionesPorNoTenerBajistasNiSaxofonistasBases() {
		cancion1 = new Cancion("Crawling",
				new ArrayList<>(List.of(vozPrincipal, guitarraElectrica, vozSecundaria, bajo)));
		cancion2 = new Cancion("Heavy is the Crown",
				new ArrayList<>(List.of(vozPrincipal, guitarraElectrica, vozPrincipal, saxofon)));
		cancion3 = new Cancion("Signo Marte",
				new ArrayList<>(List.of(vozPrincipal, guitarraElectrica, vozSecundaria, bajo)));
		cancion4 = new Cancion("Broken Relief",
				new ArrayList<>(List.of(vozPrincipal, guitarraElectrica, vozPrincipal, saxofon)));
		repertorio.addLast(cancion1);
		repertorio.addLast(cancion2);
		repertorio.addLast(cancion3);
		repertorio.addLast(cancion4);
		lineUp = new ArrayList<>(List.of(cantantePrincSecunBase2, cantanteContratado1, cantanteBase1,
				bajistaContratado1, guitarristaBase1));
		recital = new Recital(repertorio, lineUp, roles);
		Map<String, Integer> expectedRolesFaltantes = new HashMap<>();
		expectedRolesFaltantes.put(bajo, 2);
		expectedRolesFaltantes.put(saxofon, 2);
		assertEquals(expectedRolesFaltantes, recital.cantDeRolesFaltantesParaTodasLasCanciones(),
				"Todos los roles deberian estár cubiertos pos los 3 artistas bases.");
	}
}
