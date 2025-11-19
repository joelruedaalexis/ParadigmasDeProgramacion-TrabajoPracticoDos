package recital;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import artista.ArtistaBase;
import cancion.Cancion;
import cancion.IntegranteDeUnRol;

public class TransaccionAsignacionDeCancion {
	private EstadoDeTransaccion estado;
	private List<ArtistaBase> artistasDisponiblesParaSerEntrenados;
	private Cancion cancion;
	private Map<String, IntegranteDeUnRol> candidatosXRol;

	protected TransaccionAsignacionDeCancion(Cancion cancion) {
		this.cancion = cancion;
		estado = EstadoDeTransaccion.EN_CURSO;
	}

	public EstadoDeTransaccion getEstadoDeTransaccion() {
		return estado;
	}

	public boolean esTransaccionEnCurso() {
		return estado == EstadoDeTransaccion.EN_CURSO;
	}

	protected boolean esTransaccionCommitted() {
		return estado == EstadoDeTransaccion.CONFIRMADA;
	}

	protected void confirmarTransaccion() {
		estado = EstadoDeTransaccion.CONFIRMADA;
	}

	public boolean sePuedenEntrenarArtistasSuficientes() {
		int cantDeArtistasNecesariosParaEntrenar = 0;
		for (IntegranteDeUnRol integrantesDeRol : candidatosXRol.values())
			cantDeArtistasNecesariosParaEntrenar += integrantesDeRol.getCantDeCuposDisponibles();
		return cantDeArtistasNecesariosParaEntrenar <= artistasDisponiblesParaSerEntrenados.size();
	}

	public String getInformeDeAsignacionDeArtistas() {
		if (estado == EstadoDeTransaccion.CONFIRMADA)
			return getInformeParaAsignacionExitosa();
		else if (estado == EstadoDeTransaccion.EN_CURSO)
			return getInformeParaFallaEnAsignacion();
		return "No se pueden asignar a todos los roles porque no hay artistas suficientes para entrenar.";
	}

	private String getInformeParaFallaEnAsignacion() {
		String informe = "";
		informe += "Para completar los integrantes para la canción \"" + cancion.getTitulo()
				+ "\"elegida se necesitan artistas con los siguientes roles:\n";

		String rolesFaltantes = "";
		for (Map.Entry<String, IntegranteDeUnRol> nodo : candidatosXRol.entrySet()) {
			String rol = nodo.getKey();
			int cantidad = nodo.getValue().getCantDeCuposDisponibles();
			if (nodo.getValue().hayCuposDisponibles())
				rolesFaltantes += String.format("\t->%s: cantidad %d\n", rol, cantidad);
		}
		informe += rolesFaltantes + "\n";
		String artistasRecomendables = "";
		Iterator<ArtistaBase> iteradorArtistasRecomendables = artistasDisponiblesParaSerEntrenados.iterator();
		for (Map.Entry<String, IntegranteDeUnRol> nodo : candidatosXRol.entrySet()) {
			String rol = nodo.getKey();
			int cantidad = nodo.getValue().getCantDeCuposDisponibles();
			while (cantidad > 0) {
				artistasRecomendables += String.format("\t->%s con el rol \"%s\"\n",
						iteradorArtistasRecomendables.next().getNombre(), rol);
				cantidad--;
			}
		}
		informe += "Se recomiendan entrenar a los siguientes artistas:\n";
		informe += artistasRecomendables;
		return informe;
	}

	private String getInformeParaAsignacionExitosa() {
		return "Se han asignados los artistas con éxito. La información actualizada de la canción es:\n"
				+ cancion.toString();
	}

	public String entrenarArtistasRecomendadosYAsignarLosCandidatos(int opcion) {
		if (opcion == OpcionDeTransaccion.NO) {
			estado = EstadoDeTransaccion.CANCELADA;
			return "No se decidió entrenar artistas para la canción.\n" + cancion.toString();
		}
		if (estado != EstadoDeTransaccion.EN_CURSO) {
			throw new IllegalStateException(
					"La transaccion no se peude realizar porque se encuentra " + estado.toString());
		}
		for (Map.Entry<String, IntegranteDeUnRol> nodo : candidatosXRol.entrySet()) {
			String rol = nodo.getKey();
			List<ArtistaBase> lista = nodo.getValue().getListaDeIntegrantes();
			lista.forEach(artista -> {
				cancion.agregarArtista(rol, artista);
				artista.asignar(this.cancion);
			});
			for (int i = 0; i < nodo.getValue().getCantDeCuposDisponibles(); i++) {
				ArtistaBase artista = artistasDisponiblesParaSerEntrenados.get(i);
				artista.entrenarNuevoRol(rol);
				cancion.agregarArtista(rol, artista);
				artista.asignar(this.cancion);

			}
		}
		estado = EstadoDeTransaccion.CONFIRMADA;
		return cancion.toString();
	}

	public void registrarFallaEnAsignacion(Map<String, IntegranteDeUnRol> rolesXIntegrantesCandidatos,
			List<ArtistaBase> listaDeArtistasCandidatos) {
		this.candidatosXRol = rolesXIntegrantesCandidatos;
		this.artistasDisponiblesParaSerEntrenados = listaDeArtistasCandidatos.stream()
				.filter(a -> !a.perteneceADiscografica() && !a.estaAsignadoAlmenosAUnaCancion()).toList();

		if (!sePuedenEntrenarArtistasSuficientes())
			estado = EstadoDeTransaccion.CANCELADA;
	}
}
