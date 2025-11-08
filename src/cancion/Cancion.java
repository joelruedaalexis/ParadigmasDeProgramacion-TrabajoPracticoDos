package cancion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import artista.Artista;

public class Cancion {
	private String titulo = null;
	private Map<String, List<Artista>> rolesXIntegrantes;
	private Map<String, Integer> rolesXCuposDeIntegrantes;

//	public Cancion(String titulo, Map<String, List<Artista>> rolesXIntegrantes) {
//		this.titulo = titulo;
//		this.rolesXIntegrantes = rolesXIntegrantes;
//	}
	public Cancion(String titulo, Map<String, Integer> rolesXCuposDeIntegrantes) {
		this.titulo = titulo;
		this.rolesXCuposDeIntegrantes = rolesXCuposDeIntegrantes;
		this.inicializarRolesXIntegrantes();
	}

	private void inicializarRolesXIntegrantes() {
		this.rolesXIntegrantes = new HashMap<>();
		for (Map.Entry<String, Integer> rolXCupo : rolesXCuposDeIntegrantes.entrySet()) {
			String rol = rolXCupo.getKey();
			Integer cupos = rolXCupo.getValue();
			rolesXIntegrantes.put(rol, new ArrayList<Artista>(cupos));
		}
	}

	public boolean artistaEstaAsignado(Artista artista) {
		if (artista == null)
			return false;// Exception?
		for (Map.Entry<String, List<Artista>> nodo : rolesXIntegrantes.entrySet()) {
			List<Artista> listaArtistasDeRol = nodo.getValue();
			if (listaArtistasDeRol.contains(artista))
				return true;
		}
		return false;
	}

	// Rol 3 cantantes
	// List<> = [null,null,pepe] ó [pepe,null,null]
	public int cantDeRolesFaltantes() {
		int cant = 0;
		for (Integer cuposDeRol : rolesXCuposDeIntegrantes.values()) {
			cant += cuposDeRol;
		}

//		for (Map.Entry<String, List<Artista>> entry : rolesXIntegrantes.entrySet()) {
//			List<Artista> listaArtistasDeRol = entry.getValue();
//			// [pepe,null,null]
//			for (int i = listaArtistasDeRol.size() - 1; i >= 0 && listaArtistasDeRol.get(i) == null; i--) {
//				cant++;
//			}
//		}
		return cant;
//		return rolesXCuposDeIntegrantes.values().stream().mapToInt(cupos -> cupos).sum();
	}

	public List<Artista> getListadoDeIntegrantes() {
		List<Artista> listadoDeIntegrantes = new ArrayList<>();

		for (Map.Entry<String, List<Artista>> rolXIntegrante : rolesXIntegrantes.entrySet()) {
			List<Artista> listaIntegrantesDeRol = rolXIntegrante.getValue();
			listadoDeIntegrantes.addAll(listaIntegrantesDeRol);

		}
//		for (Map.Entry<String, List<Artista>> rolXIntegrante : rolesXIntegrantes.entrySet()) {
//			List<Artista> listaIntegrantesDeRol = rolXIntegrante.getValue();
//			for (int i = 0; i < listaIntegrantesDeRol.size() && listaIntegrantesDeRol.get(i) != null; i++)
//				listadoDeIntegrantes.addLast(listaIntegrantesDeRol.get(i));
//		}
//		System.out.println(listadoDeIntegrantes);
		// Y si no hay integrantes ? throw Exception?
		return listadoDeIntegrantes;
	}

	public void agregarArtista(String rol, Artista artista) {
		if (!rolesXIntegrantes.containsKey(rol))
			return;// Exception?
		if (rolesXCuposDeIntegrantes.get(rol) == 0)
			return;// ya estan asignados todos los artistas a ese rol!!!!
		this.disminuirCupoDeRol(rol);
		rolesXIntegrantes.get(rol).addLast(artista);
//		List<Artista> listaIntegrantes = rolesXIntegrantes.get(rol);
//		if (listaIntegrantes.getLast() != null)// [pepe,null,null]
//			return;// ya estan asignados todos los artistas a ese rol!!!!
//		listaIntegrantes.addFirst(artista);
//		listaIntegrantes.removeLast();// Sacamos el null
	}

	private void aumentarCupoDeRol(String rol) {
		rolesXCuposDeIntegrantes.put(rol, rolesXCuposDeIntegrantes.get(rol) + 1);
	}

	private void disminuirCupoDeRol(String rol) {
		rolesXCuposDeIntegrantes.put(rol, rolesXCuposDeIntegrantes.get(rol) - 1);
	}

	public void quitarArtista(Artista artista) {
		for (Map.Entry<String, List<Artista>> nodo : rolesXIntegrantes.entrySet()) {
			List<Artista> listaArtistasDeRol = nodo.getValue();
			boolean pudoQuitarArtista = false;
			for (int i = 0; i < listaArtistasDeRol.size() && !pudoQuitarArtista; i++) {
				if (listaArtistasDeRol.contains(artista)) {
					artista.designar(this);
					listaArtistasDeRol.remove(artista);
					this.aumentarCupoDeRol(nodo.getKey());
					pudoQuitarArtista = true;
				}
			}
		}
//		List<List<Artista>> listaRolesXIntegrantes = new ArrayList<>(rolesXIntegrantes.values());
//		boolean pudoQuitarArtista = false;
//		for (int i = 0; i < listaRolesXIntegrantes.size() && !pudoQuitarArtista; i++) {
//			List<Artista> listaArtistasDeRol = listaRolesXIntegrantes.get(i);
//			if (listaArtistasDeRol.contains(artista)) {
//				artista.designar(this);
//				listaArtistasDeRol.remove(artista);// [pepe,null,null] sacamos pepe
//				listaArtistasDeRol.addLast(null);// [null,null,null]
//				pudoQuitarArtista = true;
//			}
//		}
	}

	public List<String> getRoles() {
		List<String> roles = new ArrayList<>();
		for (Map.Entry<String, List<Artista>> nodo : rolesXIntegrantes.entrySet()) {
			String rol = nodo.getKey();
			List<Artista> artistas = nodo.getValue();
			for (int i = 0; i < artistas.size(); i++)
				roles.add(rol);
		}
		return roles;
	}

	public Map<String, Integer> getRolesDisponiblesAAsignar() {
//		Map<String, Integer> rolesDisponiblesXCantidad = new HashMap<>();
//		for (Map.Entry<String, List<Artista>> nodo : rolesXIntegrantes.entrySet()) {
//			String rol = nodo.getKey();
//			List<Artista> listaArtistasSinAsignar = nodo.getValue().stream().filter(artista -> artista == null)
//					.toList();
//			rolesDisponiblesXCantidad.put(rol, listaArtistasSinAsignar.size());
//		}
		return new HashMap<>(rolesXCuposDeIntegrantes);
	}

	public String getTitulo() {
		return this.titulo;
	}

	public double getCostoDeCancion() {
		double costo = 0;
		for (Map.Entry<String, List<Artista>> rolXIntegrante : rolesXIntegrantes.entrySet()) {
			List<Artista> listaIntegrantesDeRol = rolXIntegrante.getValue();
			for (int i = 0; i < listaIntegrantesDeRol.size(); i++) {
				costo += listaIntegrantesDeRol.get(i).getCosto();
			}
		}
		return costo;
	}

	private String integrantesToString(String rol, List<Artista> lista) {
		int lugaresDisponibles = rolesXCuposDeIntegrantes.get(rol);
		String str = "";

		str += lista.isEmpty() ? "disponible" : lista.getFirst().getNombre();
		for (int i = 1; i < lista.size(); i++)
			str += ", " + lista.get(i).getNombre();
		if (lugaresDisponibles > 1) {
			lugaresDisponibles--;
			str += String.format(", %s", "disponible").repeat(lugaresDisponibles);
		}

//		if (lugaresDisponibles == 0 || lista == null)
//			return str;
//		str += lista.getFirst().getNombre();
//		for (int i = 1; i < lista.size(); i++)
//			str += ", " + lista.get(i).getNombre();
//		str += String.format(", %s", "disponible");
		return str;
	}

	@Override
	public String toString() {
		String str = "->La canción \"" + titulo + "\" está constituida por:\n";
		for (Map.Entry<String, List<Artista>> nodo : rolesXIntegrantes.entrySet()) {
			String rol = nodo.getKey();
			List<Artista> artistas = nodo.getValue();
//			str += String.format("\t%c%s= ", 158, rol);
//			str += String.format("\t%c%s= %s\n", 158, rol, integrantesToString(artistas));
			str += String.format("\t~%s= %s\n", rol, integrantesToString(rol, artistas));
		}
		return str;
	}

	public JsonObject toJSON() {
		double costo = 0;
		JsonObject cancionJSON = new JsonObject();
		JsonArray arrayRolesXIntegrantesJSON = new JsonArray();
		for (Map.Entry<String, List<Artista>> rolXIntegrante : rolesXIntegrantes.entrySet()) {
			String rol = rolXIntegrante.getKey();
			List<Artista> listaIntegrantesDeRol = rolXIntegrante.getValue();
			int lugaresDisponibles = rolesXCuposDeIntegrantes.get(rol);
			JsonObject rolXIntegranteJSON = new JsonObject();
			JsonArray arrayIntegrantes = new JsonArray(listaIntegrantesDeRol.size() + lugaresDisponibles);
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
//		for (Map.Entry<String, List<Artista>> rolXIntegrante : rolesXIntegrantes.entrySet()) {
//			String rol = rolXIntegrante.getKey();
//			List<Artista> listaIntegrantesDeRol = rolXIntegrante.getValue();
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
