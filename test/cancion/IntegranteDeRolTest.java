package cancion;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
	ArtistaBase artistaBase1, artistaContratado, artistaBase2;
	List<ArtistaBase> integrantes;
	IntegranteDeUnRol integranteDeRol;
	int cantIntegrantesDeRol;

	@BeforeEach
	void setUp() throws Exception {
		cantIntegrantesDeRol = 3;
		integranteDeRol = new IntegranteDeUnRol(cantIntegrantesDeRol);
		integrantes = new ArrayList<>();
		divididos = new BandaHistorico("Divididos");
		artistaBase1 = new ArtistaBase("Ricardo Mollo", Arrays.asList("voz principal"), Arrays.asList(divididos));
		artistaBase2 = new ArtistaBase("Luca Prodan", Arrays.asList("voz principal", "bajo"), Arrays.asList(divididos));
		artistaContratado = new ArtistaContratado("Catriel Ciavarella", Arrays.asList("bateria", "bajo"),
				Arrays.asList(divididos), 1000, 2);
	}

	@Test
	void sepuedeInstanciar() {
		cantIntegrantesDeRol = 1;
		integranteDeRol = new IntegranteDeUnRol(cantIntegrantesDeRol);
		assertNotNull(integranteDeRol);
		assertEquals(cantIntegrantesDeRol, integranteDeRol.getCantDeIntegrantesNecesarios());
		assertEquals(new ArrayList<>(), integranteDeRol.getListaDeIntegrantes());

		integrantes.add(artistaBase1);
		IntegranteDeUnRol integranteDeRol2 = new IntegranteDeUnRol(integrantes, 1);
		assertNotNull(integranteDeRol2);
		assertEquals(integrantes, integranteDeRol2.getListaDeIntegrantes());
		assertEquals(cantIntegrantesDeRol, integranteDeRol2.getCantDeIntegrantesNecesarios());
		assertEquals(cantIntegrantesDeRol, integranteDeRol.getCantDeCuposDisponibles());
		assertEquals(integrantes, integranteDeRol2.getListaDeIntegrantes());
	}

	@Test
	void sepuedeAgregarIntegrante() {
		assertFalse(integranteDeRol.artistaEstaAsignado(artistaBase1));
		assertTrue(integranteDeRol.agregarIntegrante(artistaBase1));
		assertTrue(integranteDeRol.artistaEstaAsignado(artistaBase1),
				"Es verdadero porque el integrante ya está asignado en la lista.");
		assertEquals(cantIntegrantesDeRol - 1, integranteDeRol.getCantDeCuposDisponibles(),
				"La cantidad de cupos es uno menos que la cantidad maxima porque se agregó un integrante.");
	}

	@Test
	void noSepuedeAgregarIntegranteYaExistente() {
		integranteDeRol.agregarIntegrante(artistaBase1);
		assertFalse(integranteDeRol.agregarIntegrante(artistaBase1),
				"Es falso porque el integrante ya se asignó anteriormente.");
		assertEquals(cantIntegrantesDeRol - 1, integranteDeRol.getCantDeCuposDisponibles(),
				"Como no se pudo agregar, la cantidad de cupos sigue siendo uno menos que la cantidad total");
	}

	@Test
	void noSepuedeAgregarIntegranteEnRolYaOcupado() {
		cantIntegrantesDeRol = 2;
		integranteDeRol = new IntegranteDeUnRol(cantIntegrantesDeRol);
		integranteDeRol.agregarIntegrante(artistaBase1);
		assertTrue(integranteDeRol.hayCuposDisponibles(), "Es verdadero porque queda un cupo disponible");
		integranteDeRol.agregarIntegrante(artistaBase2);
		assertFalse(integranteDeRol.hayCuposDisponibles(), "Es falso porque los espacios ya están asignados");
		assertFalse(integranteDeRol.agregarIntegrante(artistaContratado),
				"Es falso porque al no haber espacio, ya no se pueden agregar integrantes.");
	}

	@Test
	void noSepuedeAgregarIntegranteNull() {
		integranteDeRol = new IntegranteDeUnRol(3);
		assertThrows(IllegalArgumentException.class, () -> integranteDeRol.agregarIntegrante(null),
				"No se puede agregar artista cuya referencia es null");
	}

	@Test
	void sepuedeQuitarIntegrante() {
		integranteDeRol = new IntegranteDeUnRol(cantIntegrantesDeRol);
		integranteDeRol.agregarIntegrante(artistaBase1);
		assertTrue(integranteDeRol.quitarIntegrante(artistaBase1));
		assertEquals(cantIntegrantesDeRol, integranteDeRol.getCantDeCuposDisponibles(),
				"Al haber quitado el unico integrante están disponibles todos los espacios.");
	}

	@Test
	void noSepuedeQuitarIntegranteNoExistente() {
		integranteDeRol.agregarIntegrante(artistaBase1);
		assertFalse(integranteDeRol.quitarIntegrante(artistaBase2),
				"No se puede quitar artistaBase2 porque el rol solo contiene a artistaBase1.");
	}

	@Test
	void noSepuedeQuitarIntegranteNull() {
		assertThrows(IllegalArgumentException.class, () -> integranteDeRol.quitarIntegrante(null),
				"No se puede agregar artista cuya referencia es null");
	}
}
