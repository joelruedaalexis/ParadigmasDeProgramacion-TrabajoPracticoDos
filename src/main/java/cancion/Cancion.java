package cancion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import artista.ArtistaBase;

public class Cancion {
	private String titulo;
	private List<IntegranteDeUnRol> integrantesXRol;

	private Cancion(String titulo) {
		this.titulo = titulo;
	}

	public static Cancion crearCancionSinIntegrantesAsignados(String titulo, List<String> roles) {
		Cancion cancion = new Cancion(titulo);
		cancion.inicializarIntegrantesXRol(roles);
		return cancion;
	}

	public static Cancion crearCancionConIntegrantesAAsignar(String titulo, List<IntegranteDeUnRol> integrantesXRol) {
		Cancion cancion = new Cancion(titulo);
		cancion.integrantesXRol = integrantesXRol;
		return cancion;
	}

	@Override
	public int hashCode() {
		return Objects.hash(integrantesXRol, titulo);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cancion other = (Cancion) obj;
		return Objects.equals(integrantesXRol, other.integrantesXRol) && Objects.equals(titulo, other.titulo);
	}

	private void inicializarIntegrantesXRol(List<String> roles) {
		this.integrantesXRol = new ArrayList<>();
		Map<String, Integer> cupoXRol = new HashMap<>();
		for (int i = 0; i < roles.size(); i++) {
			cupoXRol.put(roles.get(i), cupoXRol.getOrDefault(roles.get(i), 0) + 1);
		}
		for (Map.Entry<String, Integer> nodo : cupoXRol.entrySet()) {
			String rol = nodo.getKey();
			Integer cantIntegrantesQueNecesitaEseRol = nodo.getValue();
			IntegranteDeUnRol integranteDeUnRol = new IntegranteDeUnRol(rol, cantIntegrantesQueNecesitaEseRol);
			integrantesXRol.addLast(integranteDeUnRol);
		}
		cupoXRol.clear();
	}

	public boolean artistaEstaAsignado(ArtistaBase artista) {
		if (artista == null)
			throw new IllegalArgumentException("No se puede asignar un artista en null");
		boolean encontroArtista = false;
		for (int i = 0; i < integrantesXRol.size() && !encontroArtista; i++) {
			if (integrantesXRol.get(i).artistaEstaAsignado(artista))
				encontroArtista = true;
		}
		return encontroArtista;
	}

	public int getCantDeCuposDisponibles() {
		int cant = 0;
		for (IntegranteDeUnRol integrantesDeRol : integrantesXRol) {
			cant += integrantesDeRol.getCantDeCuposDisponibles();
		}
		return cant;
	}

	public Map<String, Integer> getRolesFaltantesXCupos() {
		Map<String, Integer> rolesFaltantesXCupos = new HashMap<>();
		for (IntegranteDeUnRol integrantesDeRol : integrantesXRol) {
			if (integrantesDeRol.hayCuposDisponibles())
				rolesFaltantesXCupos.put(integrantesDeRol.getRol(), integrantesDeRol.getCantDeCuposDisponibles());
		}
		return rolesFaltantesXCupos;
	}

	public boolean tieneRolesDisponibles() {
		for (IntegranteDeUnRol integrantesDeRol : integrantesXRol) {
			if (integrantesDeRol.hayCuposDisponibles())
				return true;
		}
		return false;
	}

	public List<ArtistaBase> getListadoDeIntegrantes() {
		List<ArtistaBase> listadoDeIntegrantes = new ArrayList<>();
		for (IntegranteDeUnRol integrantesDeRol : integrantesXRol) {
			List<ArtistaBase> listaIntegrantesDeRol = integrantesDeRol.getListaDeIntegrantes();
			listadoDeIntegrantes.addAll(listaIntegrantesDeRol);
		}
		return listadoDeIntegrantes;
	}

	public boolean agregarArtista(String rol, ArtistaBase artista) {
		if (rol == null)
			throw new IllegalArgumentException("No se puede agregar un artista con rol en null.");
		if (artista == null)
			throw new IllegalArgumentException("No se puede agregar un artista en null.");
		IntegranteDeUnRol integranteDeUnRol;
		boolean encontroRol = false;
		int i = 0;
		while (i < integrantesXRol.size() && !encontroRol) {
			if (integrantesXRol.get(i).getRol().equals(rol)) {
				encontroRol = true;
			} else
				i++;
		}
		if (!encontroRol || !integrantesXRol.get(i).hayCuposDisponibles())
			return false;
		integranteDeUnRol = integrantesXRol.get(i);
		if (!artista.puedeSerAsignadoACancion())
			return false;
		if (!artista.getRoles().contains(rol))
			return false;
		integranteDeUnRol.agregarIntegrante(artista);
		artista.asignar(this);
		return true;
	}

	public boolean quitarArtista(ArtistaBase artista) {
		if (artista == null)
			throw new IllegalArgumentException("No se puede quitar un artista null.");
		if (!artista.estaAsignadoACancion(this))
			return false;
		boolean quitoArtista = false;
		for (int i = 0; i < integrantesXRol.size() && !quitoArtista; i++) {
			if (integrantesXRol.get(i).artistaEstaAsignado(artista)) {
				integrantesXRol.get(i).quitarIntegrante(artista);
				artista.designar(this);
				quitoArtista = true;
			}
		}
		return true;
	}

	public List<String> getRoles() {
		List<String> roles = new ArrayList<>();
		for (IntegranteDeUnRol integrantesDeUnRol : integrantesXRol) {
			for (int i = 0; i < integrantesDeUnRol.getCantDeIntegrantesNecesarios(); i++)
				roles.add(integrantesDeUnRol.getRol());
		}
		return roles;
	}

	public Map<String, IntegranteDeUnRol> getRolesFaltantes() {
		Map<String, IntegranteDeUnRol> rolesXCuposDeIntegrantes = new HashMap<>();
		for (IntegranteDeUnRol integrantesDeUnRol : integrantesXRol) {
			if (integrantesDeUnRol.hayCuposDisponibles())
				rolesXCuposDeIntegrantes.put(integrantesDeUnRol.getRol(), new IntegranteDeUnRol(
						integrantesDeUnRol.getRol(), integrantesDeUnRol.getCantDeCuposDisponibles()));
		}
		return rolesXCuposDeIntegrantes;
	}

	public String getTitulo() {
		return this.titulo;
	}

	public double getCostoDeCancion() {
		double costo = 0;
		for (IntegranteDeUnRol integrantesDeRol : integrantesXRol)
			costo += integrantesDeRol.getCostoDeIntegrantesAsignados();
		return costo;
	}

	private String integrantesToString(int cuposDisponibles, List<ArtistaBase> lista) {
		String str = "";
		str += lista.isEmpty() ? "disponible" : lista.getFirst().getNombre();
		for (int i = 1; i < lista.size(); i++)
			str += ", " + lista.get(i).getNombre();
		if (cuposDisponibles > 1) {
			cuposDisponibles--;
			str += String.format(", %s", "disponible").repeat(cuposDisponibles);
		}
		return str;
	}

	@Override
	public String toString() {
		String str = "->La canción \"" + titulo + "\" está constituida por:\n";
		for (IntegranteDeUnRol integrantesDeUnRol : integrantesXRol) {
			str += String.format("\t~%s= %s\n", integrantesDeUnRol.getRol(), integrantesToString(
					integrantesDeUnRol.getCantDeCuposDisponibles(), integrantesDeUnRol.getListaDeIntegrantes()));
		}
		str += "\tY su costo es de $" + this.getCostoDeCancion() + "\n";
		return str;
	}

	public JsonObject toJSON() {
		double costo = 0;
		JsonObject cancionJSON = new JsonObject();
		JsonArray arrayRolesXIntegrantesJSON = new JsonArray();
		for (IntegranteDeUnRol integrantesDeRol : integrantesXRol) {
			int lugaresDisponibles = integrantesDeRol.getCantDeCuposDisponibles();
			JsonObject rolXIntegranteJSON = new JsonObject();
			JsonArray arrayIntegrantes = new JsonArray(integrantesDeRol.getCantDeIntegrantesNecesarios());
			List<ArtistaBase> listaIntegrantesDeRol = integrantesDeRol.getListaDeIntegrantes();
			for (int i = 0; i < listaIntegrantesDeRol.size(); i++) {
				arrayIntegrantes.add(listaIntegrantesDeRol.get(i).getNombre());
				costo += listaIntegrantesDeRol.get(i).getCosto();
			}
			for (int j = lugaresDisponibles; j > 0; j--)
				arrayIntegrantes.add("vacante");
			rolXIntegranteJSON.addProperty("rol", integrantesDeRol.getRol());
			rolXIntegranteJSON.add("integrantes", arrayIntegrantes);
			arrayRolesXIntegrantesJSON.add(rolXIntegranteJSON);
		}
		cancionJSON.addProperty("titulo", this.titulo);
		cancionJSON.add("rolesXArtista", arrayRolesXIntegrantesJSON);
		cancionJSON.addProperty("costo", costo);
		return cancionJSON;
	}
}