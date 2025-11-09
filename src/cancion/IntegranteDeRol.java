package cancion;

import java.util.ArrayList;
import java.util.List;

import artista.Artista;

public class IntegranteDeRol {
	List<Artista> integrantes;
	int cantDeIntegrantesNecesarios;

	public IntegranteDeRol(int cantIntegrantesDeRol) {
		this.cantDeIntegrantesNecesarios = cantIntegrantesDeRol;
		integrantes = new ArrayList<>(cantIntegrantesDeRol);
	}

	public double getCostoDeIntegrantesAsignados() {
		double costo = 0;
		for (Artista artista : integrantes) {
			costo += artista.getCosto();
		}
		return costo;
	}

	public List<Artista> getListaDeIntegrantes() {
		return integrantes;
	}

	public boolean artistaEstaAsignado(Artista artista) {
		return integrantes.contains(artista);
	}

	public int getCantDeCuposDisponibles() {
		return cantDeIntegrantesNecesarios - integrantes.size();
	}

	public boolean hayCuposDisponibles() {
		return integrantes.size() < cantDeIntegrantesNecesarios;
	}

	public void agregarIntegrante(Artista artista) {
//		agregar validaciones !!!!!!!!!!
		integrantes.addLast(artista);
	}

	public void quitarIntegrante(Artista artista) {
//		agregar validaciones!!!!!!!!!!!!!!!!!!!!!!
		integrantes.remove(artista);
	}

	public int getCantDeIntegrantesNecesarios() {
		return cantDeIntegrantesNecesarios;
	}

	@Override
	public String toString() {
		return "IntegranteDeRol [integrantes=" + integrantes.stream().map(a -> a.getNombre()).toList()
				+ ", cantDeIntegrantesNecesarios=" + cantDeIntegrantesNecesarios + "]";
	}

}
