package prolog;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.text.Normalizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jpl7.Query;
import org.jpl7.Term;
import org.jpl7.Variable;

import recital.Recital;
import cancion.Cancion;

public class IntegracionProlog {

	private static final String ARTISTAS_JSON_PATH = "assets/artistas.json";
	private static final String DISCOGRAFICA_JSON_PATH = "assets/artistas-discografica.json";
	private static final String OUTPUT_DIR = "target/prolog";
	private static final String PL_FILE_NAME = "base-de-conocimiento-prolog.pl";

	private static final List<String> lineas = new ArrayList<>();
	
	private static Recital recitalActual = null;

	public static void setRecitalActual(Recital recital) {
	    recitalActual = recital;
	}

	public static void generarBaseDeConocimiento() {
	    lineas.clear();
	    
	    if (recitalActual == null) {
	        throw new IllegalStateException("No hay ningún recital cargado. Use la opción 15 primero.");
	    }

	    try {
	        File dir = new File(OUTPUT_DIR);
	        if (!dir.exists()) {
	            dir.mkdirs();
	        }

	        String outputPath = OUTPUT_DIR + File.separator + PL_FILE_NAME;

	        generarHechosDeArtistas();
	        generarHechosDeDiscografica();
	        generarHechosDeRecital(recitalActual);
	        agregarReglasEstaticas();

	        try (PrintWriter pw = new PrintWriter(new FileWriter(outputPath))) {
	            for (String l : lineas) {
	                pw.println(l);
	            }
	        }

	    } catch (IOException e) {
	        throw new RuntimeException("Error de I/O al generar la base Prolog.", e);
	    }
	}


	public static void generarHechosDeArtistas() throws IOException {
		List<String> bloque = new ArrayList<>();
		InputStream inputStream = IntegracionProlog.class.getClassLoader().getResourceAsStream(ARTISTAS_JSON_PATH);
		if (inputStream == null)
			throw new IOException("No se encontró " + ARTISTAS_JSON_PATH);
		try (InputStreamReader reader = new InputStreamReader(inputStream)) {
			JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
			for (JsonElement jsonElement : jsonArray) {
				JsonObject jsonArtista = jsonElement.getAsJsonObject();
				String nombre = jsonArtista.get("nombre").getAsString();
				String atom = toPrologAtom(nombre);
				double costo = 0;
				if (jsonArtista.has("costo") && !jsonArtista.get("costo").isJsonNull()) {
					try {
						costo = jsonArtista.get("costo").getAsDouble();
					} catch (Exception ignored) {
					}
				}
				String tipo = (costo == 0) ? "base" : "contratado";
				bloque.add(String.format("artista(%s, %s).", atom, tipo));
				if (jsonArtista.has("roles") && jsonArtista.get("roles").isJsonArray()) {
					for (JsonElement r : jsonArtista.getAsJsonArray("roles")) {
						bloque.add(String.format("habilidad(%s, %s).", atom, toPrologAtom(r.getAsString())));
					}
				}
				if (jsonArtista.has("historial") && jsonArtista.get("historial").isJsonArray()) {
					for (JsonElement h : jsonArtista.getAsJsonArray("historial")) {
						if (!h.isJsonNull()) {
							bloque.add(String.format("historial(%s, %s).", atom, toPrologAtom(h.getAsString())));
						}
					}
				}
				bloque.add(String.format(Locale.US, "costo_base(%s, %s).", atom, doubleToPrologNumber(costo)));
				int max = -1;
				if (jsonArtista.has("maxCanciones") && !jsonArtista.get("maxCanciones").isJsonNull()) {
					try {
						max = jsonArtista.get("maxCanciones").getAsInt();
					} catch (Exception ignored) {
					}
				}
				bloque.add(String.format("max_canciones(%s, %d).", atom, max));
				boolean tieneRoles = jsonArtista.has("roles") && jsonArtista.get("roles").getAsJsonArray().size() > 0;
				if ("contratado".equals(tipo) && !tieneRoles) {
					bloque.add(String.format("contratado_sin_experiencia(%s).", atom));
				}
			}
		}
		Collections.sort(bloque);
		lineas.addAll(bloque);
	}

	public static void generarHechosDeDiscografica() throws IOException {
		List<String> bloque = new ArrayList<>();
		InputStream inputStream = IntegracionProlog.class.getClassLoader().getResourceAsStream(DISCOGRAFICA_JSON_PATH);
		if (inputStream == null)
			throw new IOException("No se encontró " + DISCOGRAFICA_JSON_PATH);
		try (InputStreamReader reader = new InputStreamReader(inputStream)) {
			Gson gson = new Gson();
			List<String> artistas = gson.fromJson(reader, new TypeToken<List<String>>() {
			}.getType());
			for (String nombre : artistas) {
				bloque.add(String.format("miembro_discografica(%s).", toPrologAtom(nombre)));
			}
		}
		Collections.sort(bloque);
		lineas.addAll(bloque);
	}

	public static void generarHechosDeRecital(Recital recital) {
	    List<String> bloque = new ArrayList<>();
	    int id = 1;

	    for (Cancion c : recital.getRepertorio()) {
	        if (c.getRolesRequeridos() != null) {
	            for (String rol : c.getRolesRequeridos()) {
	                bloque.add(String.format("rol_instancia(i%d, %s).", id++, toPrologAtom(rol)));
	            }
	        }
	    }

	    Collections.sort(bloque);
	    lineas.addAll(bloque);

	    lineas.add(String.format("total_instancias_rol(%d).", id - 1));
	}


	private static void agregarReglasEstaticas() {
		lineas.add("coste_entrenamiento(A, R, 0) :- habilidad(A, R).");
		lineas.add("coste_entrenamiento(A, R, 1) :- artista(A, _), \\+ habilidad(A, R).");
		lineas.add("requeridas(Rol, Cant) :- findall(1, rol_instancia(_, Rol), L), length(L, Cant).");
		lineas.add("base_saben(Rol, Lista) :- findall(A, (habilidad(A, Rol), artista(A, base)), Lista).");
		lineas.add("capacidad_total(Rol, Capacidad) :- " + "base_saben(Rol, Lista), "
				+ "findall(Max, (member(A, Lista), max_canciones(A, Max)), Maximos), "
				+ "sumlist(Maximos, Capacidad).");
		lineas.add("entrenamientos_necesarios(Rol, Ent) :- " + "requeridas(Rol, Req), "
				+ "capacidad_total(Rol, CapacidadBase), " + "Temp is Req - CapacidadBase, "
				+ "(Temp > 0 -> Ent = Temp ; Ent = 0).");
		lineas.add("entrenamientos_minimos(Total) :- " + "setof(R, I^rol_instancia(I, R), Roles), "
				+ "findall(E, (member(R, Roles), entrenamientos_necesarios(R, E)), Lista), "
				+ "sumlist(Lista, Total).");
	}

	private static String toPrologAtom(String s) {
		String n = Normalizer.normalize(s, Normalizer.Form.NFD);
		n = n.replaceAll("\\p{M}", "");
		n = n.toLowerCase();
		return n.replaceAll("[^a-z0-9_]", "_");
	}

	private static String doubleToPrologNumber(double d) {
		if (d == (long) d)
			return String.format(Locale.US, "%d", (long) d);
		return String.format(Locale.US, "%s", d);
	}

	public static int consultarEntrenamientosMinimos() {
		String path = OUTPUT_DIR + File.separator + PL_FILE_NAME;
		Query q1 = new Query("consult('" + path.replace("\\", "/") + "')");
		if (!q1.hasSolution())
			throw new RuntimeException("No se pudo cargar el archivo Prolog en: " + path);
		Variable total = new Variable("Total");
		Query q2 = new Query("entrenamientos_minimos", new Term[] { total });
		Map<String, Term> res = q2.oneSolution();
		if (res == null)
			throw new RuntimeException("No se encontró solución al consultar Prolog.");
		return res.get("Total").intValue();
	}
}