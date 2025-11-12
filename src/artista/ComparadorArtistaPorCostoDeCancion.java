package artista;

import java.util.Comparator;

public class ComparadorArtistaPorCostoDeCancion implements Comparator<ArtistaBase>{

	@Override
	public int compare(ArtistaBase o1, ArtistaBase o2) {
		// TODO Auto-generated method stub
		return Double.compare(o1.costo, o2.costo);
	}

}
