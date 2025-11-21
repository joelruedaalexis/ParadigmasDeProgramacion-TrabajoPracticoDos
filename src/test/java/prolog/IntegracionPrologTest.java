package prolog;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.jpl7.JPL;
import org.jpl7.Query;
import org.junit.jupiter.api.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class IntegracionPrologTest {

    private static final String OUTPUT_DIR = "target/prolog";
    private static final String PL_FILE = OUTPUT_DIR + "/base-de-conocimiento-prolog.pl";

    // ============================
    // Inicialización de JPL
    // ============================
    @BeforeAll
    void initJplAndLoadBase() throws Exception {

    	System.setProperty("jpl.swipl.home", "C:\\Program Files\\swipl");
        System.setProperty("java.library.path", "C:\\Program Files\\swipl\\bin");

        JPL.setTraditional();

        new File(OUTPUT_DIR).mkdirs();

        IntegracionProlog.generarBaseDeConocimiento();

        String path = PL_FILE.replace("\\", "/");
        Query q = new Query("catch(consult('" + path + "'), _, fail)");
        if (!q.hasSolution()) {
            throw new RuntimeException("No se pudo cargar la base Prolog en @BeforeAll: " + path);
        }
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


