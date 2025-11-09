package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.JsonSyntaxException;

import artista.Artista;
import cancion.Cancion;
import importacion.Importacion;
import menu.Menu;
import recital.Recital;

public class Main {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		System.out.println("XD");
//		Scanner scanner = new Scanner(System.in);
//		List<Artista> lineUp = Importacion.importarArtistas(null);
//		List<Cancion> cancionero = Importacion.importarCancionero(null);
//		List<String> roles = Importacion.importarRoles(null);
//		Recital recital = new Recital(cancionero,lineUp,roles);
//		Menu menu = new Menu(scanner, recital);
//		menu.iniciar();
//		scanner.close();

//		String recipesPath = Paths.get("src", "assets", "recipes.json").toString();

		String rutaArchivo1 = Paths.get("src", "assets", "artistas-discografica.json").toString();
		List<String> artistasDeDiscografica = null;
		try {
			artistasDeDiscografica = Importacion.importarNombresDeArtistasDeDiscografica(rutaArchivo1);
//			System.out.println(artistasDeDiscografia);
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String rutaArchivo2 = Paths.get("src", "assets", "recital.json").toString();
		List<Cancion> repertorio = null;
		try {
			repertorio = Importacion.importarRepertorio(rutaArchivo2);
//			System.out.println(cancionero.stream().map(Cancion::getTitulo).toList());
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<String> roles = null;
		String rutaArchivo3 = Paths.get("src", "assets", "roles-necesarios.json").toString();
		try {
			roles = Importacion.importarRoles(rutaArchivo3);
//			System.out.println(roles);
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String rutaArchivo4 = Paths.get("src", "assets", "artistas.json").toString();
		List<Artista> artistas = null;
		try {
			artistas = Importacion.importarArtistas(rutaArchivo4, roles, artistasDeDiscografica);
//			System.out.println(artistas.stream().map(Artista::getListaDeBandas).toList());
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Scanner scanner = new Scanner(System.in);
//		List<Artista> lineUp = Importacion.importarArtistas(null);
//		List<Cancion> cancionero = Importacion.importarCancionero(null);
//		List<String> roles = Importacion.importarRoles(null);
		Recital recital = new Recital(repertorio, artistas, roles);
		Menu menu = new Menu(scanner, recital);
		menu.iniciar();
		scanner.close();
		
		List<String> lista = new ArrayList<>(4);
		System.out.println("---->" + lista.size());
		
//		System.out.println(repertorio);
		Map<String, Integer> map = new LinkedHashMap<>();
		map.put("D", 4);
		map.put("B", 2);
		map.put("A", 1);
		map.put("C", 3);
		System.out.println(map);

		List<Integer> xd = new ArrayList<>();
		xd.add(null);
		xd.add(null);
		xd.add(2);
		System.out.println(xd);
	}
}
