package cancion;

import java.util.Comparator;

public class ComparadoraPorCantidadDeIntegrantesYRol implements Comparator<IntegranteDeUnRol> {
	@Override
	public int compare(IntegranteDeUnRol o1, IntegranteDeUnRol o2) {
		int comp = Integer.compare(o1.getCantDeIntegrantesNecesarios(), o2.getCantDeIntegrantesNecesarios());
		return comp == 0 ? o1.rol.compareTo(o2.rol) : (comp > 0 ? 1 : -1);
	}
}
