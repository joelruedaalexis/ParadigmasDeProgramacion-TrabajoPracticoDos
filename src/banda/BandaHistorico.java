package banda;

import java.util.ArrayList;
import java.util.List;

import artista.Artista;

public class BandaHistorico {
	private String nombre;
	private List<Artista> integrantes;

	public BandaHistorico(String nombre) {
		this.nombre = nombre;
		this.integrantes = new ArrayList<>();
	}

	public boolean tieneArtistaDeDiscografica() {
		boolean tieneArtistaDeDiscografica = false;

		for (int i = 0; i < integrantes.size() && !tieneArtistaDeDiscografica; i++) {
			if (integrantes.get(i).perteneceADiscografica())
				tieneArtistaDeDiscografica = true;
		}
		return tieneArtistaDeDiscografica;
	}

	public void agregarArtista(Artista artista) {
		integrantes.add(artista);
	}

	@Override
	public String toString() {
//		return "Banda [nombre=" + nombre + "]";
		return "Banda [nombre=" + nombre + ", integrantes=" + integrantes + "]";
	}

	public String getNombre() {
		return nombre;
	}
}
