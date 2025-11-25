package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.google.gson.JsonSyntaxException;

import artista.ArtistaBase;
import cancion.Cancion;
import importacion.Importacion;
import menu.Menu;
import recital.Recital;

public class Main {
	public static void main(String[] args) {
		String rutaArchivo1 = Paths.get("src", "main", "resources", "assets", "artistas-discografica.json").toString();
		List<String> artistasDeDiscografica = null;
		try {
			artistasDeDiscografica = Importacion.importarNombresDeArtistasDeDiscografica(rutaArchivo1);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String rutaArchivo2 = Paths.get("src", "main", "resources", "assets", "recital.json").toString();
		List<Cancion> repertorio = null;
		try {
			repertorio = Importacion.importarRepertorio(rutaArchivo2);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Set<String> roles = null;
		String rutaArchivo3 = Paths.get("src", "main", "resources", "assets", "roles-necesarios.json").toString();
		try {
			roles = Importacion.importarRoles(rutaArchivo3);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String rutaArchivo4 = Paths.get("src", "main", "resources", "assets", "artistas.json").toString();
		List<ArtistaBase> artistas = null;
		try {
			artistas = Importacion.importarArtistas(rutaArchivo4, roles, artistasDeDiscografica);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Scanner scanner = new Scanner(System.in);
		Recital recital = new Recital(repertorio, artistas, roles);
		Menu menu;
		try {
			menu = new Menu(scanner, recital);
			menu.iniciar();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} finally {
			scanner.close();
		}
	}
}
