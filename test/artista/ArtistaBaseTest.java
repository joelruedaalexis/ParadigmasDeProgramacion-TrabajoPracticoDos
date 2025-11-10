package artista;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import banda.BandaHistorico;

class ArtistaBaseTest {

	@BeforeEach
	void setUp() throws Exception {
		List<Artista> listaDivididos = new ArrayList<>();
		BandaHistorico divididos = new BandaHistorico("Divididos", listaDivididos);

		List<Artista> listaSumo = new ArrayList<>();
		BandaHistorico sumo = new BandaHistorico("Sumo", listaSumo);

		Artista mollo = new ArtistaContratado("Ricardo Mollo",
				Arrays.asList("voz principal", "voz secundaria", "guitarra"), Arrays.asList(divididos, sumo), 1650, 3);
		Artista catriel = new ArtistaContratado("Catriel Ciavarella", Arrays.asList("bateria", "bajo"),
				Arrays.asList(divididos), 1000, 2);
		Artista prodan = new ArtistaBase("Luca Prodan", Arrays.asList("voz principal", "bajo"), Arrays.asList(sumo));

		listaDivididos.add(catriel);
		listaDivididos.add(mollo);

		listaSumo.add(mollo);
		listaSumo.add(prodan);
	}

	@Test
	void test() {
		fail("Not yet implemented");
	}

}
