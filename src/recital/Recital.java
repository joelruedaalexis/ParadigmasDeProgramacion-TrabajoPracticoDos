package recital;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import artista.Artista;
import artista.ArtistaBase;
import artista.ArtistaContratado;
import artista.ComparadorArtistaPorCostoDeCancion;
import artista.ComparadorArtistaPorNombre;
import banda.BandaHistorico;
import cancion.Cancion;
import cancion.IntegranteDeRol;

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
	private List<Artista> lineUp;
	private List<String> roles;
//	private Map<String, List<Artista>> todosLosRolesXArtista;
//	private ResultadoTransaccionContratacion resTransaccionContratacion;
//	private Map<String>

	public Recital(List<Cancion> repertorio, List<Artista> lineUp, List<String> roles) {
		this.repertorio = repertorio;
		this.lineUp = lineUp;
		this.roles = roles;
		lineUp.sort(new ComparadorArtistaPorNombre());
	}

//	rolesFaltantesParaCancion = 1
	public String cantDeRolesFaltantesParaUnaCancion(int index) {
		// Chequear si el index es válido y blablabla
		Cancion cancion = repertorio.get(index);
		Map<String, Integer> rolesFaltantesXCupos = cancion.getRolesFaltantesXCupos();
		if (rolesFaltantesXCupos.isEmpty())
			return String.format("Todos los roles de la canción \"%s\"ya han sido asignados.", cancion.getTitulo());

		String str = String.format("A la canción \"%s\" le faltan los siguientes roles a asignar:\n",
				cancion.getTitulo());
		for (Map.Entry<String, Integer> nodo : rolesFaltantesXCupos.entrySet()) {
			String rol = nodo.getKey();
			Integer cupos = nodo.getValue();
			str += String.format("\t~%d %s.\n", cupos, rol);
		}
		return str;
	}

//	rolesFaltantesParaTodasLasCanciones = 2,
	public int cantDeRolesFaltantesParaTodasLasCanciones() {
//		return repertorio.stream().mapToInt(cancion -> cancion.cantDeRolesFaltantes()).sum();
		return repertorio.stream().mapToInt(cancion -> cancion.cantDeCuposDisponibles()).sum();
	}

//	public void cargarMapTodosLosRolesXArtista() {
//		for (Artista artista : lineUp) {
//			List<String> roles = artista.getRoles();
//			for (String rol : roles) {
//				if (!todosLosRolesXArtista.containsKey(rol))
//					todosLosRolesXArtista.put(rol, new ArrayList<>());
//				todosLosRolesXArtista.get(rol).add(artista);
//			}
//		}
//		this.ordenarMapTodosLosRolesXArtista();
//	}

//	public void ordenarMapTodosLosRolesXArtista() {
//		for (List<Artista> listaArtistas : todosLosRolesXArtista.values())
//			listaArtistas.sort(new ComparadorArtistaPorCostoDeCancion());
//	}

//	contratarArtistasParaUnaCancion = 3, 
	public TransaccionAsignacionDeCancion contratarArtistasParaUnaCancion(int index) {
		if (index < 0 || index >= repertorio.size())
			return null;// exception? el indice está por afuera de los limites del array
		Cancion cancion = repertorio.get(index);

		TransaccionAsignacionDeCancion resultadoTransaccion = new TransaccionAsignacionDeCancion(cancion);

		Map<String, IntegranteDeRol> rolesXIntegrantesCandidatos = cancion.getRolesConCuposDeIntegrantes();

//		Map<String, Integer> rolesXCantidadNecesaria = cancion.getRolesConCuposDeIntegrantes();
//		Map<String, Integer> rolesXCantidadNecesaria = new HashMap<>();
//		Map<String, Integer> rolesXCantidadFaltante = new HashMap<>();
//		Map<String, List<Artista>> rolesXArtistasCandidatos = new HashMap<>();

		List<Artista> listaDeArtistasDisponibles = lineUp.stream()
				.filter(artista -> artista.puedeSerAsignadoACancion() && !cancion.artistaEstaAsignado(artista))
				.collect(Collectors.toList());
		listaDeArtistasDisponibles.sort(new ComparadorArtistaPorCostoDeCancion());

		boolean hayIntegrantesInsuficientes = false;
//		Map<String,IntegranteDeRol> rolesXIntegrantesCandidatos

		for (Map.Entry<String, IntegranteDeRol> nodo : rolesXIntegrantesCandidatos.entrySet()) {
			String rol = nodo.getKey();
			IntegranteDeRol integrantesDeRol = nodo.getValue();

			int i = 0;
			while (i < listaDeArtistasDisponibles.size() && integrantesDeRol.hayCuposDisponibles()) {
				Artista artista = listaDeArtistasDisponibles.get(i);
				if (artista.tieneRol(rol)) {
					integrantesDeRol.agregarIntegrante(artista);
					listaDeArtistasDisponibles.remove(i);
				} else
					i++;
			}
			if (!hayIntegrantesInsuficientes && integrantesDeRol.hayCuposDisponibles())
//				No hay artistas SUFICIENTES que tengan ese rol!!!														
				hayIntegrantesInsuficientes = true;
		}

		if (hayIntegrantesInsuficientes) {
//			hay roles q no estan cubiertos !!!!!
			System.out.println("XDDDDDDDDDDDDDD");
			resultadoTransaccion.cancelarTransaccion(rolesXIntegrantesCandidatos, listaDeArtistasDisponibles);
			return resultadoTransaccion;
		}

		for (Map.Entry<String, IntegranteDeRol> nodo : rolesXIntegrantesCandidatos.entrySet()) {
			String rol = nodo.getKey();
			List<Artista> listaDeArtistas = nodo.getValue().getListaDeIntegrantes();
			listaDeArtistas.forEach(artista -> {
				System.out.println(cancion.agregarArtista(rol, artista));
				;
				artista.asignar(cancion);
			});
		}
		System.out.println(cancion);
		resultadoTransaccion.confirmarTransaccion();
		return resultadoTransaccion;
	}

//	contratarArtistasParaTodasLasCanciones = 4
	public void contratarArtistasParaTodasLasCanciones() {// plantearlo
		for (int i = 0; i < repertorio.size(); i++) {// borrar esto
			this.contratarArtistasParaUnaCancion(i);
		}
	}

//	entrenarArtista = 5
	public boolean entrenarArtista(int index, String nuevoRol) {
		if (index < 0 || index >= lineUp.size())
			return false;// Exception fuera del limite?
		Artista artista = lineUp.get(index);
		if (artista.perteneceADiscografica())
			return false;
		if (artista.getRoles().contains(nuevoRol))
			return false;// Exception?
		if (artista.estaAsignadoAUnaCancion())
			return false;
//		System.out.println(artista);
		artista.entrenarNuevoRol(nuevoRol);
//		System.out.println(artista);
		return true;
	}

//	listarArtistasContratados = 6
	public String getInformacionDeArtistasContratados() {
		String str = "";
		for (Artista artista : lineUp)
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
			for (Artista artista : cancion.getListadoDeIntegrantes())
				costoDeCancion += artista.getCosto();
			str += cancion.toString() + String.format("  Y su costo es de $%.02f\n", costoDeCancion);
		}
		return str;
//		return repertorio.stream().map(c -> c.toString()).toList();
	}

//	prolog = 8
	public void prolog() {

	}

//	quitarArtistaDeCancion = 9
	public void quitarArtistaDeCancion(int indexArtista, int indexCancion) {
		if (indexArtista < 0 || indexArtista >= lineUp.size()) {
			System.out.println("indexArtista");
			return;// exception out of bonds o algo así
		}
		if (indexCancion < 0 || indexCancion >= repertorio.size()) {
			System.out.println("indexCancion");
			return;// lo mismo
		}
		Cancion cancion = repertorio.get(indexCancion);
		Artista artista = cancion.getListadoDeIntegrantes().get(indexArtista);
		cancion.quitarArtista(artista);
	}

//	quitarArtistaDeTodasLasCanciones = 10
	public void quitarArtistaDeTodasLasCanciones(String nombreDeArtista) {
		Artista artista;
		boolean encontro = false;
		int i;
		for (i = 0; i < lineUp.size() && !encontro; i++)
			if (lineUp.get(i).getNombre().compareTo(nombreDeArtista) == 0)
				encontro = true;
		if (!encontro/* && i == lineUp.size() */)// borrar la 2da condicion!!!!
			return; // el artista con ese nombre no existe
		artista = lineUp.get(i);
		repertorio.stream().filter(cancion -> cancion.artistaEstaAsignado(artista)).forEach(cancion -> {
			cancion.quitarArtista(artista);
		});
	}

//	quitarArtistaDelLineUp = 11
	public void quitarArtistaDelLineUp(int indexLineUp) {
//		chequear que el index esté dentro de un rango válido
		Artista artista = lineUp.get(indexLineUp);
//		chequear que el artista NO PERTENEZCA a la discografica
		if (artista.estaAsignadoAUnaCancion())
			artista.getListaDeCancionesEnLasQueEstaAsignado().forEach(c -> c.quitarArtista(artista));
		lineUp.remove(indexLineUp);
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
		Map<String, BandaHistorico> repositorioBandaHistorico = new HashMap<>();
		JsonObject jsonArch = JsonParser.parseReader(fileReader).getAsJsonObject();

		JsonArray repositorioRolesJSON = jsonArch.getAsJsonArray("roles");
		List<String> repositorioRoles = new ArrayList<>(repositorioRolesJSON.size());
		for (JsonElement rolElement : repositorioRolesJSON) {
			String rol = rolElement.getAsJsonObject().getAsString();
			repositorioRoles.addLast(rol);
		}

		JsonArray lineUpJSON = jsonArch.getAsJsonArray("lineUp");
		List<Artista> lineUpImportado = new ArrayList<>(lineUpJSON.size());
		Map<String, Artista> repositorioArtistas = new HashMap<>(lineUpJSON.size());
		for (JsonElement jsonElement : lineUpJSON) {
			JsonObject artistaJSON = jsonElement.getAsJsonObject();

			String nombreDelArtista = artistaJSON.get("nombre").getAsString();
			List<String> roles = new ArrayList<>(artistaJSON.get("roles").getAsJsonArray().size());
			for (JsonElement rolElement : artistaJSON.get("roles").getAsJsonArray()) {
				String rol = rolElement.getAsString();
				roles.add(rol);
			}

			List<BandaHistorico> bandasHistorico = new ArrayList<>(artistaJSON.get("bandas").getAsJsonArray().size());
			for (JsonElement bandaElement : artistaJSON.get("bandas").getAsJsonArray()) {
				String nombreBanda = bandaElement.getAsString();
				BandaHistorico banda;
				if (repositorioBandaHistorico.containsKey(nombreBanda)) {
					banda = new BandaHistorico(nombreBanda);
					repositorioBandaHistorico.put(nombreBanda, banda);
				} else
					banda = repositorioBandaHistorico.get(nombreBanda);
				bandasHistorico.add(banda);
			}
			Artista artista;
			if (artistaJSON.get("costo").getAsDouble() == 0) {
//				esBase
				artista = new ArtistaBase(nombreDelArtista, roles, bandasHistorico);
				lineUpImportado.addLast(artista);
			} else {
//				esContratado
				Double costo = artistaJSON.get("costo").getAsDouble();
				int maxCanciones = artistaJSON.get("maxCanciones").getAsInt();
				artista = new ArtistaContratado(nombreDelArtista, roles, bandasHistorico, costo, maxCanciones);
				lineUpImportado.addLast(artista);
			}
			repositorioArtistas.put(nombreDelArtista, artista);

			for (BandaHistorico banda : bandasHistorico)
				banda.agregarArtista(artista);

		}
//		"lineUp": [
//		           {
//		             "nombre": "Agustin Cruz",
//		             "roles": [
//		               "voz principal",
//		               "saxofón",
//		               "armónica"
//		             ],
//		             "bandas": [
//		               "Acru"
//		             ],
//		             "costo": 500.0,
//		             "maxCanciones": 2
//		           },

		JsonArray repertorioJSON = jsonArch.get("repertorio").getAsJsonArray();
		List<Cancion> repertorioImportado = new ArrayList<>(repertorioJSON.size());
		for (JsonElement cancionElement : repertorioJSON) {
			JsonObject cancionJSON = cancionElement.getAsJsonObject();

			String nombre = cancionJSON.get("titulo").getAsString();
		}
//		{
//		      "titulo": "O tempo nao para",
//		      "rolesXArtista": [
//		        {
//		          "rol": "voz secundaria",
//		          "integrantes": [
//		            "Roger Taylor",
//		            "Brian May"
//		          ]
//		        },
//		        {
//		          "rol": "batería",
//		          "integrantes": [
//		            "Roberto Musso"
//		          ]
//		        }
//		      ],
//		      "costo": 225.0
//		    },

//		List<Artista> lineUp = new ArrayList<>();
//		JsonArray json = JsonParser.parseReader(fileReader).getAsJsonArray();
//		Map<String, BandaHistorico> repositorioBanda = new HashMap<>();
//		Artista artista;
//
//		for (JsonElement jsonArtistaElement : json) {
//			JsonObject jsonArtistaObject = jsonArtistaElement.getAsJsonObject();
//			String nombreDelArtista = jsonArtistaObject.get("nombre").getAsString();
//			List<BandaHistorico> historicoDeBanda = new ArrayList<>();
//			List<String> historicoDeRoles = new ArrayList<>();
//			for (JsonElement jsonRolElement : jsonArtistaObject.get("roles").getAsJsonArray()) {
//				String rol = jsonRolElement.getAsString();
//				historicoDeRoles.add(rol);
//			}
//			for (JsonElement jsonRolElement : jsonArtistaObject.get("bandas").getAsJsonArray()) {
//				String nombreBanda = jsonRolElement.getAsString();
//				if (!repositorioBanda.containsKey(nombreBanda))
//					repositorioBanda.put(nombreBanda, new BandaHistorico(nombreBanda));
//				historicoDeBanda.add(repositorioBanda.get(nombreBanda));
//			}
//			if (listaDeArtistasBase.contains(nombreDelArtista))
//				artista = new ArtistaBase(nombreDelArtista, historicoDeRoles, historicoDeBanda);
//			else {
//				double costoXCancion = jsonArtistaObject.get("costo").getAsDouble();
//				int maxCanciones = jsonArtistaObject.get("maxCanciones").getAsInt();
//				artista = new ArtistaContratado(nombreDelArtista, historicoDeRoles, historicoDeBanda, costoXCancion,
//						maxCanciones);
//			}
//			for (BandaHistorico banda : historicoDeBanda)
//				banda.agregarArtista(artista);
//			lineUp.add(artista);
//		}
//		fileReader.close();
//		repositorioBanda.clear();
//		return lineUp;
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
			List<Artista> integrantesDeCancion = cancion.getListadoDeIntegrantes();
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
			Artista artista = lineUp.get(i);
			if (!artista.perteneceADiscografica() && !artista.estaAsignadoAUnaCancion())
				listado.put(artista.getNombre(), i);
		}
//		System.out.println(listado.size());s
		return listado;
	}

	public Map<String, Integer> getListadoArtistasContratados() {
		Map<String, Integer> listado = new LinkedHashMap<>();
		for (int i = 0; i < lineUp.size(); i++) {
			Artista artista = lineUp.get(i);
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
