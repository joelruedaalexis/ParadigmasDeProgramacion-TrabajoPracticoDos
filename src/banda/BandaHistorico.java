package banda;

import java.util.ArrayList;
import java.util.List;

import artista.Artista;

public class BandaHistorico {
	private String nombre;
	private List<Artista> integrantes;

	public BandaHistorico(String nombre) {
		if (nombre == null || nombre.isBlank())
			throw new IllegalArgumentException("El nombre es inválido.");
		this.nombre = nombre;
		this.integrantes = new ArrayList<>();
	}

	public BandaHistorico(String nombre, List<Artista> integrantes) {
		if (nombre == null || nombre.isBlank())
			throw new IllegalArgumentException("El nombre es inválido.");
		if (integrantes == null || integrantes.contains(null))
			throw new IllegalArgumentException("La lista de integrantees es inválida.");

		this.nombre = nombre;
		this.integrantes = integrantes;
	}

	public boolean agregarIntegrante(Artista artista) {
		if(artista == null)
			throw new IllegalArgumentException("La variable ingresada no puede ser Null.");
		if (integrantes.contains(artista))
			return false;
		integrantes.addLast(artista);
		return true;
	}

	public boolean tieneArtistaDeDiscografica() {
		boolean tieneArtistaDeDiscografica = false;

		for (int i = 0; i < integrantes.size() && !tieneArtistaDeDiscografica; i++) {
			if (integrantes.get(i).perteneceADiscografica())
				tieneArtistaDeDiscografica = true;
		}
		return tieneArtistaDeDiscografica;
	}

	@Override
	public String toString() {
		return "Banda [nombre=" + nombre + ", integrantes=" + integrantes + "]";
	}

	public String getNombre() {
		return nombre;
	}
}
