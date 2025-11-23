package recital;

import java.util.List;

import artista.ArtistaBase;

public abstract class Transaccion {
	protected EstadoDeTransaccion estado;
	protected List<ArtistaBase> artistasDisponiblesParaSerEntrenados;

	public Transaccion() {
		estado = EstadoDeTransaccion.EN_CURSO;
	}

	protected void confirmarTransaccion() {
		estado = EstadoDeTransaccion.CONFIRMADA;
	}

	public abstract String entrenarArtistasRecomendadosYAsignarLosCandidatos(int opcion);

	public boolean esTransaccionCommitted() {
		return estado == EstadoDeTransaccion.CONFIRMADA;
	}

	public String getInformeDeAsignacionDeArtistas() {
		if (estado == EstadoDeTransaccion.CONFIRMADA)
			return getInformeParaAsignacionExitosa();
		else if (estado == EstadoDeTransaccion.EN_CURSO)
			return getInformeParaFallaEnAsignacion();
		return "No se pueden asignar a todos los roles porque no hay artistas suficientes para entrenar.";
	}

	protected abstract String getInformeParaAsignacionExitosa();

	protected abstract String getInformeParaFallaEnAsignacion();

	public abstract boolean sePuedenEntrenarParaTodosLosRoles();

}
