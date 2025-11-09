package menu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import recital.Recital;
import recital.TransaccionAsignacionDeCancion;

public class Menu {
	private static final int salir = 0, rolesFaltantesParaCancion = 1, rolesFaltantesParaTodasLasCanciones = 2,
			contratarArtistasParaUnaCancion = 3, contratarArtistasParaTodasLasCanciones = 4, entrenarArtista = 5,
			listarArtistasContratados = 6, listarCanciones = 7, prolog = 8, quitarArtistaDeCancion = 9,
			quitarArtistaDeTodasLasCanciones = 10, quitarArtistaContratadoDelLineUp = 11, guardarEstadoDelRecital = 12,
			cargarEstadoDelRecital = 13;
	private Scanner scanner;
	private Recital recital;
	private List<String> recitalesGuardados;

	public Menu(Scanner scanner, Recital recital) {
		this.recital = recital;
		this.scanner = scanner;
		recitalesGuardados = new ArrayList<>();
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
				System.out.println("XD");
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
				System.out.println(recital.cantDeRolesFaltantesParaUnaCancion(indexCancion));
				break;
			case rolesFaltantesParaTodasLasCanciones:// 2
				System.out.println(recital.cantDeRolesFaltantesParaTodasLasCanciones());
//				int cantDeRolesFaltantesParaTodasLasCanciones = recital.cantDeRolesFaltantesParaTodasLasCanciones();
//				if (cantDeRolesFaltantesParaTodasLasCanciones == 0)
//					System.out.println("->Todas las canciones tienen asignado a un artista.\n");
//				else
//					System.out.printf("->Hay %d rol(es) sin asignar.\n", cantDeRolesFaltantesParaTodasLasCanciones);
				break; 
			case contratarArtistasParaUnaCancion:// 3
				indexCancion = elegirCancion();
				TransaccionAsignacionDeCancion resultadoTransaccion = recital
						.contratarArtistasParaUnaCancion(indexCancion);
				System.out.println(resultadoTransaccion.getInformeDeAsignacionDeArtistas());
				if (!resultadoTransaccion.esTransaccionCommitted()) {
					if (resultadoTransaccion.sePuedenEntrenarArtistasSuficientes()) {
						System.out.printf(
								"Seleccione la opcion \"Si\" si desea entrenarlos y luego se asignarán automaticamente a la canción:\n"
										+ "%02d)SI\n%02d)NO\n",
								TransaccionAsignacionDeCancion.SI, TransaccionAsignacionDeCancion.NO);
						int opcionEntrenar = ingresarOpcionVal(TransaccionAsignacionDeCancion.SI,
								TransaccionAsignacionDeCancion.NO);
						String informe = resultadoTransaccion
								.entrenarArtistasRecomendadosYAsignarLosCandidatos(opcionEntrenar);
						System.out.println(informe);
					}
				}
//				System.out.println(recital.getInformacionSobreCancion(indexCancion));
				break;
			case contratarArtistasParaTodasLasCanciones:// 4
				recital.contratarArtistasParaTodasLasCanciones();
				break;
			case entrenarArtista:// 5
				Map<String, Integer> mapArtistaAEntrenar = recital.getListadoArtistasContratadosSinSerAsignados();
				System.out.println("Elija un artista contratado para entrenarle un nuevo rol:");
				String nombreArtistaAEntrenar = elegirArtistaAEntrenar(new ArrayList<>(mapArtistaAEntrenar.keySet()));
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
				indexArtista = this.elegirArtistaAQuitarDeTodasLasCanciones(listaArtistasAsignados);
//				System.out.println(listaArtistasAsignados.get(indexArtista));
				if (indexArtista == -1)
					System.out.println("Todavía no se han asignado artistas.");
				else
					recital.quitarArtistaDeTodasLasCanciones(listaArtistasAsignados.get(indexArtista));
				break;
			case quitarArtistaContratadoDelLineUp:// 11
				Map<String, Integer> xd = recital.getListadoArtistasContratados();
				System.out.println("->Elija un artista contratado para quitarlo del lineUp.");
				String nombreArtista = this.elegirArtistaAEntrenar(new ArrayList<>(xd.keySet()));
				recital.quitarArtistaDelLineUp(xd.get(nombreArtista));
				break;
			case guardarEstadoDelRecital:// 12
				String ruta = this.ingresarRutaParaGuardarRecital();
				try {
					recital.guardarEnArchivoJSON(ruta);
					System.out.println("->El archivo se ha guardado con éxito.");
					recitalesGuardados.add(ruta);
				} catch (IOException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					System.out.println(e.getMessage());
				}
				break;
			case cargarEstadoDelRecital:// 13
				break;
			}
			if (opcion != salir) {
				pausar();
			}
		} while (opcion == salir);//<------------------------CAMBIAR ESTO PARA LOOPEAR
//		} while (opcion != salir);
		System.out.println("Saliendo...");
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

	public String elegirArtistaAEntrenar(List<String> lista) {
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
		System.out.printf("00) Salir \n01) rolesFaltantesParaCancion \n02) rolesFaltantesParaTodasLasCanciones\n"
				+ "03) contratarArtistasParaUnaCancion \n04) contratarArtistasParaTodasLasCanciones \n05) entrenarArtista \n"
				+ "06) listarArtistasContratados \n07) listarCanciones \n08) prolog\n09) quitarArtistaDeCancion \n"
				+ "10) quitarArtistaDeTodasLasCanciones \n11)quitarArtistaDelLineUp \n12)guardarEstadoDelRecital \n13) cargarEstadoDelRecital\n");

	}

	private String ingresarRutaParaGuardarRecital() {
		String ruta = "";
		do {
			System.out.printf("-> Ingrese la ruta del archivo donde desea guardar el recital: ");
			ruta = this.scanner.nextLine().trim();

			if (!ruta.endsWith(".json") || ruta.length() <= 5) {
				System.out.println("->La ruta del archivo es inválida.");
			}
		} while (!ruta.endsWith(".json") || ruta.length() <= 5);

		return ruta;
	}
}
