package artista;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cancion.IntegranteDeRol;

class ArtistaBaseTest {

	BandaHistorico divididos;
	ArtistaBase artistaBase1, artistaContratado, artistaBase2;
	List<ArtistaBase> integrantes;
	IntegranteDeRol integranteDeRol;
	int cantIntegrantesDeRol;

	@BeforeEach
	void setUp() throws Exception {
		cantIntegrantesDeRol = 3;
		integranteDeRol = new IntegranteDeRol(cantIntegrantesDeRol);
		integrantes = new ArrayList<>();
		divididos = new BandaHistorico("Divididos");
		artistaBase1 = new ArtistaBase("Ricardo Mollo", Arrays.asList("voz principal"), Arrays.asList(divididos));
		artistaBase2 = new ArtistaBase("Luca Prodan", Arrays.asList("voz principal", "bajo"), Arrays.asList(divididos));
		artistaContratado = new ArtistaContratado("Catriel Ciavarella", Arrays.asList("bateria", "bajo"),
				Arrays.asList(divididos), 1000, 2);
	}

	@Test
	void artistaBaseTieneRol() {
		BandaHistorico banda = new BandaHistorico("Las Manos de Filippi");
//		banda.nombre = "";
		String nombre = "Palito ortega";
		List<String> rol = Arrays.asList("voz principal", "bateria");
		ArtistaBase artista = new ArtistaBase(nombre, Arrays.asList("voz principal", "bateria"), List.of(banda));
		banda.agregarIntegrante(artista);

		assertEquals(rol, artista.getRoles());
		assertTrue(artista.tieneRol("voz principal"));
		assertTrue(artista.tieneRol("bateria"));
	}

	@Test
	void artistaBaseNoTieneRol() {
		BandaHistorico banda = new BandaHistorico("Las Manos de Filippi");
		ArtistaBase artista = new ArtistaBase("Palito ortega", Arrays.asList("voz principal", "bateria"),
				List.of(banda));
		banda.agregarIntegrante(artista);

		assertFalse(artista.tieneRol("acordeón"));
	}

	@Test
	void artistaBaseTieneBandaHistorico() {
		BandaHistorico banda = new BandaHistorico("Las Manos de Filippi");
		String nombre = "Palito ortega";
		ArtistaBase artista = new ArtistaBase(nombre, Arrays.asList("voz principal", "bateria"), List.of(banda));
		banda.agregarIntegrante(artista);

		assertEquals(List.of(banda), artista.getListaDeBandas());
	}

}
