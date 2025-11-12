package artista;

import java.util.Comparator;

public class ComparadorArtistaPorNombre implements Comparator<ArtistaBase> {
	@Override
	public int compare(ArtistaBase o1, ArtistaBase o2) {
		return o1.getNombre().compareTo(o2.getNombre());
	}
}
