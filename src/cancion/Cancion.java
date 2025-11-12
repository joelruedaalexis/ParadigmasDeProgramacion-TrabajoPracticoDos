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
	private Map<String, IntegranteDeRol> rolesXListaDeIntegrantes;

	public Cancion(String titulo, List<String> roles) {
		this.titulo = titulo;
		inicializarRolesXIntegrantes(new LinkedList<String>(roles));
	}

	public Cancion(String titulo, Map<String, IntegranteDeRol> rolesXListaDeIntegrantes) {
		this.titulo = titulo;
		this.rolesXListaDeIntegrantes = rolesXListaDeIntegrantes;
		this.inicializarRolesXIntegrantes();
	}

	@Override
	public int hashCode() {
		return Objects.hash(rolesXListaDeIntegrantes, titulo);
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
		return Objects.equals(rolesXListaDeIntegrantes, other.rolesXListaDeIntegrantes)
				&& Objects.equals(titulo, other.titulo);
	}

	private void inicializarRolesXIntegrantes() {
		for (Map.Entry<String, IntegranteDeRol> nodo : rolesXListaDeIntegrantes.entrySet()) {
			String rol = nodo.getKey();
			List<ArtistaBase> artistasDeRol = nodo.getValue().getListaDeIntegrantes();
			artistasDeRol.forEach(a -> this.agregarArtista(rol, a));
		}
	}

	private void inicializarRolesXIntegrantes(List<String> listaRoles) {
		this.rolesXListaDeIntegrantes = new HashMap<>();
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
			this.rolesXListaDeIntegrantes.put(rol, new IntegranteDeRol(cantRoles));

		}
	}

	public boolean artistaEstaAsignado(ArtistaBase artista) {
		if (artista == null)
			return false;// Exception?
		for (IntegranteDeRol integrantesDeRol : rolesXListaDeIntegrantes.values()) {
			if (integrantesDeRol.artistaEstaAsignado(artista))
				return true;
		}
		return false;
	}

	// Rol 3 cantantes
	// List<> = [null,null,pepe] ó [pepe,null,null]
	public int cantDeCuposDisponibles() {
		int cant = 0;
		for (IntegranteDeRol integrantesDeRol : rolesXListaDeIntegrantes.values()) {
			cant += integrantesDeRol.getCantDeCuposDisponibles();
		}
		return cant;
//		return rolesXCuposDeIntegrantes.values().stream().mapToInt(cupos -> cupos).sum();
	}

	public Map<String, Integer> getRolesFaltantesXCupos() {
		Map<String, Integer> rolesFaltantesXCupos = new HashMap<>();
		for (Map.Entry<String, IntegranteDeRol> nodo : rolesXListaDeIntegrantes.entrySet()) {
			if (nodo.getValue().hayCuposDisponibles())
				rolesFaltantesXCupos.put(nodo.getKey(), nodo.getValue().getCantDeCuposDisponibles());
		}
		return rolesFaltantesXCupos;
//		return rolesXCuposDeIntegrantes.values().stream().mapToInt(cupos -> cupos).sum();
	}

	public List<ArtistaBase> getListadoDeIntegrantes() {
		List<ArtistaBase> listadoDeIntegrantes = new ArrayList<>();
		for (IntegranteDeRol integrantesDeRol : rolesXListaDeIntegrantes.values()) {
			List<ArtistaBase> listaIntegrantesDeRol = integrantesDeRol.getListaDeIntegrantes();
			listadoDeIntegrantes.addAll(listaIntegrantesDeRol);
		}
//		System.out.println(listadoDeIntegrantes);
		// Y si no hay integrantes ? throw Exception?
		return listadoDeIntegrantes;
	}

	public boolean agregarArtista(String rol, ArtistaBase artista) {// chequear si el artista en null o si rol es null maybe
																// ?
		if (!rolesXListaDeIntegrantes.containsKey(rol))
			return false;// Exception?
		if (!rolesXListaDeIntegrantes.get(rol).hayCuposDisponibles())
			return false;// ya estan asignados todos los artistas a ese rol!!!!
		// chequear q el artista pueda ser asignado por su limite en participaciones en
		// canciones
		rolesXListaDeIntegrantes.get(rol).agregarIntegrante(artista);
		artista.asignar(this);
		return true;
	}

	public void quitarArtista(ArtistaBase artista) {
		for (IntegranteDeRol integrantesDeRol : rolesXListaDeIntegrantes.values()) {
			if (integrantesDeRol.artistaEstaAsignado(artista)) {
				integrantesDeRol.quitarIntegrante(artista);
				artista.designar(this);
			}
		}
	}

	public List<String> getRoles() {
		List<String> roles = new ArrayList<>();
		for (Map.Entry<String, IntegranteDeRol> nodo : rolesXListaDeIntegrantes.entrySet()) {
			String rol = nodo.getKey();
			IntegranteDeRol integrantesDeRol = nodo.getValue();
			for (int i = 0; i < integrantesDeRol.getCantDeIntegrantesNecesarios(); i++)
				roles.add(rol);
		}
		return roles;
	}

	public Map<String, IntegranteDeRol> getRolesConCuposDeIntegrantes() {
//		return new HashMap<>(rolesXCuposDeIntegrantes);
		Map<String, IntegranteDeRol> rolesXCuposDeIntegrantes = new HashMap<>();
		for (Map.Entry<String, IntegranteDeRol> nodo : rolesXListaDeIntegrantes.entrySet()) {
			String rol = nodo.getKey();
			IntegranteDeRol integrantesDeRol = nodo.getValue();
			if (integrantesDeRol.hayCuposDisponibles())
				rolesXCuposDeIntegrantes.put(rol, new IntegranteDeRol(integrantesDeRol.getCantDeCuposDisponibles()));
		}
		return rolesXCuposDeIntegrantes;
	}

	public String getTitulo() {
		return this.titulo;
	}

	public double getCostoDeCancion() {
		double costo = 0;
		for (IntegranteDeRol integrantesDeRol : rolesXListaDeIntegrantes.values())
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
		for (Map.Entry<String, IntegranteDeRol> nodo : rolesXListaDeIntegrantes.entrySet()) {
			String rol = nodo.getKey();
			IntegranteDeRol integrantesDeRol = nodo.getValue();
//			str += String.format("\t%c%s= ", 158, rol);
//			str += String.format("\t%c%s= %s\n", 158, rol, integrantesToString(artistas));
			str += String.format("\t~%s= %s\n", rol, integrantesToString(integrantesDeRol.getCantDeCuposDisponibles(),
					integrantesDeRol.getListaDeIntegrantes()));
		}
		return str;
	}

	public JsonObject toJSON() {
		double costo = 0;
		JsonObject cancionJSON = new JsonObject();
		JsonArray arrayRolesXIntegrantesJSON = new JsonArray();
		for (Map.Entry<String, IntegranteDeRol> rolXIntegrante : rolesXListaDeIntegrantes.entrySet()) {
			String rol = rolXIntegrante.getKey();
			IntegranteDeRol integrantesDeRol = rolXIntegrante.getValue();
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
//		for (Map.Entry<String, List<ArtistaBase>> rolXIntegrante : rolesXIntegrantes.entrySet()) {
//			String rol = rolXIntegrante.getKey();
//			List<ArtistaBase> listaIntegrantesDeRol = rolXIntegrante.getValue();
//			int lugaresDisponibles = rolesXCuposDeIntegrantes.get(rol);
//			int i;
//			JsonObject rolXIntegranteJSON = new JsonObject();
//			JsonArray arrayIntegrantes = new JsonArray(listaIntegrantesDeRol.size());
//			for (i = 0; i < listaIntegrantesDeRol.size(); i++) {
//				arrayIntegrantes.add(listaIntegrantesDeRol.get(i).getNombre());
//				costo += listaIntegrantesDeRol.get(i).getCosto();
//			}
//			
//			while (i < listaIntegrantesDeRol.size()) {
//				arrayIntegrantes.add("vacante");
//				i++;
//			}
//			rolXIntegranteJSON.addProperty("rol", rol);
//			rolXIntegranteJSON.add("integrantes", arrayIntegrantes);
//			arrayRolesXIntegrantesJSON.add(rolXIntegranteJSON);
//		}
		cancionJSON.addProperty("titulo", this.titulo);
		cancionJSON.add("rolesXArtista", arrayRolesXIntegrantesJSON);
		cancionJSON.addProperty("costo", costo);
		return cancionJSON;
	}
}
