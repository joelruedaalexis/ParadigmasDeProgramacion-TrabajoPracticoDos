package menu;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Collectors;

import recital.EstadoDeTransaccion;
import recital.OpcionDeTransaccion;
import recital.Recital;
import recital.TransaccionAsignacionDeCancion;
import recital.TransaccionAsignacionDeTodasLasCanciones;

public class Menu {
	private static final int salir = 0, rolesFaltantesParaCancion = 1, rolesFaltantesParaTodasLasCanciones = 2,
			contratarArtistasParaUnaCancion = 3, contratarArtistasParaTodasLasCanciones = 4, entrenarArtista = 5,
			listarArtistasContratados = 6, listarCanciones = 7, prolog = 8, quitarArtistaDeCancion = 9,
			quitarArtistaDeTodasLasCanciones = 10, quitarArtistaContratadoDelLineUp = 11, guardarEstadoDelRecital = 12,
			cargarEstadoDelRecital = 13;
	private static final String rutaCarpetaRecitales = "src/main/resources/recitalesGuardados";
	private Scanner scanner;
	private Recital recital;
	private List<String> recitalesGuardados;

	public Menu(Scanner scanner, Recital recital) throws IOException {
		this.recital = recital;
		this.scanner = scanner;
		recitalesGuardados = Files.list(Paths.get("src", "main", "resources", "recitalesGuardados"))
				.map(f -> f.getFileName().toString()).collect(Collectors.toList());
		System.out.println(recitalesGuardados);
	}

	private int ingresarOpcionVal(int limInf, int limSup) {
		int opcion;
		do {
			try {
				opcion = scanner.nextInt();
			} catch (NoSuchElementException e) {
				opcion = -1;
			} finally {
				scanner.nextLine();
			}
			if (opcion < limInf || opcion > limSup)
				System.out.println("Opción inválida. Ingrese Nuevamente: ");
		} while (opcion < limInf || opcion > limSup);
		return opcion;
	}

	public void iniciar() {
		int opcion, indexCancion, indexArtista;
		do {
			limpiarConsola();
			mostrarOpciones();
//			recital.aux();
			opcion = ingresarOpcionVal(0, 13);
			switch (opcion) {
			case rolesFaltantesParaCancion:// 1
				indexCancion = elegirCancion();
				Map<String, Integer> rolesFaltantesXCupos = recital.cantDeRolesFaltantesParaUnaCancion(indexCancion);
				String titulo = recital.getListadoDeTitulosDeCanciones().get(indexCancion);
				if (rolesFaltantesXCupos.isEmpty())
					System.out.printf("Todos los roles de la canción \"%s\"ya han sido asignados.\n", titulo);
				else {
					String str = String.format("A la canción \"%s\" le faltan los siguientes roles a asignar:\n",
							titulo);
					for (Map.Entry<String, Integer> nodo : rolesFaltantesXCupos.entrySet()) {
						String rol = nodo.getKey();
						Integer cupos = nodo.getValue();
						str += String.format("\t~%d %s.\n", cupos, rol);
					}
					System.out.println(str);
				}
				break;
			case rolesFaltantesParaTodasLasCanciones:// 2
				Map<String, Integer> rolesFaltantesTotalesConSusCupos = recital
						.cantDeRolesFaltantesParaTodasLasCanciones();
				if (rolesFaltantesTotalesConSusCupos.isEmpty())
					System.out.println("Todas las canciones ya tienen sus roles asignados a artistas.");
				else {
					String str = "Para poder asignar todas las canciones con artistas contratados se necesitan que tengan los siguientes roles:\n";
					for (Map.Entry<String, Integer> nodo : rolesFaltantesTotalesConSusCupos.entrySet()) {
						String rol = nodo.getKey();
						Integer cupos = nodo.getValue();
						str += String.format("\t~%d %s.\n", cupos, rol);
					}
					System.out.println(str);
				}
				break;
			case contratarArtistasParaUnaCancion:// 3
				indexCancion = elegirCancion();
				TransaccionAsignacionDeCancion transaccion1 = recital.contratarArtistasParaUnaCancion(indexCancion);
				System.out.println(transaccion1.getInformeDeAsignacionDeArtistas());
				if (transaccion1.esTransaccionEnCurso()) {
					System.out.printf(
							"Seleccione la opcion \"Si\" si desea entrenarlos y luego se asignarán automaticamente a la canción:\n"
									+ "%02d)SI\n%02d)NO\n",
							OpcionDeTransaccion.SI, OpcionDeTransaccion.NO);
					int opcionEntrenar = ingresarOpcionVal(OpcionDeTransaccion.SI, OpcionDeTransaccion.NO);
					String informe = transaccion1.entrenarArtistasRecomendadosYAsignarLosCandidatos(opcionEntrenar);
					System.out.println(informe);
				}
//				System.out.println(recital.getInformacionSobreCancion(indexCancion));
				break;
			case contratarArtistasParaTodasLasCanciones:// 4
				TransaccionAsignacionDeTodasLasCanciones transaccion2 = recital
						.contratarArtistasParaTodasLasCanciones();
				System.out.println(transaccion2.getInformeDeAsignacionesDeArtistas());
				if (transaccion2.getEstadoDeTransaccion() == EstadoDeTransaccion.EN_CURSO) {
					System.out.printf(
							"Seleccione la opcion \"Si\" si desea entrenarlos y luego se asignarán automaticamente a la canción:\n"
									+ "%02d)SI\n%02d)NO\n",
							OpcionDeTransaccion.SI, OpcionDeTransaccion.NO);
					int opcionEntrenar = ingresarOpcionVal(OpcionDeTransaccion.SI, OpcionDeTransaccion.NO);
					String informe = transaccion2.entrenarArtistasRecomendadosYAsignarLosCandidatos(opcionEntrenar);
					System.out.println(informe);
				}
				break;
			case entrenarArtista:// 5
				Map<String, Integer> mapArtistaAEntrenar = recital.getListadoArtistasContratadosSinSerAsignados();
				System.out.println("Elija un artista contratado para entrenarle un nuevo rol:");
				String nombreArtistaAEntrenar = elegirArtista(new ArrayList<>(mapArtistaAEntrenar.keySet()));
				indexArtista = mapArtistaAEntrenar.get(nombreArtistaAEntrenar);
				System.out.printf("Elija qué rol desea que %s entrene.\n", nombreArtistaAEntrenar);
				String nuevoRol = this.elegirRolDelArtistaAEntrenar(indexArtista);
				recital.entrenarArtista(indexArtista, nuevoRol);
				break;
			case listarArtistasContratados:// 6
				System.out.println(recital.getInformacionDeArtistasContratados());
				break;
			case listarCanciones:// 7
				System.out.println(recital.getInformacionCompletaDelRepertorio());
//				this.imprimirListadoDeCanciones();
				break;
			case prolog:// 8
				recital.prolog();
				break;
			case quitarArtistaDeCancion:// 9
				indexCancion = elegirCancion();
				indexArtista = this.elegirArtistaAQuitarDeCancion(indexCancion);
//				System.out.printf("index C %d index A %d\n", indexCancion, indexArtista);
				if (indexArtista == -1)
					System.out.println("La canción elegida todavía no tiene artistas asignados.");
				else
					recital.quitarArtistaDeCancion(indexArtista, indexCancion);
				break;
			case quitarArtistaDeTodasLasCanciones:// 10
				List<String> listaArtistasAsignados = recital
						.getListaDeNombresDeArtistasQueEstanAsignadosAlMenosACancion();
				if (listaArtistasAsignados.isEmpty())
					System.out.println("Todavía no se han asignado artistas.");
				else {
					indexArtista = this.elegirArtistaAQuitarDeTodasLasCanciones(listaArtistasAsignados);
					recital.quitarArtistaDeTodasLasCanciones(listaArtistasAsignados.get(indexArtista));
				}
				break;
			case quitarArtistaContratadoDelLineUp:// 11
				Map<String, Integer> artistas = recital.getListadoArtistasContratados();
				if (artistas.isEmpty()) {
					System.out.println("No hay artistas contratados para quitar.");
					break;
				}
				System.out.println("Elija un artista contratado para quitarlo del lineUp.");
				String nombreArtista = this.elegirArtista(new ArrayList<>(artistas.keySet()));

				recital.quitarArtistaDelLineUp(artistas.get(nombreArtista));
				break;
			case guardarEstadoDelRecital:// 12
				String nombreArchivo = this.ingresarArchivoParaGuardarRecital();
				try {
					recital.guardarEnArchivoJSON(Paths.get(rutaCarpetaRecitales, nombreArchivo).toString());
					System.out.println("-> El archivo se ha guardado con éxito.");
					recitalesGuardados.add(nombreArchivo);
				} catch (IOException e) {
					System.out.println("-> Error al guardar el archivo: " + e.getMessage());
				}
				break;
			case cargarEstadoDelRecital:// 13
				if (recitalesGuardados.isEmpty()) {
					System.out.println("No hay ningun recital guardado.");
					break;
				}
				int op2 = cargarArchivoDelRecital();
				String rutaArchivo = Paths.get(rutaCarpetaRecitales, recitalesGuardados.get(op2 - 1)).toString();
				try {
					recital.cargarEstadoDeArchivoJSON(rutaArchivo);
					System.out.println("El archivo se ha cargado con éxito.");
				} catch (IOException e) {
					System.out.println("Error al cargar el archivo: " + e.getMessage());
				}
				break;
			}
			if (opcion != salir) {
				pausar();
			}
//		} while (opcion == salir);//<------------------------CAMBIAR ESTO PARA LOOPEAR
		} while (opcion != salir);
		System.out.println("Saliendo...");
		try {
			String rutaArchivo = Paths.get("src", "main", "resources", "recitalesGuardados", "recital-out.json")
					.toString();
			recital.guardarEnArchivoJSON(rutaArchivo);
			System.out.println("Se ha guardado la información del recital en el archivo \"recital-out.json\".");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public int cargarArchivoDelRecital() {
		System.out.println("Elija uno de los siguientes estados para cargar:\n");
		for (int i = 0; i < recitalesGuardados.size(); i++) {
			System.out.printf("\t%02d) %s\n", i + 1, recitalesGuardados.get(i));
		}
		return ingresarOpcionVal(1, recitalesGuardados.size());
	}

	public int elegirCancion() {
		List<String> cancionero = recital.getListadoDeTitulosDeCanciones();
		System.out.println("Repertorio:");
		for (int i = 0; i < cancionero.size(); i++) {
			System.out.printf("%02d) %s\n", i + 1, cancionero.get(i));
		}
		return ingresarOpcionVal(1, cancionero.size()) - 1;
	}

	public int elegirArtistaAQuitarDeTodasLasCanciones(List<String> lista) {
		if (lista.isEmpty())
			return -1;
		for (int i = 0; i < lista.size(); i++) {
			System.out.printf("%02d) %s\n", i + 1, lista.get(i));
		}
		return ingresarOpcionVal(1, lista.size()) - 1;
	}

	public int elegirArtistaAQuitarDeCancion(int indexDelRepertorio) {
		List<String> lista = recital.getListadoDeIntegrantesDeCancion(indexDelRepertorio);
		if (lista.isEmpty())
			return -1;
		for (int i = 0; i < lista.size(); i++)
			System.out.printf("%02d) %s\n", i + 1, lista.get(i));
		return ingresarOpcionVal(1, lista.size()) - 1;
	}

	public String elegirArtista(List<String> lista) {
		for (int i = 0; i < lista.size(); i++)
			System.out.printf("%02d) %s\n", i + 1, lista.get(i));
		int index = ingresarOpcionVal(1, lista.size());
		return lista.get(index - 1);
	}

	public String elegirRolDelArtistaAEntrenar(int index) {
		List<String> rolesDisponibles = recital.getListaDeRolesDisponiblesParaEntrenarArtista(index);
		for (int i = 0; i < rolesDisponibles.size(); i++)
			System.out.printf("%02d) %s\n", i + 1, rolesDisponibles.get(i));
		int posRol = ingresarOpcionVal(1, rolesDisponibles.size());
		return rolesDisponibles.get(posRol - 1);
	}

	public void limpiarConsola() {
		System.out.printf("\n".repeat(50));
	}

	public void pausar() {
		System.out.printf("Presione cualquier tecla para continuar...");
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void mostrarOpciones() {
		System.out.println("Elija una de las siguientes opciones:");
		System.out.printf(
				"00) Salir \n01) Roles faltantes para una canción \n02) Roles faltantes para todas las canciones\n"
						+ "03) Contratar artistas para una canción \n04) Contratar artistas para todas las canciones \n05) Entrenar artista \n"
						+ "06) Listar artistas contratados \n07) Listar Canciones \n08) [PROLOG] - Consulta de entrenamientos mínimos\n09) Quitar artista de una canción \n"
						+ "10) Quitar artista de todas las canciones \n11) Quitar artista del LineUp \n12) Guardar estado del recital actual \n13) Cargar estado de un recital\n");

	}

	private String ingresarArchivoParaGuardarRecital() {
		String nombreArchivo = "";
		do {
			System.out.println("Ingrese el nombre del archivo para guardar el recital: ");
			nombreArchivo = this.scanner.nextLine().trim();
			if (!nombreArchivo.endsWith(".json") || nombreArchivo.length() <= 5) {
				System.out.println("El formato del archivo es inválido.");
			}
		} while (!nombreArchivo.endsWith(".json") || nombreArchivo.length() <= 5);
		return nombreArchivo;
	}
}
