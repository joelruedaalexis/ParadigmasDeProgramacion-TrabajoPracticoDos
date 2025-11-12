package artista;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BandaHistorico {
	protected String nombre;
	private List<ArtistaBase> integrantes;

	public BandaHistorico(String nombre) {
		if (nombre == null || nombre.isBlank())
			throw new IllegalArgumentException("El nombre es inválido.");
		this.nombre = nombre;
		this.integrantes = new ArrayList<>();
	}

	public BandaHistorico(String nombre, List<ArtistaBase> integrantes) {
		if (nombre == null || nombre.isBlank())
			throw new IllegalArgumentException("El nombre es inválido.");
		if (integrantes == null || integrantes.contains(null))
			throw new IllegalArgumentException("La lista de integrantees es inválida.");

		this.nombre = nombre;
		this.integrantes = integrantes;
	}

	@Override
	public int hashCode() {
		return Objects.hash(integrantes, nombre);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BandaHistorico other = (BandaHistorico) obj;
		return Objects.equals(integrantes, other.integrantes) && Objects.equals(nombre, other.nombre);
	}

	protected boolean agregarIntegrante(ArtistaBase artista) {
		if (artista == null)
			throw new IllegalArgumentException("El objeto ingresado no puede ser Null.");
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
