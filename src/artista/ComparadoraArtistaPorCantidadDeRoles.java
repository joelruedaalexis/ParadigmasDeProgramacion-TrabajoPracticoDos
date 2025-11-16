package artista;

import java.util.Comparator;

public class ComparadoraArtistaPorCantidadDeRoles implements Comparator<ArtistaBase> {

	@Override
	public int compare(ArtistaBase o1, ArtistaBase o2) {
		return Integer.compare(o1.getRoles().size(), o2.getRoles().size());
	}

}
