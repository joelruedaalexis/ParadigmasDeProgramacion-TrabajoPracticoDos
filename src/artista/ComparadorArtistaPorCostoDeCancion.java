package artista;

import java.util.Comparator;

public class ComparadorArtistaPorCostoDeCancion implements Comparator<Artista>{

	@Override
	public int compare(Artista o1, Artista o2) {
		// TODO Auto-generated method stub
		return Double.compare(o1.costo, o2.costo);
	}

}
