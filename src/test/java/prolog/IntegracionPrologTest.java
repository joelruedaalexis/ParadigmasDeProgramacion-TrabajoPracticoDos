package prolog;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.jpl7.JPL;
import org.jpl7.Query;
import org.junit.jupiter.api.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class IntegracionPrologTest {

    private static final String OUTPUT_DIR = "target/prolog";
    private static final String PL_FILE = OUTPUT_DIR + "/base-de-conocimiento-prolog.pl";

    @BeforeAll
    void initJplAndLoadBase() throws Exception {
    	
        System.setProperty("jpl.swipl.home", "C:\\Program Files\\swipl");
        System.setProperty("java.library.path", "C:\\Program Files\\swipl\\bin");
        JPL.setTraditional();

        new File(OUTPUT_DIR).mkdirs();

        var fLineas = IntegracionProlog.class.getDeclaredField("lineas");
        fLineas.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<String> lineas = (List<String>) fLineas.get(null);
        lineas.clear();

        IntegracionProlog.generarHechosDeArtistas();
        IntegracionProlog.generarHechosDeDiscografica();

        // Añado al menos una instancia de rol y reglas básicas para que el .pl se pueda leer
        lineas.add("");
        lineas.add("% --- HECHOS DE PRUEBA DE RECITAL ---");
        lineas.add("rol_instancia(i1, voz_principal).");
        lineas.add("total_instancias_rol(1).");
        lineas.add("");
        lineas.add("% --- REGLAS ESTATICAS (PRUEBA) ---");
        lineas.add("coste_entrenamiento(A, R, 0) :- habilidad(A, R).");
        lineas.add("coste_entrenamiento(A, R, 1) :- artista(A, _), \\+ habilidad(A, R).");
        lineas.add("requeridas(Rol, Cant) :- findall(1, rol_instancia(_, Rol), L), length(L, Cant).");
        lineas.add("base_saben(Rol, Lista) :- findall(A, (habilidad(A, Rol), artista(A, base)), Lista).");
        lineas.add("capacidad_total(Rol, Capacidad) :- base_saben(Rol, Lista), findall(Max, (member(A, Lista), max_canciones(A, Max)), Maximos), sumlist(Maximos, Capacidad).");
        lineas.add("entrenamientos_necesarios(Rol, Ent) :- requeridas(Rol, Req), capacidad_total(Rol, CapacidadBase), Temp is Req - CapacidadBase, (Temp > 0 -> Ent = Temp ; Ent = 0).");
        lineas.add("entrenamientos_minimos(Total) :- setof(R, I^rol_instancia(I, R), Roles), findall(E, (member(R, Roles), entrenamientos_necesarios(R, E)), Lista), sumlist(Lista, Total).");

        // Escribir el archivo PL en OUTPUT_DIR
        String outputPath = PL_FILE.replace("\\", "/");
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(outputPath))) {
            for (String l : lineas) pw.println(l);
        }

        // Intentar cargarlo con JPL para verificar que no tiene errores de sintaxis
        Query q = new Query("catch(consult('" + outputPath + "'), _, fail)");
        if (!q.hasSolution()) {
            throw new RuntimeException("No se pudo cargar la base Prolog en @BeforeAll: " + outputPath);
        }
    }


    
    @Test
    void testGenerarHechosDeArtistas() throws Exception {
        // Ejecutar el método a probar
        IntegracionProlog.generarHechosDeArtistas();

        // Obtener la lista generada
        Field field = IntegracionProlog.class.getDeclaredField("lineas");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
		List<String> hechos = (List<String>) field.get(null);

        // Assertions básicas
        assertFalse(hechos.isEmpty(), "No debería estar vacío");
        
        // Verificar algunos hechos clave generados por el JSON de prueba
        assertTrue(hechos.contains("artista(juan_perez, base)."));
        assertTrue(hechos.contains("habilidad(juan_perez, guitarra)."));
        assertTrue(hechos.contains("habilidad(juan_perez, voz_principal)."));
        assertTrue(hechos.contains("historial(juan_perez, river_plate)."));
        assertTrue(hechos.contains("costo_base(juan_perez, 0)."));
        assertTrue(hechos.contains("max_canciones(juan_perez, 5)."));

        // Contratado con costo y sin roles → debe tener contratado_sin_experiencia
        assertTrue(hechos.contains("artista(maria_lopez, contratado)."));
        assertTrue(hechos.contains("costo_base(maria_lopez, 1500)."));
        assertTrue(hechos.contains("contratado_sin_experiencia(maria_lopez)."));

        // Contratado CON roles → NO debe estar la marca contratado_sin_experiencia
        assertTrue(hechos.contains("artista(carlos_gomez, contratado)."));
        assertTrue(hechos.contains("habilidad(carlos_gomez, piano)."));
        assertFalse(hechos.contains("contratado_sin_experiencia(carlos_gomez)."));
    }

    private boolean consultarArchivoSilencioso(String filePath) {
        String p = filePath.replace("\\", "/");
        Query q = new Query("catch(consult('" + p + "'), _, fail)");
        return q.hasSolution();
    }

    private void restoreFromBackup(Path backup, Path target) {
        try {
            if (backup != null && Files.exists(backup)) {
                Files.copy(backup, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("01 - Genera archivo PL")
    public void testGeneraArchivoPL() {
        File f = new File(PL_FILE);
        assertTrue(f.exists(), "El archivo .pl debe existir");
        assertTrue(f.length() > 10, "El archivo .pl no debe estar vacío");
    }

    @Test
    @DisplayName("02 - Archivo PL contiene hechos")
    public void testArchivoPLContieneHechos() throws Exception {
        String contenido = Files.readString(new File(PL_FILE).toPath());
        assertTrue(contenido.contains("artista("), "Debe contener hechos de artistas");
        assertTrue(contenido.contains("rol_instancia("), "Debe contener roles de recital");
        assertTrue(contenido.contains("coste_entrenamiento("), "Debe contener reglas de coste");
    }

    @Test
    @DisplayName("03 - Consultar entrenamientos no lanza excepción")
    public void testConsultaEntrenamientosNoLanzaExcepcion() {
        assertDoesNotThrow(() -> IntegracionProlog.consultarEntrenamientosMinimos(),
                "La consulta a entrenamientos_minimos no debe lanzar excepción");
    }

    @Test
    @DisplayName("04 - Consultar entrenamientos retorna entero")
    public void testConsultaEntrenamientosRetornaEntero() {
        int resultado = IntegracionProlog.consultarEntrenamientosMinimos();
        assertTrue(resultado >= 0, "El resultado debe ser un entero no negativo");
    }

    @Test
    @DisplayName("05 - Falla si archivo .pl no existe (temporarily remove)")
    public void testFallaSiElArchivoPrologNoExiste() throws Exception {
        Path pl = new File(PL_FILE).toPath();
        Path backup = null;
        try {
            if (Files.exists(pl)) {
                backup = Files.createTempFile("pl-backup-", ".pl");
                Files.copy(pl, backup, StandardCopyOption.REPLACE_EXISTING);
                Files.delete(pl);
            }

            assertThrows(RuntimeException.class,
                    () -> IntegracionProlog.consultarEntrenamientosMinimos(),
                    "Debe lanzar RuntimeException si el .pl no existe");
        } finally {
            restoreFromBackup(backup, pl);
            if (backup != null) Files.deleteIfExists(backup);
        }
    }

    @Test
    @DisplayName("07 - Carga manual del archivo PL (silencioso)")
    public void testCargaManualDelArchivoPL() {
        assertTrue(consultarArchivoSilencioso(PL_FILE),
                "Prolog debe poder cargar el archivo .pl sin lanzar errores");
    }

    @Test
    @DisplayName("08 - Archivo PL no contiene texto inválido")
    public void testArchivoPLNoSeCorrompe() throws Exception {
        String contenido = Files.readString(new File(PL_FILE).toPath());
        assertFalse(contenido.contains("null"));
        assertFalse(contenido.contains("??"));
    }

    @Test
    @DisplayName("09 - Reglas estáticas bien formadas")
    public void testReglasEstaticasBienFormadas() throws Exception {
        String contenido = Files.readString(new File(PL_FILE).toPath());
        assertTrue(contenido.contains("coste_entrenamiento("));
        assertTrue(contenido.contains(":-"));
    }

    @Test
    @DisplayName("11 - Consulta con timeout corta (sanity)")
    public void testConsultaConTimeout() {
        assertTimeoutPreemptively(java.time.Duration.ofSeconds(2), () -> {
            Query q = new Query("true");
            assertTrue(q.hasSolution());
        });
    }
}


