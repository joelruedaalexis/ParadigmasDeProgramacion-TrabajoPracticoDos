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
import java.util.Set;
import java.util.TreeSet;
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
	public String cantDeRolesFaltantesParaUnaCancion(int index) {
//		Chequear si el index es válido y blablabla
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
	public String cantDeRolesFaltantesParaTodasLasCanciones() {
		List<ArtistaBase> listaArtistasBase = lineUp.stream().filter(a -> a.perteneceADiscografica()).toList();
		Map<String, Integer> rolesFaltantesTotalesXCupo = new HashMap<>();
		Set<ArtistaBase> setArtistasYaAsignados = new TreeSet<ArtistaBase>(new ComparadorArtistaPorNombre());
		for (Cancion cancion : repertorio) {
			System.out.println(cancion.getTitulo());
			Map<String, Integer> rolesFaltantesXCupoDeCancion = cancion.getRolesFaltantesXCupos();
			for (Map.Entry<String, Integer> nodo : rolesFaltantesXCupoDeCancion.entrySet()) {
				String rol = nodo.getKey();
				Integer cupos = nodo.getValue();
				for (int i = 0; i < listaArtistasBase.size() && cupos > 0; i++) {
					ArtistaBase artistaBase = listaArtistasBase.get(i);
					if (!cancion.artistaEstaAsignado(artistaBase) && !setArtistasYaAsignados.contains(artistaBase)
							&& artistaBase.tieneRol(rol)) {
						setArtistasYaAsignados.add(artistaBase);
						cupos--;
					}
				}
				if (cupos > 0)
					rolesFaltantesTotalesXCupo.put(rol, rolesFaltantesTotalesXCupo.getOrDefault(rol, 0) + cupos);
			}
			rolesFaltantesXCupoDeCancion.clear();
			setArtistasYaAsignados.clear();
		}

		if (rolesFaltantesTotalesXCupo.isEmpty())
			return "Todas las canciones ya tienen sus roles asignados a artistas.";

		String str = "Para poder asignar todas las canciones con artistas contratados se necesitan que tengan los siguientes roles:\n";
		for (Map.Entry<String, Integer> nodo : rolesFaltantesTotalesXCupo.entrySet()) {
			String rol = nodo.getKey();
			Integer cupos = nodo.getValue();
			str += String.format("\t~%d %s.\n", cupos, rol);
		}

		return str;
	}

//	public void cargarMapTodosLosRolesXArtista() {
//		for (ArtistaBase artista : lineUp) {
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
//		for (List<ArtistaBase> listaArtistas : todosLosRolesXArtista.values())
//			listaArtistas.sort(new ComparadorArtistaPorCostoDeCancion());
//	}

//	contratarArtistasParaUnaCancion = 3, 
	public TransaccionAsignacionDeCancion contratarArtistasParaUnaCancion(int index) {
		if (index < 0 || index >= repertorio.size())
			throw new ArrayIndexOutOfBoundsException(
					"El index ingresado es inválido porque está fuera de los limites.");
		Cancion cancion = repertorio.get(index);

		TransaccionAsignacionDeCancion resultadoTransaccion = new TransaccionAsignacionDeCancion(cancion);

		Map<String, IntegranteDeRol> rolesXIntegrantesCandidatos = cancion.getRolesFaltantesConCuposDeIntegrantes();

//		Map<String, Integer> rolesXCantidadNecesaria = cancion.getRolesConCuposDeIntegrantes();
//		Map<String, Integer> rolesXCantidadNecesaria = new HashMap<>();
//		Map<String, Integer> rolesXCantidadFaltante = new HashMap<>();
//		Map<String, List<Artista>> rolesXArtistasCandidatos = new HashMap<>();

		List<ArtistaBase> listaDeArtistasDisponibles = lineUp.stream()
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
				ArtistaBase artista = listaDeArtistasDisponibles.get(i);
				if (artista.tieneRol(rol)) {
					integrantesDeRol.agregarIntegrante(artista);
					listaDeArtistasDisponibles.remove(i);
				} else
					i++;
			}
			if (!hayIntegrantesInsuficientes && integrantesDeRol.hayCuposDisponibles())
				hayIntegrantesInsuficientes = true;// No hay artistas SUFICIENTES que tengan ese rol!!!
		}

		if (hayIntegrantesInsuficientes) {// hay roles q no estan cubiertos !!!!!
			resultadoTransaccion.cancelarTransaccion(rolesXIntegrantesCandidatos, listaDeArtistasDisponibles);
			return resultadoTransaccion;
		}

		for (Map.Entry<String, IntegranteDeRol> nodo : rolesXIntegrantesCandidatos.entrySet()) {
			String rol = nodo.getKey();
			List<ArtistaBase> listaDeArtistas = nodo.getValue().getListaDeIntegrantes();
			listaDeArtistas.forEach(artista -> {
				System.out.println(cancion.agregarArtista(rol, artista));
				;
				artista.asignar(cancion);
			});
		}
		resultadoTransaccion.confirmarTransaccion();
		return resultadoTransaccion;
	}

//	contratarArtistasParaTodasLasCanciones = 4
	public void contratarArtistasParaTodasLasCanciones() {// plantearlo
		Map<String, IntegranteDeRol> rolesXIntegrantesCandidatos = new HashMap<>();
		for (Cancion cancion : repertorio) {
			Map<String, IntegranteDeRol> rolesXIntegrantes = cancion.getRolesFaltantesConCuposDeIntegrantes();
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
		ArtistaBase artista = cancion.getListadoDeIntegrantes().get(indexArtista);
		cancion.quitarArtista(artista);
	}

//	quitarArtistaDeTodasLasCanciones = 10
	public void quitarArtistaDeTodasLasCanciones(String nombreDeArtista) {
		ArtistaBase artista;
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
		ArtistaBase artista = lineUp.get(indexLineUp);
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
			Map<String, IntegranteDeRol> rolesXIntegrantes = new HashMap<>();
			for (JsonElement jsonRolElement : jsonCancionObject.get("rolesXArtista").getAsJsonArray()) {
				JsonObject rolesXIntegrantesJSON = jsonRolElement.getAsJsonObject();
				String rol = rolesXIntegrantesJSON.get("rol").getAsString();
				JsonArray integrantesJSON = rolesXIntegrantesJSON.get("integrantes").getAsJsonArray();
				int cupos = integrantesJSON.size();
				IntegranteDeRol integrantesDeRol = new IntegranteDeRol(cupos);
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
			if (!artista.perteneceADiscografica() && !artista.estaAsignadoAUnaCancion())
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
