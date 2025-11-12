package cancion;

import java.util.ArrayList;
import java.util.List;

import artista.ArtistaBase;

public class IntegranteDeRol {
	List<ArtistaBase> integrantes;
	int cantDeIntegrantesNecesarios;

	public IntegranteDeRol(int cantIntegrantesDeRol) {
		this.cantDeIntegrantesNecesarios = cantIntegrantesDeRol;
		integrantes = new ArrayList<>(cantIntegrantesDeRol);
	}

	public IntegranteDeRol(List<ArtistaBase> integrantes, int cantDeIntegrantesNecesarios) {
		this.integrantes = integrantes;
		this.cantDeIntegrantesNecesarios = cantDeIntegrantesNecesarios;
	}

	public double getCostoDeIntegrantesAsignados() {
		double costo = 0;
		for (ArtistaBase artista : integrantes) {
			costo += artista.getCosto();
		}
		return costo;
	}

//
	public List<ArtistaBase> getListaDeIntegrantes() {
		return integrantes;
	}

//
	public boolean artistaEstaAsignado(ArtistaBase artista) {
		return integrantes.contains(artista);
	}

//
	public int getCantDeCuposDisponibles() {
		return cantDeIntegrantesNecesarios - integrantes.size();
	}

//
	public boolean hayCuposDisponibles() {
		return integrantes.size() < cantDeIntegrantesNecesarios;
	}

//
	public boolean agregarIntegrante(ArtistaBase artista) {
//		agregar validaciones !!!!!!!!!!
		if (artista == null)
			throw new IllegalArgumentException("No se puede agregar artista null.");
		if (!this.hayCuposDisponibles())
			return false;
		if (integrantes.contains(artista))
			return false;
		integrantes.addLast(artista);
		return true;
	}

	public boolean quitarIntegrante(ArtistaBase artista) {
		if (artista == null)
			throw new IllegalArgumentException("No se puede quitar artista null.");
		return integrantes.remove(artista);
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
