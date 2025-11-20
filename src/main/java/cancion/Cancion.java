package cancion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import artista.ArtistaBase;

public class Cancion {
	private String titulo = null;
//	private Map<String, List<ArtistaBaseBase>> rolesXIntegrantes;
//	private Map<String, Integer> rolesXCuposDeIntegrantes;
	private Map<String, IntegranteDeUnRol> integrantesXRol;

	public Cancion(String titulo, List<String> roles) {
		this.titulo = titulo;
		inicializarRolesXIntegrantes(new LinkedList<String>(roles));
	}

	public Cancion(String titulo, Map<String, IntegranteDeUnRol> integrantesXRol) {
		this.titulo = titulo;
		this.integrantesXRol = integrantesXRol;
		this.inicializarIntegrantesXRoles();
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

	private void inicializarIntegrantesXRoles() {
		for (Map.Entry<String, IntegranteDeUnRol> nodo : integrantesXRol.entrySet()) {
			String rol = nodo.getKey();
			List<ArtistaBase> artistasDeRol = nodo.getValue().getListaDeIntegrantes();
			artistasDeRol.forEach(a -> this.agregarArtista(rol, a));
		}
	}

	private void inicializarRolesXIntegrantes(List<String> listaRoles) {
		this.integrantesXRol = new HashMap<>();
		while (!listaRoles.isEmpty()) {
			String rol = listaRoles.removeFirst();
			int cantRoles = 1;
			int i = 0;
			while (i < listaRoles.size()) {
				if (listaRoles.get(i).equals(rol)) {
					cantRoles++;
					listaRoles.remove(i);
				} else
					i++;
			}
			this.integrantesXRol.put(rol, new IntegranteDeUnRol(rol, cantRoles));

		}
	}

//	probado
	public boolean artistaEstaAsignado(ArtistaBase artista) {
		if (artista == null)
			throw new IllegalArgumentException("No se puede asignar un artista en null");// Exception?
		for (IntegranteDeUnRol integrantesDeRol : integrantesXRol.values()) {
			if (integrantesDeRol.artistaEstaAsignado(artista))
				return true;
		}
		return false;
	}

//	probado
	public int getCantDeCuposDisponibles() {
		int cant = 0;
		for (IntegranteDeUnRol integrantesDeRol : integrantesXRol.values()) {
			cant += integrantesDeRol.getCantDeCuposDisponibles();
		}
		return cant;
//		return rolesXCuposDeIntegrantes.values().stream().mapToInt(cupos -> cupos).sum();
	}

//	
	public Map<String, Integer> getRolesFaltantesXCupos() {
		Map<String, Integer> rolesFaltantesXCupos = new HashMap<>();
		for (Map.Entry<String, IntegranteDeUnRol> nodo : integrantesXRol.entrySet()) {
			if (nodo.getValue().hayCuposDisponibles())
				rolesFaltantesXCupos.put(nodo.getKey(), nodo.getValue().getCantDeCuposDisponibles());
		}
		return rolesFaltantesXCupos;
//		return rolesXCuposDeIntegrantes.values().stream().mapToInt(cupos -> cupos).sum();
	}

//	probado
	public List<ArtistaBase> getListadoDeIntegrantes() {
		List<ArtistaBase> listadoDeIntegrantes = new ArrayList<>();
		for (IntegranteDeUnRol integrantesDeRol : integrantesXRol.values()) {
			List<ArtistaBase> listaIntegrantesDeRol = integrantesDeRol.getListaDeIntegrantes();
			listadoDeIntegrantes.addAll(listaIntegrantesDeRol);
		}
//		System.out.println(listadoDeIntegrantes);
		// Y si no hay integrantes ? throw Exception?
		return listadoDeIntegrantes;
	}

//	probado
	public boolean agregarArtista(String rol, ArtistaBase artista) {// chequear si el artista en null o si rol es null
		if (rol == null)
			throw new IllegalArgumentException("No se puede agregar un artista con rol en null.");
		if (artista == null)
			throw new IllegalArgumentException("No se puede agregar un artista en null.");
		if (!integrantesXRol.containsKey(rol))
			return false;// Exception?
		if (!integrantesXRol.get(rol).hayCuposDisponibles())
			return false;// ya estan asignados todos los artistas a ese rol!!!!
		// chequear q el artista pueda ser asignado por su limite en participaciones en
		// canciones
		if (!artista.puedeSerAsignadoACancion())
			return false;
		integrantesXRol.get(rol).agregarIntegrante(artista);
		artista.asignar(this);
		return true;
	}

//	probado
	public boolean quitarArtista(ArtistaBase artista) {
		if (artista == null)
			throw new IllegalArgumentException("No se puede quitar un artista null.");
		for (IntegranteDeUnRol integrantesDeRol : integrantesXRol.values()) {// cambiar al for tradicional!!!
			if (integrantesDeRol.artistaEstaAsignado(artista)) {
				integrantesDeRol.quitarIntegrante(artista);
				artista.designar(this);
				return true;
			}
		}
		return false;
	}

//	probado
	public List<String> getRoles() {
		List<String> roles = new ArrayList<>();
		for (Map.Entry<String, IntegranteDeUnRol> nodo : integrantesXRol.entrySet()) {
			String rol = nodo.getKey();
			IntegranteDeUnRol integrantesDeRol = nodo.getValue();
			for (int i = 0; i < integrantesDeRol.getCantDeIntegrantesNecesarios(); i++)
				roles.add(rol);
		}
		return roles;
	}

	public Map<String, IntegranteDeUnRol> getRolesFaltantes() {
//		return new HashMap<>(rolesXCuposDeIntegrantes);
		Map<String, IntegranteDeUnRol> rolesXCuposDeIntegrantes = new HashMap<>();
		for (Map.Entry<String, IntegranteDeUnRol> nodo : integrantesXRol.entrySet()) {
			String rol = nodo.getKey();
			IntegranteDeUnRol integrantesDeRol = nodo.getValue();
			if (integrantesDeRol.hayCuposDisponibles())
				rolesXCuposDeIntegrantes.put(rol,
						new IntegranteDeUnRol(rol, integrantesDeRol.getCantDeCuposDisponibles()));
		}
		return rolesXCuposDeIntegrantes;
	}

//	probado
	public String getTitulo() {
		return this.titulo;
	}

//	probado
	public double getCostoDeCancion() {
		double costo = 0;
		for (IntegranteDeUnRol integrantesDeRol : integrantesXRol.values())
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
		for (Map.Entry<String, IntegranteDeUnRol> nodo : integrantesXRol.entrySet()) {
			String rol = nodo.getKey();
			IntegranteDeUnRol integrantesDeRol = nodo.getValue();
			str += String.format("\t~%s= %s\n", rol, integrantesToString(integrantesDeRol.getCantDeCuposDisponibles(),
					integrantesDeRol.getListaDeIntegrantes()));
		}
		str += "\tY su costo es de $" + this.getCostoDeCancion();
		return str;
	}

	public JsonObject toJSON() {
		double costo = 0;
		JsonObject cancionJSON = new JsonObject();
		JsonArray arrayRolesXIntegrantesJSON = new JsonArray();
		for (Map.Entry<String, IntegranteDeUnRol> rolXIntegrante : integrantesXRol.entrySet()) {
			String rol = rolXIntegrante.getKey();
			IntegranteDeUnRol integrantesDeRol = rolXIntegrante.getValue();
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
			rolXIntegranteJSON.addProperty("rol", rol);
			rolXIntegranteJSON.add("integrantes", arrayIntegrantes);
			arrayRolesXIntegrantesJSON.add(rolXIntegranteJSON);
		}
		cancionJSON.addProperty("titulo", this.titulo);
		cancionJSON.add("rolesXArtista", arrayRolesXIntegrantesJSON);
		cancionJSON.addProperty("costo", costo);
		return cancionJSON;
	}
}
