package cancion;

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

import artista.ArtistaBase;
import artista.ArtistaContratado;
import artista.BandaHistorico;

class IntegranteDeRolTest {
	BandaHistorico divididos;
	ArtistaBase cantanteBase, bateristaYBajistaContratado, cantanteYBajistaBase;
	List<ArtistaBase> integrantes;
	IntegranteDeUnRol integranteDeRol;
	int cantIntegrantesDeRol;
	String vozPrincipal = "voz principal", bajo = "bajo", bateria = "bateria";

	@BeforeEach
	void setUp() throws Exception {
		cantIntegrantesDeRol = 3;
		integrantes = new ArrayList<>();
		divididos = new BandaHistorico("Divididos");
		cantanteBase = new ArtistaBase("Ricardo Mollo", Arrays.asList(vozPrincipal), Arrays.asList(divididos));
		cantanteYBajistaBase = new ArtistaBase("Luca Prodan", Arrays.asList(vozPrincipal, bajo),
				Arrays.asList(divididos));
		bateristaYBajistaContratado = new ArtistaContratado("Catriel Ciavarella", Arrays.asList(bateria, bajo),
				Arrays.asList(divididos), 1000, 2);
		integranteDeRol = new IntegranteDeUnRol(vozPrincipal, cantIntegrantesDeRol);
	}

	@Test
	void sepuedeInstanciar() {
		cantIntegrantesDeRol = 1;
		integranteDeRol = new IntegranteDeUnRol(vozPrincipal, cantIntegrantesDeRol);
		assertNotNull(integranteDeRol);
		assertEquals(cantIntegrantesDeRol, integranteDeRol.getCantDeIntegrantesNecesarios());
		assertEquals(vozPrincipal, integranteDeRol.getRol());
		assertEquals(new ArrayList<>(), integranteDeRol.getListaDeIntegrantes());

		integrantes.add(cantanteBase);
		IntegranteDeUnRol integranteDeRol2 = new IntegranteDeUnRol(vozPrincipal, integrantes);
		assertNotNull(integranteDeRol2);
		assertEquals(integrantes, integranteDeRol2.getListaDeIntegrantes());
		assertEquals(cantIntegrantesDeRol, integranteDeRol2.getCantDeIntegrantesNecesarios());
		assertEquals(cantIntegrantesDeRol, integranteDeRol.getCantDeCuposDisponibles());
		assertEquals(integrantes, integranteDeRol2.getListaDeIntegrantes());
	}

	@Test
	void sepuedeAgregarIntegrante() {
		assertFalse(integranteDeRol.artistaEstaAsignado(cantanteBase));
		assertTrue(integranteDeRol.agregarIntegrante(cantanteBase));
		assertTrue(integranteDeRol.artistaEstaAsignado(cantanteBase),
				"Es verdadero porque el integrante ya está asignado en la lista.");
		assertEquals(cantIntegrantesDeRol - 1, integranteDeRol.getCantDeCuposDisponibles(),
				"La cantidad de cupos es uno menos que la cantidad maxima porque se agregó un integrante.");
	}

	@Test
	void noSepuedeAgregarIntegranteYaExistente() {
		integranteDeRol.agregarIntegrante(cantanteBase);
		assertFalse(integranteDeRol.agregarIntegrante(cantanteBase),
				"Es falso porque el integrante ya se asignó anteriormente.");
		assertEquals(cantIntegrantesDeRol - 1, integranteDeRol.getCantDeCuposDisponibles(),
				"Como no se pudo agregar, la cantidad de cupos sigue siendo uno menos que la cantidad total");
	}

	@Test
	void noSepuedeAgregarIntegranteEnRolYaOcupado() {
		cantIntegrantesDeRol = 2;
		integranteDeRol = new IntegranteDeUnRol(vozPrincipal, cantIntegrantesDeRol);
		integranteDeRol.agregarIntegrante(cantanteBase);
		assertTrue(integranteDeRol.hayCuposDisponibles(), "Es verdadero porque queda un cupo disponible");
		integranteDeRol.agregarIntegrante(cantanteYBajistaBase);
		assertFalse(integranteDeRol.hayCuposDisponibles(), "Es falso porque los espacios ya están asignados");
		assertFalse(integranteDeRol.agregarIntegrante(bateristaYBajistaContratado),
				"Es falso porque al no haber espacio, ya no se pueden agregar integrantes.");
	}

	@Test
	void noSepuedeAgregarIntegranteNull() {
		integranteDeRol = new IntegranteDeUnRol("sarasa", 3);
		assertThrows(IllegalArgumentException.class, () -> integranteDeRol.agregarIntegrante(null),
				"No se puede agregar artista cuya referencia es null");
	}

	@Test
	void sepuedeQuitarIntegrante() {
		integranteDeRol = new IntegranteDeUnRol(vozPrincipal, cantIntegrantesDeRol);
		integranteDeRol.agregarIntegrante(cantanteBase);
		assertTrue(integranteDeRol.quitarIntegrante(cantanteBase));
		assertEquals(cantIntegrantesDeRol, integranteDeRol.getCantDeCuposDisponibles(),
				"Al haber quitado el unico integrante están disponibles todos los espacios.");
	}

	@Test
	void noSepuedeQuitarIntegranteNoExistente() {
		integranteDeRol.agregarIntegrante(cantanteBase);
		assertFalse(integranteDeRol.quitarIntegrante(cantanteYBajistaBase),
				"No se puede quitar artistaBase2 porque el rol solo contiene a artistaBase1.");
	}

	@Test
	void noSepuedeQuitarIntegranteNull() {
		assertThrows(IllegalArgumentException.class, () -> integranteDeRol.quitarIntegrante(null),
				"No se puede agregar artista cuya referencia es null");
	}
}
