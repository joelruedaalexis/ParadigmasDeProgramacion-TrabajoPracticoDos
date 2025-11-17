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
//	private class ResultadoTransaccionContratacion{
//		Map<String, Integer> rolesXCantidadFaltante;
//		Map<Artista, List<String>> artistaXRolFaltante;
//		
//		public String getRecomendacionesArtistasEntrenables() {
//			return null;
//		}
//	}
	private List<Cancion> repertorio;
	private List<ArtistaBase> lineUp;
	private List<String> roles;
//	private Map<String, List<Artista>> todosLosRolesXArtista;
//	private ResultadoTransaccionContratacion resTransaccionContratacion;
//	private Map<String>

	public Recital(List<Cancion> repertorio, List<ArtistaBase> lineUp, List<String> roles) {
		this.repertorio = repertorio;
		this.lineUp = lineUp;
		this.roles = roles;
		lineUp.sort(new ComparadorArtistaPorNombre());
	}

//	rolesFaltantesParaCancion = 1
	public Map<String, Integer> cantDeRolesFaltantesParaUnaCancion(int index) {
		if (index < 0 || index >= repertorio.size())
			throw new IllegalArgumentException("Los indices están fuera del limite permitido");
//		Chequear si el index es válido y blablabla
		Cancion cancion = repertorio.get(index);
		return cancion.getRolesFaltantesXCupos();
	}

//	rolesFaltantesParaTodasLasCanciones = 2,
	public Map<String, Integer> cantDeRolesFaltantesParaTodasLasCanciones() {
		List<ArtistaBase> artistasBase = lineUp.stream().filter(ArtistaBase::perteneceADiscografica).toList();
		Map<String, List<ArtistaBase>> artistasXRol = new HashMap<>();
		for (ArtistaBase artista : artistasBase) {
			for (String rol : artista.getRoles()) {
				if (artistasXRol.containsKey(rol))
					artistasXRol.get(rol).add(artista);
				else
					artistasXRol.put(rol, new ArrayList<>(List.of(artista)));
			}
		}
		Map<String, Integer> rolesFaltantesTotales = new HashMap<>();
		ComparadoraDeArtistasXRoles comparadoraDeArtistasXRoles = new ComparadoraDeArtistasXRoles(artistasXRol);
		Set<ArtistaBase> artistasUsadosEnCancion = new HashSet<>();
		for (Cancion cancion : repertorio) {
			Map<String, Integer> rolesFaltantes = cancion.getRolesFaltantesXCupos();
			List<String> rolesDeCancion = new ArrayList<>(rolesFaltantes.keySet());
			rolesDeCancion.sort(comparadoraDeArtistasXRoles);
			for (String rol : rolesDeCancion) {
				int cupos = rolesFaltantes.get(rol);
				List<ArtistaBase> artistasQueTieneRol = artistasXRol.getOrDefault(rol, new ArrayList<>(0));
				for (int i = 0; i < artistasQueTieneRol.size() && cupos > 0; i++) {
					ArtistaBase artista = artistasQueTieneRol.get(i);
					if (!artistasUsadosEnCancion.contains(artista) && !artista.estaAsignadoACancion(cancion)) {
						artistasUsadosEnCancion.add(artista);
						cupos--;
					}
				}
				if (cupos > 0)// https://medium.com/@AlexanderObregon/javas-map-merge-method-explained-a2f0b5de9c8a
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

		TransaccionAsignacionDeCancion resultadoTransaccion = new TransaccionAsignacionDeCancion(cancion);
		Map<String, IntegranteDeUnRol> candidatosXRol = cancion.getRolesFaltantes();
		List<ArtistaBase> listaDeArtistasDisponibles = lineUp.stream()
				.filter(artista -> artista.puedeSerAsignadoACancion() && !cancion.artistaEstaAsignado(artista))
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
			resultadoTransaccion.registrarFallaEnAsignacion(candidatosXRol, listaDeArtistasDisponibles);
			return resultadoTransaccion;
		}

		for (Map.Entry<String, IntegranteDeUnRol> nodo : candidatosXRol.entrySet()) {
			String rol = nodo.getKey();
			List<ArtistaBase> listaDeArtistas = nodo.getValue().getListaDeIntegrantes();
			listaDeArtistas.forEach(artista -> {
				cancion.agregarArtista(rol, artista);
			});
		}
		resultadoTransaccion.confirmarTransaccion();
		return resultadoTransaccion;
	}

//	contratarArtistasParaTodasLasCanciones = 4
	public void contratarArtistasParaTodasLasCanciones() {// plantearlo
		Map<String, IntegranteDeUnRol> rolesXIntegrantesCandidatos = new HashMap<>();
		for (Cancion cancion : repertorio) {
			Map<String, IntegranteDeUnRol> rolesXIntegrantes = cancion.getRolesFaltantes();
//			if(roles)
		}

	}

//	entrenarArtista = 5
	public boolean entrenarArtista(int index, String nuevoRol) {
		if (index < 0 || index >= lineUp.size())
			return false;// Exception fuera del limite?
		ArtistaBase artista = lineUp.get(index);
		if (artista.perteneceADiscografica())
			return false;
		if (artista.getRoles().contains(nuevoRol))
			return false;// Exception?
		if (artista.estaAsignadoAlmenosAUnaCancion())
			return false;
//		System.out.println(artista);
		artista.entrenarNuevoRol(nuevoRol);
//		System.out.println(artista);
		return true;
	}

//	listarArtistasContratados = 6
	public String getInformacionDeArtistasContratados() {
		String str = "";
		for (ArtistaBase artista : lineUp)
			if (!artista.perteneceADiscografica())
				str += artista.toString() + "\n";
		return str;
//		return lineUp.stream().filter(a -> !a.perteneceADiscografica()).map(a -> a.toString()).toList();
	}

//	listarCanciones = 7
	public String getInformacionCompletaDelRepertorio() {
		String str = "";
		for (int i = 0; i < repertorio.size(); i++) {
			Cancion cancion = repertorio.get(i);
			double costoDeCancion = 0;
			for (ArtistaBase artista : cancion.getListadoDeIntegrantes())
				costoDeCancion += artista.getCosto();
			str += cancion.toString() + String.format("  Y su costo es de $%.02f\n", costoDeCancion);
		}
		return str;
//		return repertorio.stream().map(c -> c.toString()).toList();
	}

//	prolog = 8
	public void prolog() {
		IntegracionProlog.generarBaseDeConocimiento();
	}

//	quitarArtistaDeCancion = 9
	public void quitarArtistaDeCancion(int indexArtista, int indexCancion) {
		if (indexCancion < 0 || indexCancion >= repertorio.size())
			throw new IllegalArgumentException("El índice de canción está fuera de los limites permitidos.");
		Cancion cancion = repertorio.get(indexCancion);
		if (indexArtista < 0 || indexArtista >= cancion.getListadoDeIntegrantes().size())
			throw new IllegalArgumentException("El índice de artista está fuera de los limites permitidos.");
		ArtistaBase artista = cancion.getListadoDeIntegrantes().get(indexArtista);
		cancion.quitarArtista(artista);
	}

//	quitarArtistaDeTodasLasCanciones = 10
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
		if (!encontro/* && i == lineUp.size() */)// borrar la 2da condicion!!!!
			throw new RuntimeException("No hay ningun artista que tenga ese nombre"); // CREAR EXCEPTION -> el artista
																						// con ese nombre no existe
		artista = lineUp.get(i);
		if (!artista.estaAsignadoAlmenosAUnaCancion())
			return false;
		repertorio.stream().filter(cancion -> cancion.artistaEstaAsignado(artista)).forEach(cancion -> {
			cancion.quitarArtista(artista);
		});
		return true;
	}

//	quitarArtistaDelLineUp = 11
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

//	guardarEstadoDelRecital = 12
	public void guardarEnArchivoJSON(String rutaArchivo) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonObject recitalJSON = new JsonObject();
		JsonArray repertorioJSON = new JsonArray(repertorio.size());
		JsonArray arrayRolesJSON = new JsonArray(roles.size());
		JsonArray lineUpJSON = new JsonArray(lineUp.size());
		repertorio.forEach(cancion -> repertorioJSON.add(cancion.toJSON()));
		roles.forEach(rol -> arrayRolesJSON.add(rol));
		lineUp.forEach(artista -> lineUpJSON.add(artista.toJson()));

		recitalJSON.add("roles", arrayRolesJSON);
		recitalJSON.add("repertorio", repertorioJSON);
		recitalJSON.add("lineUp", lineUpJSON);
		try {
			FileWriter fileWriter = new FileWriter(rutaArchivo);
//			System.out.println(gson.toJson(repertorio));
			gson.toJson(recitalJSON, fileWriter);
			fileWriter.close();
		} catch (IOException e) {
			throw new IOException("Error en escribir el archivo");
		}
	}

//	cargarEstadoDelRecital = 13
	public void cargarEstadoDeArchivoJSON(String rutaArch) throws FileNotFoundException {
		FileReader fileReader = new FileReader(new File(rutaArch));
		JsonObject jsonArch = JsonParser.parseReader(fileReader).getAsJsonObject();

		JsonArray repositorioRolesJSON = jsonArch.getAsJsonArray("roles");
		List<String> rolesImportados = new ArrayList<>(repositorioRolesJSON.size());
		for (JsonElement rolElement : repositorioRolesJSON) {
			String rol = rolElement.getAsString();
			rolesImportados.addLast(rol);
		}

		// Copy paste de importacion
		List<ArtistaBase> lineUpImportado = new ArrayList<>();
		Map<String, BandaHistorico> repositorioBanda = new HashMap<>();// Cambiarlo por una lista o SET maybe
//		Map<String, List<Artista>> bandaXIntegrantes = new HashMap<>();
		ArtistaBase artista;
		JsonArray lineUpJSON = jsonArch.getAsJsonArray("lineUp");

		for (JsonElement jsonArtistaElement : lineUpJSON) {
			JsonObject jsonArtistaObject = jsonArtistaElement.getAsJsonObject();
			for (JsonElement jsonRolElement : jsonArtistaObject.get("bandas").getAsJsonArray()) {
				String banda = jsonRolElement.getAsString();
				if (!repositorioBanda.containsKey(banda)) {
//					bandaXIntegrantes.put(banda, new ArrayList<>());
					repositorioBanda.put(banda, new BandaHistorico(banda));
//					repositorioBanda.put(banda, new BandaHistorico(banda, bandaXIntegrantes.get(banda)));
				}
			}
		}
		for (JsonElement jsonArtistaElement : lineUpJSON) {
			JsonObject jsonArtistaObject = jsonArtistaElement.getAsJsonObject();
			String nombreDelArtista = jsonArtistaObject.get("nombre").getAsString();
			List<String> historicoDeRoles = new ArrayList<>(jsonArtistaObject.get("roles").getAsJsonArray().size());
			for (JsonElement jsonRolElement : jsonArtistaObject.get("roles").getAsJsonArray()) {
				String rol = jsonRolElement.getAsString();
				historicoDeRoles.add(rol);
			}
			List<BandaHistorico> historicoDeBanda = new ArrayList<>();

			for (JsonElement jsonRolElement : jsonArtistaObject.get("bandas").getAsJsonArray()) {
				String nombreBanda = jsonRolElement.getAsString();
				historicoDeBanda.add(repositorioBanda.get(nombreBanda));
			}

			if (jsonArtistaObject.get("costo").getAsDouble() == 0)
				artista = new ArtistaBase(nombreDelArtista, historicoDeRoles, historicoDeBanda);
			else {
				double costoXCancion = jsonArtistaObject.get("costo").getAsDouble();
				int maxCanciones = jsonArtistaObject.get("maxCanciones").getAsInt();
				artista = new ArtistaContratado(nombreDelArtista, historicoDeRoles, historicoDeBanda, costoXCancion,
						maxCanciones);
			}
//			for (BandaHistorico banda : historicoDeBanda) {
//				bandaXIntegrantes.get(banda.getNombre()).add(artista);
//			}
			lineUpImportado.addLast(artista);
		}
//		System.out.println(lineUpImportado);
		JsonArray repertorioJSON = jsonArch.get("repertorio").getAsJsonArray();
		List<Cancion> repertorioImportado = new ArrayList<>();
		for (JsonElement jsonCancionElement : repertorioJSON) {
			JsonObject jsonCancionObject = jsonCancionElement.getAsJsonObject();
			String titulo = jsonCancionObject.get("titulo").getAsString();
//			List<String> roles1 = new ArrayList<>();
			Map<String, IntegranteDeUnRol> rolesXIntegrantes = new HashMap<>();
			for (JsonElement jsonRolElement : jsonCancionObject.get("rolesXArtista").getAsJsonArray()) {
				JsonObject rolesXIntegrantesJSON = jsonRolElement.getAsJsonObject();
				String rol = rolesXIntegrantesJSON.get("rol").getAsString();
				JsonArray integrantesJSON = rolesXIntegrantesJSON.get("integrantes").getAsJsonArray();
				int cupos = integrantesJSON.size();
				IntegranteDeUnRol integrantesDeRol = new IntegranteDeUnRol(cupos);
				for (JsonElement nombreArtistaElement : integrantesJSON) {
					String nombreArtista1 = nombreArtistaElement.getAsString();
					boolean encontroArtista = false;
					int i = 0;
					System.out.println("-->" + nombreArtista1);
					if (!nombreArtista1.equals("vacante")) {
						while (i < lineUpImportado.size() && !encontroArtista) {
							if (lineUpImportado.get(i).getNombre().equals(nombreArtista1))
								encontroArtista = true;
							else
								i++;
						}
						System.out.println(rolesXIntegrantes);
						integrantesDeRol.agregarIntegrante(lineUpImportado.get(i));
					}
				}
				rolesXIntegrantes.put(rol, integrantesDeRol);
			}
			repertorioImportado.add(new Cancion(titulo, rolesXIntegrantes));
		}
		this.roles = rolesImportados;
		this.repertorio = repertorioImportado;
		this.lineUp = lineUpImportado;
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

	public List<String> getListaDeNombresDeArtistasQueEstanAsignadosAlMenosACancion() {
		List<String> artistasAsignadosAUnaCancion = new ArrayList<>();
		for (Cancion cancion : repertorio) {
			List<ArtistaBase> integrantesDeCancion = cancion.getListadoDeIntegrantes();
			for (int i = 0; i < integrantesDeCancion.size() && integrantesDeCancion.get(i) != null; i++) {
				if (!artistasAsignadosAUnaCancion.contains(integrantesDeCancion.get(i).getNombre())) {
					artistasAsignadosAUnaCancion.add(integrantesDeCancion.get(i).getNombre());
				}
			}
		}
		return artistasAsignadosAUnaCancion;
	}

	public List<String> getListaDeRolesDisponiblesParaEntrenarArtista(int indexDelLineUp) {
		if (indexDelLineUp < 0 || indexDelLineUp >= lineUp.size())
			return null;// Exception out of bonds
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
//		System.out.println(listado.size());s
		return listado;
	}

	public Map<String, Integer> getListadoArtistasContratados() {
		Map<String, Integer> listado = new LinkedHashMap<>();
		for (int i = 0; i < lineUp.size(); i++) {
			ArtistaBase artista = lineUp.get(i);
			if (!artista.perteneceADiscografica())
				listado.put(artista.getNombre(), i);
		}
//		System.out.println(listado.size());s
		return listado;
	}

//	private List<String> getListadoCancionesEnLasQueArtistaEstaAsignado(String nombreDeArtista) {
//		Artista artista;
//		boolean encontro = false;
//		int i;
//		for (i = 0; i < lineUp.size() && !encontro; i++)
//			if (lineUp.get(i).getNombre().compareTo(nombreDeArtista) == 0)
//				encontro = true;
//		if (!encontro)// borrar la 2da condicion!!!!
//			return null; // el artista con ese nombre no existe
//		artista = lineUp.get(i);
//		repertorio.stream().filter(cancion -> cancion.artistaEstaAsignado(artista)).forEach(cancion -> {
//			cancion.quitarArtista(artista);
//			artista.designar(cancion);
//		});
//		return null;
//	}
}
