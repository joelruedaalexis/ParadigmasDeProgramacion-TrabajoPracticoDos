package prolog;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IntegracionProlog {
	
	private static final String ARTISTAS_JSON_PATH = "assets/artistas.json";
    private static final String PL_FILE_NAME = "base-de-conocimiento-prolog.pl";
    private static final String ASSETS_FOLDER = "assets"; // Removido el '/'
    private static final String SRC_FOLDER = "src"; // Nombre de la carpeta fuente

	private static String toPrologAtom(String s) {
        return s.replace(' ', '_').toLowerCase();
    }
    
    public static void generarHechosDeArtistas() {
        
        try {
            // 1. OBTENER LA RUTA RELATIVA AL PROYECTO PARA ESCRITURA (./src/assets/)
            // Esto construye una ruta relativa al directorio de trabajo actual (normalmente la raíz del proyecto).
            String relativeFilePath = "." + File.separator + SRC_FOLDER + 
                                      File.separator + ASSETS_FOLDER + File.separator + PL_FILE_NAME;
            
            // Usamos new File(relativeFilePath).getCanonicalPath() para obtener la ruta absoluta
            // sólo para imprimirla y verificar que la ruta sea válida, pero la escritura se hace
            // con la ruta relativa que el sistema interpreta.
            String canonicalPath = new File(relativeFilePath).getCanonicalPath();

            // 2. ABRIR EL ESCRITOR y gestionar el flujo de I/O
            try (FileWriter writer = new FileWriter(relativeFilePath)) {
                
                writer.write("% --- HECHOS DE ARTISTAS Y SUS HABILIDADES ---\n");

                // Leer artistas.json desde el classpath (bin/assets/)
                InputStream inputStream = IntegracionProlog.class.getClassLoader().getResourceAsStream(ARTISTAS_JSON_PATH);
                
                if (inputStream == null) {
                    throw new IOException("ERROR: No se encontró el recurso JSON en el classpath: " + ARTISTAS_JSON_PATH);
                }
                
                // Procesamiento de JSON
                try (java.io.Reader reader = new InputStreamReader(inputStream)) {
                    
                    JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();

                    for (JsonElement jsonElement : jsonArray) {
                        JsonObject jsonArtistaObject = jsonElement.getAsJsonObject();
                        
                        String nombreDelArtista = jsonArtistaObject.get("nombre").getAsString();
                        String nombreProlog = toPrologAtom(nombreDelArtista);
                        
                        double costo = jsonArtistaObject.get("costo").getAsDouble();
                        String tipo = (costo == 0.0) ? "base" : "contratado";

                        // Hecho artista/2
                        writer.write(String.format("artista(%s, %s).\n", nombreProlog, tipo));

                        // Hecho habilidad/2
                        JsonArray rolesArray = jsonArtistaObject.get("roles").getAsJsonArray();
                        for (JsonElement jsonRolElement : rolesArray) {
                            String rol = jsonRolElement.getAsString();
                            String rolProlog = toPrologAtom(rol);
                            
                            writer.write(String.format("habilidad(%s, %s).\n", nombreProlog, rolProlog));
                        }
                    }
                }
                
                writer.write("\n% --- REGLAS ESTÁTICAS DE COSTE ---\n");
                agregarReglasEstaticas(writer);

                System.out.println("✅ Hechos de artistas generados y escritos en: " + canonicalPath);

            } // FileWriter se cierra aquí
            
        } catch (IOException e) {
            // CONVERSIÓN: Capturamos la IOException (chequeada) y la relanzamos 
            // como una RuntimeException (no chequeada).
            throw new RuntimeException("Error fatal de I/O al generar la base de conocimiento Prolog.", e);
        }
    }
    
    private static void agregarReglasEstaticas(FileWriter writer) throws IOException {
        // Reglas de costo (0 si sabe, 1 si necesita entrenamiento)
        writer.write("coste_entrenamiento(A, R, 0) :- habilidad(A, R).\n");
        writer.write("coste_entrenamiento(A, R, 1) :- artista(A, _), \\+ habilidad(A, R).\n");
    }
    
    /** Orquestador que puede ser llamado desde main.Main para generar la BC.
     * Si falla, la RuntimeException detendrá la aplicación.
     */
    public static void generarBaseDeConocimiento() {
        // Por ahora, solo llama a la generación de artistas
        // Si añades más generadores, los llamas aquí:
        generarHechosDeArtistas(); 
        
        // Aquí se llamaría a generarHechosDeRecital(), etc.
    }
}
