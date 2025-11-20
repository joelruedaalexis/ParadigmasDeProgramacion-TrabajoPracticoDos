package recital;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import artista.ArtistaBase;
import cancion.Cancion;
import cancion.IntegranteDeUnRol;

public class TransaccionAsignacionDeTodasLasCanciones {
	private EstadoDeTransaccion estado;
	private Map<Cancion, Map<String, IntegranteDeUnRol>> artistasCandidatosAsignadosACancion;
	private Set<Cancion> cancionesConRolesFaltantes;
	private Map<ArtistaBase, Integer> artistasXCantDisponiblesDeCanciones;
	private List<ArtistaBase> artistasDisponiblesParaSerEntrenados;
	private Map<String, List<ArtistaBase>> artistasEntrenadosEnRol;

	protected TransaccionAsignacionDeTodasLasCanciones(
			Map<Cancion, Map<String, IntegranteDeUnRol>> artistasCandidatosAsignadosACancion) {
		this.artistasCandidatosAsignadosACancion = artistasCandidatosAsignadosACancion;
		estado = EstadoDeTransaccion.EN_CURSO;
	}

	protected void confirmarTransaccion() {
		estado = EstadoDeTransaccion.CONFIRMADA;
	}

	public boolean esTransaccionCommitted() {
		return estado == EstadoDeTransaccion.CONFIRMADA;
	}

	public EstadoDeTransaccion getEstadoDeTransaccion() {
		return estado;
	}

	public boolean sePuedenEntrenarParaTodosLosRoles() {
		int cuposDeCancion = 0;
		Map<ArtistaBase, Integer> artistasXCantDisponiblesDeCanciones = new HashMap<>(
				this.artistasXCantDisponiblesDeCanciones);
		for (Map.Entry<Cancion, Map<String, IntegranteDeUnRol>> nodo : artistasCandidatosAsignadosACancion.entrySet()) {
			Cancion cancion = nodo.getKey();
			cuposDeCancion = 0;
			for (Map.Entry<String, IntegranteDeUnRol> integrantesDeRolDeCancion : nodo.getValue().entrySet()) {
				IntegranteDeUnRol integranteDeUnRol = integrantesDeRolDeCancion.getValue();
				cuposDeCancion += integranteDeUnRol.getCantDeCuposDisponibles();
			}
			Set<ArtistaBase> candidatosUsadosEnCancion = getCandidatosDeCancion(cancion);
			for (int i = 0; i < artistasDisponiblesParaSerEntrenados.size() && cuposDeCancion > 0; i++) {
				ArtistaBase artista = artistasDisponiblesParaSerEntrenados.get(i);
				if (!candidatosUsadosEnCancion.contains(artista)
						&& artistasXCantDisponiblesDeCanciones.get(artista) > 0) {
					artistasXCantDisponiblesDeCanciones.put(artista,
							artistasXCantDisponiblesDeCanciones.get(artista) - 1);
					cuposDeCancion--;
				}
			}
			if (cuposDeCancion != 0)
				return false;
		}
		return true;
	}

	private Set<ArtistaBase> getCandidatosDeCancion(Cancion cancion) {
		Set<ArtistaBase> candidatosUsadosEnCancion = new HashSet<>();
		for (Map.Entry<String, IntegranteDeUnRol> integrantesDeRol : artistasCandidatosAsignadosACancion.get(cancion)
				.entrySet()) {
			candidatosUsadosEnCancion.addAll(integrantesDeRol.getValue().getListaDeIntegrantes());
		}
		return candidatosUsadosEnCancion;
	}

	public String entrenarArtistasRecomendadosYAsignarLosCandidatos(int opcion) {
		if (estado != EstadoDeTransaccion.EN_CURSO)
			throw new IllegalStateException(
					"La transaccion no se peude realizar porque se encuentra " + estado.toString());
		if (opcion == OpcionDeTransaccion.NO) {
			estado = EstadoDeTransaccion.CANCELADA;
			return "No se decidió entrenar artistas para las canciones.\n";
		}
		if (opcion == OpcionDeTransaccion.SI && !sePuedenEntrenarParaTodosLosRoles()) {
			estado = EstadoDeTransaccion.CANCELADA;
			return "No hay artistas disponibles para entrenar y asignar los roles faltantes";
		}
		artistasEntrenadosEnRol = new HashMap<>();
		Iterator<Cancion> iterador = cancionesConRolesFaltantes.iterator();
		Set<ArtistaBase> artistasUsadosEnCancion = new HashSet<>();
		while (iterador.hasNext()) {
			Cancion cancion = iterador.next();
			artistasUsadosEnCancion = getCandidatosDeCancion(cancion);
			for (Map.Entry<String, IntegranteDeUnRol> integrantesXRol : artistasCandidatosAsignadosACancion.get(cancion)
					.entrySet()) {
				String rol = integrantesXRol.getKey();
				IntegranteDeUnRol integrantesDeUnRol = integrantesXRol.getValue();
				int cupos = integrantesDeUnRol.getCantDeCuposDisponibles();
				List<ArtistaBase> listaArtistasEntrenadosEnRol;
				if (!artistasEntrenadosEnRol.containsKey(rol)) {
					listaArtistasEntrenadosEnRol = new ArrayList<ArtistaBase>();
					artistasEntrenadosEnRol.put(rol, listaArtistasEntrenadosEnRol);
				} else
					listaArtistasEntrenadosEnRol = artistasEntrenadosEnRol.get(rol);
//				Si entra a este for es xq ya entrené a artistas con este rol. Ahora asigno al artista SI Y SOLO SI tiene su cantMaxCanciones > 0
				for (int i = 0; i < listaArtistasEntrenadosEnRol.size() && cupos > 0; i++) {
					ArtistaBase artista = listaArtistasEntrenadosEnRol.get(i);
					if (!artistasUsadosEnCancion.contains(artista)
							&& artistasXCantDisponiblesDeCanciones.get(artista) > 0) {
						artistasXCantDisponiblesDeCanciones.put(artista,
								artistasXCantDisponiblesDeCanciones.get(artista) - 1);
						integrantesDeUnRol.agregarIntegrante(artista);
						artistasUsadosEnCancion.add(artista);
						cupos--;
					}
				}
//				Si entra a este for es xq NO tengo artistas entrenados (o no estan disponibles) en este rol
				for (int i = 0; i < artistasDisponiblesParaSerEntrenados.size() && cupos > 0; i++) {
					ArtistaBase artista = artistasDisponiblesParaSerEntrenados.get(i);
					if (!artistasUsadosEnCancion.contains(artista)
							&& artistasXCantDisponiblesDeCanciones.get(artista) > 0) {
						artistasXCantDisponiblesDeCanciones.put(artista,
								artistasXCantDisponiblesDeCanciones.get(artista) - 1);
						integrantesDeUnRol.agregarIntegrante(artista);
						artistasUsadosEnCancion.add(artista);
						artistasEntrenadosEnRol.get(rol).addLast(artista);
						cupos--;
					}
				}
			}
			artistasUsadosEnCancion.clear();
		}
		String str = "";
//		Ahora asigno los artistas a sus canciones
		for (Map.Entry<Cancion, Map<String, IntegranteDeUnRol>> cancionConSusIntegrantes : artistasCandidatosAsignadosACancion
				.entrySet()) {
			Cancion cancion = cancionConSusIntegrantes.getKey();
			for (Map.Entry<String, IntegranteDeUnRol> artistasEntrenadosConRol : cancionConSusIntegrantes.getValue()
					.entrySet()) {
				String rol = artistasEntrenadosConRol.getKey();
				IntegranteDeUnRol integrantesDeUnRol = artistasEntrenadosConRol.getValue();
				for (ArtistaBase artista : integrantesDeUnRol.getListaDeIntegrantes()) {
					cancion.agregarArtista(rol, artista);
				}
			}
			str = "->" + cancion.toString() + "\n";
		}
		estado = EstadoDeTransaccion.CONFIRMADA;
		return str;
	}

	protected void registrarFallaEnAsignacion(Set<Cancion> cancionesConRolesFaltantes,
			Map<ArtistaBase, Integer> artistasXCantDisponiblesDeCanciones, List<ArtistaBase> artistasDisponibles) {
		this.cancionesConRolesFaltantes = cancionesConRolesFaltantes;
		this.artistasXCantDisponiblesDeCanciones = artistasXCantDisponiblesDeCanciones;

		this.artistasDisponiblesParaSerEntrenados = artistasDisponibles.stream()
				.filter(a -> !a.perteneceADiscografica() && !a.estaAsignadoAlmenosAUnaCancion())
				.collect(Collectors.toList());

		if (!sePuedenEntrenarParaTodosLosRoles())
			estado = EstadoDeTransaccion.CANCELADA;
	}

	public String getInformeDeAsignacionesDeArtistas() {
		if (estado == EstadoDeTransaccion.CONFIRMADA)
			return getInformeParaAsignacionExitosa();
		else if (estado == EstadoDeTransaccion.EN_CURSO)
			return getInformeParaFallaEnAsignacion();
		return "No hay artistas suficientes para entrenar en todos los roles";
	}

	private String getInformeParaFallaEnAsignacion() {
		String str = "Para completar todos los roles del repertorio se necesitan entrenar a artistas. "
				+ "Las canciones y roles con espacios disponibles son :\n";
		artistasEntrenadosEnRol = new HashMap<>();

		Map<ArtistaBase, Integer> artistasXCantDisponiblesDeCanciones = new HashMap<>(
				this.artistasXCantDisponiblesDeCanciones);
		Iterator<Cancion> iterador = cancionesConRolesFaltantes.iterator();
		Set<ArtistaBase> artistasUsadosEnCancion = new HashSet<>();
//		Map<String, List<ArtistaBase>>
		while (iterador.hasNext()) {
			Cancion cancion = iterador.next();
			artistasUsadosEnCancion = getCandidatosDeCancion(cancion);
			str += "-> " + cancion.getTitulo() + "\n";
			for (Map.Entry<String, IntegranteDeUnRol> integrantesXRol : artistasCandidatosAsignadosACancion.get(cancion)
					.entrySet()) {
				if (!integrantesXRol.getValue().hayCuposDisponibles())
					continue;
				String rol = integrantesXRol.getKey();
				IntegranteDeUnRol integrantesDeUnRol = integrantesXRol.getValue();
				int cupos = integrantesDeUnRol.getCantDeCuposDisponibles();
				List<ArtistaBase> listaArtistasEntrenadosEnRol;
				if (!artistasEntrenadosEnRol.containsKey(rol)) {
					listaArtistasEntrenadosEnRol = new ArrayList<ArtistaBase>();
					artistasEntrenadosEnRol.put(rol, listaArtistasEntrenadosEnRol);
				} else
					listaArtistasEntrenadosEnRol = artistasEntrenadosEnRol.get(rol);
//				Si entra a este for es xq ya entrené a artistas con este rol. Ahora asigno al artista SI Y SOLO SI tiene su cantMaxCanciones > 0
				str += "\t~" + rol + ": ";
				for (int i = 0; i < listaArtistasEntrenadosEnRol.size() && cupos > 0; i++) {
					ArtistaBase artista = listaArtistasEntrenadosEnRol.get(i);
					if (!artistasUsadosEnCancion.contains(artista)
							&& artistasXCantDisponiblesDeCanciones.get(artista) > 0) {
						artistasXCantDisponiblesDeCanciones.put(artista,
								artistasXCantDisponiblesDeCanciones.get(artista) - 1);
						artistasUsadosEnCancion.add(artista);
						str += artista.getNombre() + ", ";
						cupos--;
					}
				}

//				Si entra a este for es xq NO tengo artistas entrenados (o no estan disponibles) en este rol
				for (int i = 0; i < artistasDisponiblesParaSerEntrenados.size() && cupos > 0; i++) {
					ArtistaBase artista = artistasDisponiblesParaSerEntrenados.get(i);
					if (!artistasUsadosEnCancion.contains(artista)
							&& artistasXCantDisponiblesDeCanciones.get(artista) > 0) {
						artistasXCantDisponiblesDeCanciones.put(artista,
								artistasXCantDisponiblesDeCanciones.get(artista) - 1);
						integrantesDeUnRol.agregarIntegrante(artista);
						artistasUsadosEnCancion.add(artista);
						str += artista.getNombre() + ", ";
						cupos--;
					}
				}
				str += "\n";
			}
		}
		return str;
	}

	private String getInformeParaAsignacionExitosa() {
		String str = "Se han asignados los artistas con éxito. La información actualizada de las canciones son:\n";
		for (Cancion cancion : artistasCandidatosAsignadosACancion.keySet()) {
			str += "->" + cancion.toString();
		}
		return str;
	}
}
