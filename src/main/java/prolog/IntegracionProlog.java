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

public class IntegracionProlog {

	private static final String ARTISTAS_JSON_PATH = "assets/artistas.json";
	private static final String RECITAL_JSON_PATH = "assets/recital.json";
	private static final String DISCOGRAFICA_JSON_PATH = "assets/artistas-discografica.json";

	private static final String OUTPUT_DIR = "target/prolog";
	private static final String PL_FILE_NAME = "base-de-conocimiento-prolog.pl";

	private static final List<String> lineas = new ArrayList<>();

	public static void generarBaseDeConocimiento() {

		lineas.clear();

		try {
			// Crear carpeta target/prolog si no existe
			File dir = new File(OUTPUT_DIR);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			String outputPath = OUTPUT_DIR + File.separator + PL_FILE_NAME;

			lineas.add("% --- GENERADO AUTOMATICAMENTE DESDE JAVA ---");

			generarHechosDeArtistas();
			generarHechosDeDiscografica();
			generarHechosDeRecital();
			agregarReglasEstaticas();

			try (PrintWriter pw = new PrintWriter(new FileWriter(outputPath))) {
				for (String l : lineas) {
					pw.println(l);
				}
			}

			// System.out.println("Base de conocimiento generada en: " + canonicalPath);

		} catch (IOException e) {
			throw new RuntimeException("Error de I/O al generar la base Prolog.", e);
		}
	}

	public static void generarBaseDeConocimientoEn(String path) {
		try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {

			pw.println("% --- GENERADO AUTOMATICAMENTE DESDE JAVA ---");
			pw.println("");

			pw.println("artista(agustin_cruz, contratado).");
			pw.println("rol_instancia(i1, voz_principal).");
			pw.println("costo_base(agustin_cruz, 500).");
			pw.println("");

			pw.println("% --- REGLAS ESTÁTICAS DE COSTE ---");
			pw.println("coste_entrenamiento(A, R, 0) :- habilidad(A, R).");
			pw.println("coste_entrenamiento(A, R, 1) :- artista(A, _), \\+ habilidad(A, R).");

			pw.flush();

		} catch (IOException e) {
			throw new RuntimeException("No se pudo generar el archivo PL: " + path, e);
		}

		System.out.println("Base generada en: " + path);
	}

	public static void generarHechosDeArtistas() throws IOException {

		lineas.add("\n% --- HECHOS DE ARTISTAS Y HABILIDADES ---");

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

				// habilidades
				if (jsonArtista.has("roles") && jsonArtista.get("roles").isJsonArray()) {
					for (JsonElement r : jsonArtista.getAsJsonArray("roles")) {
						bloque.add(String.format("habilidad(%s, %s).", atom, toPrologAtom(r.getAsString())));
					}
				}

				// historial
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

				// contratado sin experiencia
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

		lineas.add("\n% --- MIEMBROS DE DISCOGRAFICA (ARTISTAS BASE) ---");

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

	public static void generarHechosDeRecital() throws IOException {

		lineas.add("\n% --- HECHOS DE ROLES REQUERIDOS (POR CADA CANCION) ---");

		List<String> bloque = new ArrayList<>();

		InputStream inputStream = IntegracionProlog.class.getClassLoader().getResourceAsStream(RECITAL_JSON_PATH);

		if (inputStream == null)
			throw new IOException("No se encontró " + RECITAL_JSON_PATH);

		try (InputStreamReader reader = new InputStreamReader(inputStream)) {

			JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();

			int numeroCancion = 1;

			for (JsonElement jsonElement : jsonArray) {

				JsonObject cancion = jsonElement.getAsJsonObject();
				String idCancion = "c" + numeroCancion;

				if (cancion.has("rolesRequeridos") && cancion.get("rolesRequeridos").isJsonArray()) {
					for (JsonElement r : cancion.getAsJsonArray("rolesRequeridos")) {
						bloque.add(String.format("rol_instancia(%s, %s).", idCancion, toPrologAtom(r.getAsString())));
					}
				}

				numeroCancion++;
			}
		}

		Collections.sort(bloque);
		lineas.addAll(bloque);
	}

	private static void agregarReglasEstaticas() {

		lineas.add("\n% --- REGLAS ESTÁTICAS DE COSTE ---");
		lineas.add("coste_entrenamiento(A, R, 0) :- habilidad(A, R).");
		lineas.add("coste_entrenamiento(A, R, 1) :- artista(A, _), \\+ habilidad(A, R).");

		lineas.add("\n% --- CALCULO DE ENTRENAMIENTOS MINIMOS (MAX SIMULTANEO) ---");

		// Artistas base que saben el rol
		lineas.add("saben_rol(Rol, Lista) :- findall(A, habilidad(A, Rol), Lista).");

		// Capacidad de artistas base (1 instancia por artista base)
		lineas.add("capacidad_total(Rol, Cap) :- saben_rol(Rol, L), length(L, Cap).");

		// Cantidad de ese rol por canción (simultáneo)
		lineas.add("cantidad_por_cancion(Cancion, Rol, Cant) :- "
				+ "findall(1, rol_instancia(Cancion, Rol), L), length(L, Cant).");

		// Listado de todas las canciones
		lineas.add("canciones(Lista) :- setof(C, R^rol_instancia(C, R), Lista).");

		// Máximo simultáneo del rol entre canciones
		lineas.add("maximo_simultaneo(Rol, Max) :- " + "canciones(Cs), "
				+ "findall(Cant, (member(C, Cs), cantidad_por_cancion(C, Rol, Cant)), L), " + "max_list(L, Max).");

		// Entrenamientos necesarios = max simultáneo – base existentes
		lineas.add("entrenamientos_necesarios(Rol, Ent) :- " + "maximo_simultaneo(Rol, Max), "
				+ "capacidad_total(Rol, Cap), " + "Temp is Max - Cap, " + "(Temp > 0 -> Ent = Temp ; Ent = 0).");

		// Suma total
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
