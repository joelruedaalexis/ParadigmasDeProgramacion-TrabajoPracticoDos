package artista;

import java.util.Comparator;

public class ComparadorArtistaPorNombre implements Comparator<Artista> {
	@Override
	public int compare(Artista o1, Artista o2) {
		return o1.getNombre().compareTo(o2.getNombre());
	}
}
