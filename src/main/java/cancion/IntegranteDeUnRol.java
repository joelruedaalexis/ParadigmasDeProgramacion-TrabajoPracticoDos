package cancion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import artista.ArtistaBase;

public class IntegranteDeUnRol {
	private String rol;
	private List<ArtistaBase> integrantes;
	private int cantDeIntegrantesNecesarios;

	public IntegranteDeUnRol(String rol, List<ArtistaBase> integrantes) {
		this.rol = rol;
		this.integrantes = integrantes;
		cantDeIntegrantesNecesarios = integrantes.size();
	}

	public IntegranteDeUnRol(String rol, int cantIntegrantesDeRol) {
		this.rol = rol;
		this.cantDeIntegrantesNecesarios = cantIntegrantesDeRol;
		integrantes = new ArrayList<>(cantIntegrantesDeRol);
	}

//	public IntegranteDeUnRol(String rol, List<ArtistaBase> integrantes, int cantDeIntegrantesNecesarios) {
//		this.integrantes = integrantes;
//		this.cantDeIntegrantesNecesarios = cantDeIntegrantesNecesarios;
//	}

	public double getCostoDeIntegrantesAsignados() {
		double costo = 0;
		for (ArtistaBase artista : integrantes) {
			costo += artista.getCosto();
		}
		return costo;
	}

	@Override
	public int hashCode() {
		return Objects.hash(cantDeIntegrantesNecesarios, rol);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IntegranteDeUnRol other = (IntegranteDeUnRol) obj;
		return cantDeIntegrantesNecesarios == other.cantDeIntegrantesNecesarios && Objects.equals(rol, other.rol);
	}

	public List<ArtistaBase> getListaDeIntegrantes() {
		return integrantes;
	}

	public boolean artistaEstaAsignado(ArtistaBase artista) {
		return integrantes.contains(artista);
	}

	public int getCantDeCuposDisponibles() {
		return cantDeIntegrantesNecesarios - integrantes.size();
	}

	public boolean hayCuposDisponibles() {
		return integrantes.size() < cantDeIntegrantesNecesarios;
	}

	public boolean agregarIntegrante(ArtistaBase artista) {
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

	public String getRol() {
		// TODO Auto-generated method stub
		return rol;
	}
}
