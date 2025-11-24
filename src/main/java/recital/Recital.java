package recital;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import artista.ArtistaBase;
import artista.ArtistaContratado;
import artista.BandaHistorico;
import artista.ComparadorArtistaPorCostoDeCancion;
import artista.ComparadorArtistaPorNombre;
import cancion.Cancion;
import cancion.IntegranteDeUnRol;
import prolog.IntegracionProlog;

public class Recital {
	private List<Cancion> repertorio;
	private List<ArtistaBase> lineUp;
	private Set<String> roles;

	public Recital(List<Cancion> repertorio, List<ArtistaBase> lineUp, Set<String> roles) {
		this.repertorio = repertorio;
		this.lineUp = lineUp;
		this.roles = roles;
		lineUp.sort(new ComparadorArtistaPorNombre());
	}

//	rolesFaltantesParaCancion = 1
	public Map<String, Integer> cantDeRolesFaltantesParaUnaCancion(int index) {
		if (index < 0 || index >= repertorio.size())
			throw new IllegalArgumentException("Los indices están fuera del limite permitido");
		Cancion cancion = repertorio.get(index);
		return cancion.getRolesFaltantesXCupos();
	}

//	rolesFaltantesParaTodasLasCanciones = 2,
	public Map<String, Integer> cantDeRolesFaltantesParaTodasLasCanciones() {
//		Uso un filter para quedarme con los artistas bases del Line Up
		List<ArtistaBase> artistasBase = lineUp.stream().filter(ArtistaBase::perteneceADiscografica).toList();
		Map<String, List<ArtistaBase>> artistasQueTienenRol = new HashMap<>();
//		Voy asignando a los artistas según los roles que tienen
		for (ArtistaBase artista : artistasBase) {
			for (String rol : artista.getRoles()) {
				if (artistasQueTienenRol.containsKey(rol))
					artistasQueTienenRol.get(rol).add(artista);
				else
					artistasQueTienenRol.put(rol, new ArrayList<>(List.of(artista)));
			}
		}
		Map<String, Integer> rolesFaltantesTotales = new HashMap<>();
//		Le mando el map "artistasQueTienenRol" para usarlo para un futuro ordenamiento por los roles q' tienen menos artistas
		ComparadoraDeArtistasXRoles comparadoraDeArtistasXRoles = new ComparadoraDeArtistasXRoles(artistasQueTienenRol);
		Set<ArtistaBase> artistasUsadosEnCancion = new HashSet<>();
		for (Cancion cancion : repertorio) {
			Map<String, Integer> rolesFaltantes = cancion.getRolesFaltantesXCupos();
			List<String> rolesDeCancion = new ArrayList<>(rolesFaltantes.keySet());
//			Ordeno x los roles que menos tienen artistas. Si no hay ningún artista con ese rol va a quedar primero en la lista
			rolesDeCancion.sort(comparadoraDeArtistasXRoles);
			for (String rol : rolesDeCancion) {
				int cupos = rolesFaltantes.get(rol);
				List<ArtistaBase> artistasQueTieneRol = artistasQueTienenRol.getOrDefault(rol, new ArrayList<>(0));
				for (int i = 0; i < artistasQueTieneRol.size() && cupos > 0; i++) {
					ArtistaBase artista = artistasQueTieneRol.get(i);
					if (!artistasUsadosEnCancion.contains(artista) && !artista.estaAsignadoACancion(cancion)) {
						artistasUsadosEnCancion.add(artista);
						cupos--;
					}
				}
				if (cupos > 0)
					rolesFaltantesTotales.merge(rol, cupos, Integer::sum);
			}
			artistasUsadosEnCancion.clear();
		}
		return rolesFaltantesTotales;
	}

//	contratarArtistasParaUnaCancion = 3, 
	public TransaccionAsignacionDeCancion contratarArtistasParaUnaCancion(int index) {
		if (index < 0 || index >= repertorio.size())
			throw new ArrayIndexOutOfBoundsException(
					"El index ingresado es inválido porque está fuera de los limites.");
		Cancion cancion = repertorio.get(index);
		TransaccionAsignacionDeCancion transaccion = new TransaccionAsignacionDeCancion(cancion);
		Map<String, IntegranteDeUnRol> candidatosXRol = cancion.getRolesFaltantes();
		List<ArtistaBase> listaDeArtistasDisponibles = lineUp.stream()
				.filter(artista -> artista.puedeSerAsignadoACancion() && !artista.estaAsignadoACancion(cancion))
				.collect(Collectors.toList());
//		Lo ordenamos por costo, asi ya no tenemos q preocuparnos por asignar primero a los contratados
		listaDeArtistasDisponibles.sort(new ComparadorArtistaPorCostoDeCancion());
		List<ArtistaBase> artistasUsados = new ArrayList<>();
		Map<String, List<ArtistaBase>> artistasQueTieneRol = new HashMap<>(candidatosXRol.size());
//		cargo los artistas y los voy encasillando en los roles
		for (ArtistaBase artista : listaDeArtistasDisponibles) {
			for (String rolDelArtista : artista.getRoles()) {
				if (candidatosXRol.containsKey(rolDelArtista)) {
					if (artistasQueTieneRol.containsKey(rolDelArtista))
						artistasQueTieneRol.get(rolDelArtista).add(artista);
					else
						artistasQueTieneRol.put(rolDelArtista, new ArrayList<>(List.of(artista)));
				}
			}
		}
		ComparadoraDeArtistasXRoles comparadoraDeArtistasXRoles = new ComparadoraDeArtistasXRoles(artistasQueTieneRol);
		List<String> rolesDisponiblesDeArtistas = new ArrayList<>(candidatosXRol.keySet());
		rolesDisponiblesDeArtistas.sort(comparadoraDeArtistasXRoles);
		boolean hayIntegrantesSuficientes = true;
		for (String rol : rolesDisponiblesDeArtistas) {
			int cupos = candidatosXRol.get(rol).getCantDeIntegrantesNecesarios();
			List<ArtistaBase> artistasDeEseRol = artistasQueTieneRol.getOrDefault(rol, new ArrayList<>(0));
			for (int i = 0; i < artistasDeEseRol.size() && cupos > 0; i++) {
				ArtistaBase artista = artistasDeEseRol.get(i);
				if (!artistasUsados.contains(artista)) {
					candidatosXRol.get(rol).agregarIntegrante(artista);
					artistasUsados.add(artista);
					cupos--;
				}
			}
			if (hayIntegrantesSuficientes && candidatosXRol.get(rol).hayCuposDisponibles())
				hayIntegrantesSuficientes = false;
		}

		if (!hayIntegrantesSuficientes) {// hay roles q no estan cubiertos !!!!!
			listaDeArtistasDisponibles.removeAll(artistasUsados);
			transaccion.registrarFallaEnAsignacion(candidatosXRol, listaDeArtistasDisponibles);
			return transaccion;
		}

		for (Map.Entry<String, IntegranteDeUnRol> nodo : candidatosXRol.entrySet()) {
			String rol = nodo.getKey();
			List<ArtistaBase> listaDeArtistas = nodo.getValue().getListaDeIntegrantes();
			listaDeArtistas.forEach(artista -> {
				cancion.agregarArtista(rol, artista);
			});
		}
		transaccion.confirmarTransaccion();
		return transaccion;
	}

//	contratarArtistasParaTodasLasCanciones = 4
	public TransaccionAsignacionDeTodasLasCanciones contratarArtistasParaTodasLasCanciones() {
		List<ArtistaBase> artistasDisponibles = lineUp.stream().filter(a -> a.puedeSerAsignadoACancion())
				.collect(Collectors.toList());
//		Al ordenarlo por costo, me van a quedar los artistas bases adelante de la lista.
		artistasDisponibles.sort(new ComparadorArtistaPorCostoDeCancion());
		Map<ArtistaBase, Integer> cantDisponibleDeCancionesDeArtistas = new HashMap<>();
		Map<String, List<ArtistaBase>> artistasQueTienenRol = new HashMap<>();
//		Encasillo todos los artistas en todos los roles que tienen. De esta manera ya sé cuáles son los roles que menos artistas tiene
		for (ArtistaBase artista : artistasDisponibles) {
			for (String rol : artista.getRoles()) {
				if (artistasQueTienenRol.containsKey(rol))
					artistasQueTienenRol.get(rol).addLast(artista);
				else
					artistasQueTienenRol.put(rol, new ArrayList<>(List.of(artista)));
			}
			if (!artista.perteneceADiscografica()) {
				ArtistaContratado artistaContratado = (ArtistaContratado) artista;
				cantDisponibleDeCancionesDeArtistas.put(artistaContratado,
						artistaContratado.getCantCancionesDisponiblesParaSerAsignado());
			}
		}
		Set<ArtistaBase> artistasUsadosEnCancion = new HashSet<>();
		Set<Cancion> cancionesConRolesFaltantes = new HashSet<>();
		Map<Cancion, Map<String, IntegranteDeUnRol>> artistasCandidatosAsignadosACancion = new HashMap<>();
		for (Cancion cancion : repertorio) {
			boolean hayArtistasSuficientes = true;
			Map<String, IntegranteDeUnRol> candidatosXRol = cancion.getRolesFaltantes();
			List<String> rolesDeCancion = new ArrayList<>(candidatosXRol.keySet());
			ComparadoraDeArtistasXRoles comparadoraDeArtistasXRoles = new ComparadoraDeArtistasXRoles(
					artistasQueTienenRol);
			rolesDeCancion.sort(comparadoraDeArtistasXRoles);
			for (String rol : rolesDeCancion) {
				int cupos = candidatosXRol.get(rol).getCantDeIntegrantesNecesarios();
				List<ArtistaBase> artistasDeEseRol = artistasQueTienenRol.getOrDefault(rol, new ArrayList<>(0));
				for (int i = 0; i < artistasDeEseRol.size() && cupos > 0; i++) {
					ArtistaBase artista = artistasDeEseRol.get(i);
					if (!artista.estaAsignadoACancion(cancion) && !artistasUsadosEnCancion.contains(artista)
							&& cantDisponibleDeCancionesDeArtistas.getOrDefault(artista, Integer.MAX_VALUE) > 0) {
//						Si no está en "artistasXCantDisponiblesDeCanciones" es porque es una artista BASE!!!!
						candidatosXRol.get(rol).agregarIntegrante(artista);
						artistasUsadosEnCancion.add(artista);
						cupos--;
						if (!artista.perteneceADiscografica())
							cantDisponibleDeCancionesDeArtistas.put(artista,
									cantDisponibleDeCancionesDeArtistas.get(artista) - 1);
					}
				}
				if (hayArtistasSuficientes && cupos > 0) {
					hayArtistasSuficientes = false;
					cancionesConRolesFaltantes.add(cancion);
				}
			}
			artistasCandidatosAsignadosACancion.put(cancion, candidatosXRol);
			artistasUsadosEnCancion.clear();
		}
		TransaccionAsignacionDeTodasLasCanciones transaccion = new TransaccionAsignacionDeTodasLasCanciones(
				artistasCandidatosAsignadosACancion);
		if (!cancionesConRolesFaltantes.isEmpty()) {
			transaccion.registrarFallaEnAsignacion(cancionesConRolesFaltantes, cantDisponibleDeCancionesDeArtistas,
					artistasDisponibles);
			return transaccion;
		}

		for (Map.Entry<Cancion, Map<String, IntegranteDeUnRol>> nodo : artistasCandidatosAsignadosACancion.entrySet()) {
			Cancion cancion = nodo.getKey();
			Map<String, IntegranteDeUnRol> integrantesXRol = nodo.getValue();
			for (Map.Entry<String, IntegranteDeUnRol> integranteXRol : integrantesXRol.entrySet()) {
				String rol = integranteXRol.getKey();
				List<ArtistaBase> artistasAAsignar = integranteXRol.getValue().getListaDeIntegrantes();
				artistasAAsignar.forEach(artista -> cancion.agregarArtista(rol, artista));
			}
		}
		transaccion.confirmarTransaccion();
		return transaccion;
	}

//	entrenarArtista = 5
	public boolean entrenarArtista(int index, String nuevoRol) {
		ArtistaBase artista = lineUp.get(index);
		if (artista.perteneceADiscografica())
			return false;// Solo se puede entrenar a artistas contratados!!!
		if (artista.getRoles().contains(nuevoRol))
			return false;// El artista ya tiene ese rol xd
		if (artista.estaAsignadoAlmenosAUnaCancion())
			return false;// No se puede entrenar a artistas que están asignados almenos a una cancion
		((ArtistaContratado) artista).entrenarNuevoRol(nuevoRol);
		return true;
	}

//	listarArtistasContratados = 6
	public String getInformacionDeArtistasContratados() {
		String str = "";
		for (ArtistaBase artista : lineUp)
			if (!artista.perteneceADiscografica())
				str += artista.toString() + "\n";
		return str;
	}

//	listarCanciones = 7
	public String getInformacionCompletaDelRepertorio() {
		String str = "";
		for (int i = 0; i < repertorio.size(); i++)
			str += repertorio.get(i).toString();
		return str;
	}

//	prolog = 10
	public int prolog() {
		IntegracionProlog.generarBaseDeConocimiento();
		return IntegracionProlog.consultarEntrenamientosMinimos();
	}

//	quitarArtistaDeCancion = 11
	public void quitarArtistaDeCancion(int indexArtista, int indexCancion) {
		if (indexCancion < 0 || indexCancion >= repertorio.size())
			throw new IllegalArgumentException("El índice de canción está fuera de los limites permitidos.");
		Cancion cancion = repertorio.get(indexCancion);
		if (indexArtista < 0 || indexArtista >= cancion.getListadoDeIntegrantes().size())
			throw new IllegalArgumentException("El índice de artista está fuera de los limites permitidos.");
		ArtistaBase artista = cancion.getListadoDeIntegrantes().get(indexArtista);
		cancion.quitarArtista(artista);
	}

//	quitarArtistaDeTodasLasCanciones = 12
	public boolean quitarArtistaDeTodasLasCanciones(String nombreDeArtista) {
		if (nombreDeArtista == null)
			throw new IllegalArgumentException("El nombre de artista no puede ser null.");
		ArtistaBase artista;
		boolean encontro = false;
		int i = 0;

		while (i < lineUp.size() && !encontro)
			if (lineUp.get(i).getNombre().compareTo(nombreDeArtista) == 0)
				encontro = true;
			else
				i++;
		if (!encontro)
			throw new RuntimeException("No hay ningun artista que tenga ese nombre");
		artista = lineUp.get(i);
		if (!artista.estaAsignadoAlmenosAUnaCancion())
			return false;
		artista.getListaDeCancionesEnLasQueEstaAsignado().forEach(cancion -> cancion.quitarArtista(artista));
		return true;
	}

//	quitarArtistaDelLineUp = 13
	public boolean quitarArtistaDelLineUp(int indexLineUp) {
		if (indexLineUp < 0 || indexLineUp >= lineUp.size())
			throw new IllegalArgumentException("El índice ingresado está fuera de los límites  permitidos.");
		ArtistaBase artista = lineUp.get(indexLineUp);
		if (artista.perteneceADiscografica())
			return false;
		if (artista.estaAsignadoAlmenosAUnaCancion())
			artista.getListaDeCancionesEnLasQueEstaAsignado().forEach(c -> c.quitarArtista(artista));
		lineUp.remove(indexLineUp);
		return true;
	}

//	guardarEstadoDelRecital = 14
	public void guardarEnArchivoJSON(String rutaArchivo) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonObject recitalJSON = new JsonObject();
		JsonArray repertorioJSON = new JsonArray(repertorio.size());
		repertorio.forEach(cancion -> repertorioJSON.add(cancion.toJSON()));
		JsonArray rolesJSON = new JsonArray(roles.size());
		roles.forEach(rol -> rolesJSON.add(rol));
		JsonArray lineUpJSON = new JsonArray(lineUp.size());
		lineUp.forEach(artista -> lineUpJSON.add(artista.toJson()));
		recitalJSON.add("roles", rolesJSON);
		recitalJSON.add("repertorio", repertorioJSON);
		recitalJSON.add("lineUp", lineUpJSON);
		try (FileWriter fileWriter = new FileWriter(rutaArchivo)) {
			gson.toJson(recitalJSON, fileWriter);
		}
	}

//	cargarEstadoDelRecital = 15
	public void cargarEstadoDeArchivoJSON(String rutaArch) throws FileNotFoundException {
		JsonObject jsonArch;
		try (FileReader fileReader = new FileReader(new File(rutaArch))) {
			jsonArch = JsonParser.parseReader(fileReader).getAsJsonObject();
		} catch (IOException e) {
			throw new FileNotFoundException("No se pudo leer el archivo: " + e.getMessage());
		}
		Set<String> rolesImportados = new HashSet<>();
		JsonArray rolesJSON = jsonArch.getAsJsonArray("roles");
		for (JsonElement rol : rolesJSON)
			rolesImportados.add(rol.getAsString());
		JsonArray lineUpJSON = jsonArch.getAsJsonArray("lineUp");
		Map<String, BandaHistorico> bancoBandas = new HashMap<>();
		List<ArtistaBase> lineUpImportado = new ArrayList<>();
		for (JsonElement e : lineUpJSON) {
			JsonArray bandasJSON = e.getAsJsonObject().get("bandas").getAsJsonArray();
			for (JsonElement b : bandasJSON) {
				String nombreBanda = b.getAsString();
				bancoBandas.putIfAbsent(nombreBanda, new BandaHistorico(nombreBanda));
			}
		}
		for (JsonElement e : lineUpJSON) {
			JsonObject artistaJSON = e.getAsJsonObject();
			String nombre = artistaJSON.get("nombre").getAsString();
			List<String> histRoles = new ArrayList<>();
			for (JsonElement r : artistaJSON.get("roles").getAsJsonArray())
				histRoles.add(r.getAsString());
			List<BandaHistorico> histBandas = new ArrayList<>();
			for (JsonElement b : artistaJSON.get("bandas").getAsJsonArray())
				histBandas.add(bancoBandas.get(b.getAsString()));
			JsonElement costoElem = artistaJSON.get("costo");
			ArtistaBase artista;
			if (costoElem != null && !costoElem.isJsonNull()) {
				double costo = costoElem.getAsDouble();
				JsonElement maxElem = artistaJSON.get("maxCanciones");
				int max = (maxElem != null && !maxElem.isJsonNull()) ? maxElem.getAsInt() : 0;
				artista = new ArtistaContratado(nombre, histRoles, histBandas, costo, max);
			} else {
				artista = new ArtistaBase(nombre, histRoles, histBandas);
			}
			lineUpImportado.add(artista);
		}
		List<Cancion> repertorioImportado = new ArrayList<>();
		JsonArray repertorioJSON = jsonArch.getAsJsonArray("repertorio");
		for (JsonElement elem : repertorioJSON) {
			JsonObject cancionJSON = elem.getAsJsonObject();
			String titulo = cancionJSON.get("titulo").getAsString();
			List<IntegranteDeUnRol> integrantesDeRol = new ArrayList<>();
			for (JsonElement rolElem : cancionJSON.get("rolesXArtista").getAsJsonArray()) {
				JsonObject rolJSON = rolElem.getAsJsonObject();
				String rol = rolJSON.get("rol").getAsString();
				JsonArray integrantesJSON = rolJSON.get("integrantes").getAsJsonArray();
				IntegranteDeUnRol integrantes = new IntegranteDeUnRol(rol, integrantesJSON.size());
				for (JsonElement art : integrantesJSON) {
					String nombreArt = art.getAsString();
					if (!nombreArt.equals("vacante")) {
						ArtistaBase artista = lineUpImportado.stream().filter(a -> a.getNombre().equals(nombreArt))
								.findFirst().orElse(null);
						if (artista != null)
							integrantes.agregarIntegrante(artista);
					}
				}
				integrantesDeRol.addLast(integrantes);
			}
			repertorioImportado.add(Cancion.crearCancionConIntegrantesAAsignar(titulo, integrantesDeRol));
		}
		this.roles = rolesImportados;
		this.lineUp = lineUpImportado;
		this.repertorio = repertorioImportado;
	}

	public List<String> getListadoDeIntegrantesDeCancion(int nombreDeCancion) {
		return repertorio.get(nombreDeCancion).getListadoDeIntegrantes().stream()
				.map(integrante -> integrante.getNombre()).toList();
	}

	public List<String> getListadoDeTitulosDeCanciones() {
		List<String> titulos = new ArrayList<>(repertorio.size());
		for (int i = 0; i < repertorio.size(); i++) {
			titulos.add(repertorio.get(i).getTitulo());
		}
		return titulos;
	}

	public Map<String, Integer> getTitulosDeCancionesConRolesDisponibles() {
		Map<String, Integer> canciones = new HashMap<>();
		for (int i = 0; i < repertorio.size(); i++) {
			if (repertorio.get(i).tieneRolesDisponibles())
				canciones.put(repertorio.get(i).getTitulo(), i);
		}
		return canciones;
	}

	public List<String> getListaDeNombresDeArtistasQueEstanAsignadosAlMenosACancion() {
		List<String> artistasAsignadosAUnaCancion = new ArrayList<>();
		for (ArtistaBase artista : lineUp) {
			if (artista.estaAsignadoAlmenosAUnaCancion())
				artistasAsignadosAUnaCancion.addLast(artista.getNombre());
		}
		return artistasAsignadosAUnaCancion;
	}

	public List<String> getListaDeRolesDisponiblesParaEntrenarArtista(int indexDelLineUp) {
		if (indexDelLineUp < 0 || indexDelLineUp >= lineUp.size())
			throw new IndexOutOfBoundsException("El índice del artista es inválido.");
		List<String> rolesDelArtista = lineUp.get(indexDelLineUp).getRoles();
		return roles.stream().filter(rol -> !rolesDelArtista.contains(rol)).toList();
	}

	public Map<String, Integer> getListadoArtistasContratadosSinSerAsignados() {
		Map<String, Integer> listado = new LinkedHashMap<>();
		for (int i = 0; i < lineUp.size(); i++) {
			ArtistaBase artista = lineUp.get(i);
			if (!artista.perteneceADiscografica() && !artista.estaAsignadoAlmenosAUnaCancion())
				listado.put(artista.getNombre(), i);
		}
		return listado;
	}

	public Map<String, Integer> getListadoArtistasContratados() {
		Map<String, Integer> listado = new LinkedHashMap<>();
		for (int i = 0; i < lineUp.size(); i++) {
			ArtistaBase artista = lineUp.get(i);
			if (!artista.perteneceADiscografica())
				listado.put(artista.getNombre(), i);
		}
		return listado;
	}

	public String getInformacionDeArtistasDeDiscografia() {
		String str = "";
		for (ArtistaBase artista : lineUp) {
			if (artista.perteneceADiscografica()) {
				str += artista.toString() + "\n";
			}
		}
		return str;
	}

	public String getInformacionDeLineUp() {
		String str = "";
		for (ArtistaBase artista : lineUp) {
			str += artista.toString() + "\n";
		}
		return str;
	}
}
