package importacion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import artista.Artista;
import artista.ArtistaBase;
import artista.ArtistaContratado;
import banda.BandaHistorico;
import cancion.Cancion;

public class Importacion {
	private Importacion() {
	}

	public static List<String> importarNombresDeArtistasDeDiscografica(String rutaArch)
			throws FileNotFoundException, JsonSyntaxException, IOException {// hacerlo private
		FileReader fileReader = new FileReader(new File(rutaArch));
		Gson gson = new Gson();
		List<String> artistas = gson.fromJson(fileReader, new TypeToken<List<String>>() {
		}.getType());
		fileReader.close();
		return artistas;
	}

//	 "nombre": "Brian May",
//	    "roles": ["guitarra eléctrica", "voz secundaria"],
//	    "bandas": ["Queen"],
//	    "costo": 0,
//	    "maxCanciones": 100
	public static List<Artista> importarArtistas(String rutaArch, List<String> listaDeRoles,
			List<String> listaDeArtistasBase) throws FileNotFoundException, JsonSyntaxException, IOException {
		FileReader fileReader = new FileReader(new File(rutaArch));
		List<Artista> lineUp = new ArrayList<>();
		JsonArray json = JsonParser.parseReader(fileReader).getAsJsonArray();
		Map<String, BandaHistorico> repositorioBanda = new HashMap<>();
		Map<String, List<Artista>> bandaXIntegrantes = new HashMap<>();
		Artista artista;
		for (JsonElement jsonArtistaElement : json) {
			JsonObject jsonArtistaObject = jsonArtistaElement.getAsJsonObject();
			for (JsonElement jsonRolElement : jsonArtistaObject.get("bandas").getAsJsonArray()) {
				String banda = jsonRolElement.getAsString();
				if (!repositorioBanda.containsKey(banda)) {
					bandaXIntegrantes.put(banda, new ArrayList<>());
					repositorioBanda.put(banda, new BandaHistorico(banda, bandaXIntegrantes.get(banda)));
				}
			}
		}

		for (JsonElement jsonArtistaElement : json) {
			JsonObject jsonArtistaObject = jsonArtistaElement.getAsJsonObject();
			String nombreDelArtista = jsonArtistaObject.get("nombre").getAsString();
			List<BandaHistorico> historicoDeBanda = new ArrayList<>();
			List<String> historicoDeRoles = new ArrayList<>();
			for (JsonElement jsonRolElement : jsonArtistaObject.get("roles").getAsJsonArray()) {
				String rol = jsonRolElement.getAsString();
				historicoDeRoles.add(rol);
			}
			for (JsonElement jsonRolElement : jsonArtistaObject.get("bandas").getAsJsonArray()) {
				String nombreBanda = jsonRolElement.getAsString();
				historicoDeBanda.add(repositorioBanda.get(nombreBanda));
			}
			if (listaDeArtistasBase.contains(nombreDelArtista))
				artista = new ArtistaBase(nombreDelArtista, historicoDeRoles, historicoDeBanda);
			else {
				double costoXCancion = jsonArtistaObject.get("costo").getAsDouble();
				int maxCanciones = jsonArtistaObject.get("maxCanciones").getAsInt();
				artista = new ArtistaContratado(nombreDelArtista, historicoDeRoles, historicoDeBanda, costoXCancion,
						maxCanciones);
			}
			for (BandaHistorico banda : historicoDeBanda) {
				bandaXIntegrantes.get(banda.getNombre()).add(artista);
			}
			lineUp.add(artista);
		}
		fileReader.close();
		bandaXIntegrantes.clear();
		repositorioBanda.clear();
		return lineUp;
	}

	public static List<Cancion> importarRepertorio(String rutaArch)
			throws FileNotFoundException, JsonSyntaxException, IOException {
		FileReader fileReader = new FileReader(new File(rutaArch));
		List<Cancion> repertorio = new ArrayList<>();
		JsonArray json = JsonParser.parseReader(fileReader).getAsJsonArray();
		for (JsonElement jsonCancionElement : json) {
			JsonObject jsonCancionObject = jsonCancionElement.getAsJsonObject();
			String titulo = jsonCancionObject.get("titulo").getAsString();
			List<String> roles = new ArrayList<>();
			for (JsonElement jsonRolElement : jsonCancionObject.get("rolesRequeridos").getAsJsonArray()) {
				roles.add(jsonRolElement.getAsString());
			}
			repertorio.add(new Cancion(titulo, roles));
		}
		fileReader.close();
		return repertorio;
	}

	public static List<String> importarRoles(String rutaArch)
			throws FileNotFoundException, JsonSyntaxException, IOException {
		FileReader fileReader = new FileReader(new File(rutaArch));
		Gson gson = new Gson();
		List<String> roles = gson.fromJson(fileReader, new TypeToken<List<String>>() {
		}.getType());
		fileReader.close();
		return roles;
	}
}
