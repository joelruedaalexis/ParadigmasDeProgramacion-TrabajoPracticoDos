package prolog;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;

import org.jpl7.Query;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IntegracionPrologTest {

    private static final String OUTPUT_DIR = "target/prolog";
    private static final String PL_FILE = OUTPUT_DIR + "/base-de-conocimiento-prolog.pl";

    // ============================
    // Configuración SWI-PROLOG
    // ============================
    @BeforeAll
    void beforeAll() {
        System.setProperty("jpl.swipl.home", "C:\\Program Files\\swipl");
        System.setProperty("java.library.path", "C:\\Program Files\\swipl\\bin");
    }

    @BeforeEach
    public void setup() {
        File f = new File(PL_FILE);
        if (f.exists()) f.delete();
    }

    @Test
    public void testGeneraArchivoPL() {
        IntegracionProlog.generarBaseDeConocimiento();

        File f = new File(PL_FILE);

        assertTrue(f.exists(), "El archivo .pl no fue creado");
        assertTrue(f.length() > 10, "El archivo .pl está vacío o casi vacío");
    }

    @Test
    public void testArchivoPLContieneHechos() throws Exception {
        IntegracionProlog.generarBaseDeConocimiento();

        String contenido = Files.readString(new File(PL_FILE).toPath());

        assertTrue(contenido.contains("artista("),
                "El archivo debe contener hechos de artistas");

        assertTrue(contenido.contains("rol_instancia("),
                "El archivo debe contener roles de recital");

        assertTrue(contenido.contains("coste_entrenamiento("),
                "El archivo debe contener reglas estáticas");
    }

    @Test
    public void testConsultaEntrenamientosNoLanzaExcepcion() {
        IntegracionProlog.generarBaseDeConocimiento();

        assertDoesNotThrow(
                () -> IntegracionProlog.consultarEntrenamientosMinimos(),
                "La consulta a Prolog no debería lanzar excepción"
        );
    }

    @Test
    public void testConsultaEntrenamientosRetornaEntero() {
        IntegracionProlog.generarBaseDeConocimiento();

        int resultado = IntegracionProlog.consultarEntrenamientosMinimos();

        assertTrue(resultado >= 0,
                "El resultado debe ser un número entero no negativo");
    }

    @Test
    public void testFallaSiElArchivoPrologNoExiste() {
        File f = new File(PL_FILE);
        if (f.exists()) f.delete();

        assertThrows(RuntimeException.class,
                () -> IntegracionProlog.consultarEntrenamientosMinimos(),
                "Debe fallar si el archivo .pl no existe");
    }

    @Test
    public void testMostrarVariablesProlog() {
        System.out.println("[DEBUG] swipl.home = " + System.getProperty("jpl.swipl.home"));
        System.out.println("[DEBUG] java.library.path = " + System.getProperty("java.library.path"));

        Query q = new Query("current_prolog_flag(home, X)");
        System.out.println("[DEBUG] Prolog home detectado = " + q.oneSolution().get("X"));
    }

    @Test
    public void testCargaManualDelArchivoPL() {
        IntegracionProlog.generarBaseDeConocimiento();

        String consulta = String.format("consult('%s')", PL_FILE.replace("\\", "/"));

        Query q = new Query(consulta);

        assertTrue(q.hasSolution(),
                "Prolog debe poder cargar manualmente el archivo PL");
    }

    @Test
    public void testArchivoPLNoSeCorrompe() throws Exception {
        IntegracionProlog.generarBaseDeConocimiento();

        String contenido = Files.readString(new File(PL_FILE).toPath());

        assertFalse(contenido.contains("null"), "El archivo PL no debe contener 'null'");
        assertFalse(contenido.contains("??"), "El archivo PL no debe contener texto inválido");
    }

    @Test
    public void testReglasEstaticasBienFormadas() throws Exception {
        IntegracionProlog.generarBaseDeConocimiento();

        String contenido = Files.readString(new File(PL_FILE).toPath());

        assertTrue(contenido.contains("coste_entrenamiento("),
                "Debe existir la regla coste_entrenamiento/3");

        assertTrue(contenido.contains(":-"),
                "El PL debería contener al menos una regla");
    }

    @Test
    public void testSimularArchivoPLCorrupto() throws Exception {
        File f = new File(PL_FILE);
        f.delete();
        f.createNewFile();

        Files.writeString(f.toPath(), "ESTO NO ES PROLOG");

        assertThrows(RuntimeException.class,
                () -> IntegracionProlog.consultarEntrenamientosMinimos(),
                "El sistema debe fallar ante un archivo PL corrupto");
    }

    @Test
    public void testConsultaConTimeout() {
        IntegracionProlog.generarBaseDeConocimiento();

        assertTimeoutPreemptively(
                java.time.Duration.ofSeconds(2),
                () -> {
                    Query q = new Query("sleep(1)");
                    q.hasSolution();
                },
                "La consulta no debe exceder 2 segundos"
        );
    }
}

