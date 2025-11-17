package recital;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import artista.ArtistaBase;

public class ComparadoraDeArtistasXRoles implements Comparator<String> {
	private Map<String, List<ArtistaBase>> candidatosPorRol;

	public ComparadoraDeArtistasXRoles(Map<String, List<ArtistaBase>> candidatosPorRol) {
		this.candidatosPorRol = candidatosPorRol;
	}

	@Override
	public int compare(String rolUno, String rolDos) {
		int compUno = candidatosPorRol.containsKey(rolUno) ? candidatosPorRol.get(rolUno).size() : 0;
		int compDos = candidatosPorRol.containsKey(rolDos) ? candidatosPorRol.get(rolDos).size() : 0;
		return Integer.compare(compUno, compDos);
	}
}
