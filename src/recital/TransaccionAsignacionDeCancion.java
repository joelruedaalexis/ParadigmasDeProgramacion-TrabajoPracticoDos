package recital;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import artista.ArtistaBase;
import cancion.Cancion;
import cancion.IntegranteDeUnRol;

public class TransaccionAsignacionDeCancion {
	public final static int SI = 0, NO = 1;
	boolean transaccionCommitted;
//	Map<String, Integer> rolesXCantidadFaltante;
//	Map<String, List<Artista>> rolXArtistaCandidato;
	List<ArtistaBase> listaDeArtistasPosiblesParaEntrenar;
	Cancion cancion;
	Map<String, IntegranteDeUnRol> rolesXIntegrantesCandidatos;

	protected TransaccionAsignacionDeCancion(Cancion cancion) {
		this.cancion = cancion;
	}

	public boolean esTransaccionCommitted() {
		return transaccionCommitted;
	}

	protected void confirmarTransaccion() {
		transaccionCommitted = true;
	}

	public boolean sePuedenEntrenarArtistasSuficientes() {
		int cantDeArtistasNecesariosParaEntrenar = 0;
		for (IntegranteDeUnRol integrantesDeRol : rolesXIntegrantesCandidatos.values()) {
			cantDeArtistasNecesariosParaEntrenar += integrantesDeRol.getCantDeCuposDisponibles();
		}

//		for (Integer cant : rolesXCantidadFaltante.values()) {
//			cantDeArtistasNecesariosParaEntrenar += cant;
//		}
		return cantDeArtistasNecesariosParaEntrenar <= listaDeArtistasPosiblesParaEntrenar.size();
	}

	public String getInformeDeAsignacionDeArtistas() {
		if (this.esTransaccionCommitted())
			return getInformeParaAsignacionExitosa();
		return getInformeParaAsignacionFallida();
	}

	private String getInformeParaAsignacionFallida() {
		String informe = "";
		informe += "Para completar los integrantes para la canción \"" + cancion.getTitulo()
				+ "\"elegida se necesitan artistas con los siguientes roles:\n";

		int cantDeArtistasNecesariosParaEntrenar = 0;
		for (IntegranteDeUnRol integrantesDeRol : rolesXIntegrantesCandidatos.values()) {
			cantDeArtistasNecesariosParaEntrenar += integrantesDeRol.getCantDeCuposDisponibles();
		}
//		for (Integer cant : rolesXCantidadFaltante.values()) {
//			cantDeArtistasNecesariosParaEntrenar += cant;
//		}
//		System.out.println(rolesXCantidadFaltante);

		String rolesFaltantes = "";
		for (Map.Entry<String, IntegranteDeUnRol> nodo : rolesXIntegrantesCandidatos.entrySet()) {
			String rol = nodo.getKey();
			int cantidad = nodo.getValue().getCantDeCuposDisponibles();
			if (nodo.getValue().hayCuposDisponibles())
				rolesFaltantes += String.format("\t->%s: cantidad %d\n", rol, cantidad);
		}
//		for (Map.Entry<String, Integer> nodo : rolesXCantidadFaltante.entrySet()) {
//			String rol = nodo.getKey();
//			int cantidad = nodo.getValue();
//			rolesFaltantes += String.format("\t->%s: cantidad %d\n", rol, cantidad);
//		}
		informe += rolesFaltantes + "\n";
		if (cantDeArtistasNecesariosParaEntrenar > listaDeArtistasPosiblesParaEntrenar.size()) {
			informe += "No hay artistas suficientes disponibles para entrenar :( \n";
			return informe;
		}

//		String rolesFaltantes = "";
		String artistasRecomendables = "";
		Iterator<ArtistaBase> iteradorArtistasRecomendables = listaDeArtistasPosiblesParaEntrenar.iterator();
		for (Map.Entry<String, IntegranteDeUnRol> nodo : rolesXIntegrantesCandidatos.entrySet()) {
			String rol = nodo.getKey();
			int cantidad = nodo.getValue().getCantDeCuposDisponibles();
//			rolesFaltantes += String.format("\t->%s\n", rol);
			while (cantidad > 0) {
				artistasRecomendables += String.format("\t->%s con el rol \"%s\"\n",
						iteradorArtistasRecomendables.next().getNombre(), rol);
				cantidad--;
			}
		}
//		for (Map.Entry<String, Integer> nodo : rolesXCantidadFaltante.entrySet()) {
//			String rol = nodo.getKey();
//			int cantidad = nodo.getValue();
		////			rolesFaltantes += String.format("\t->%s\n", rol);
//			while (cantidad > 0) {
//				artistasRecomendables += String.format("\t->%s con el rol \"%s\"\n",
//						iteradorArtistasRecomendables.next().getNombre(), rol);
//				cantidad--;
//			}
//		}
		informe += "Se recomiendan entrenar a los siguientes artistas:\n";
		informe += artistasRecomendables;
		return informe;
	}

	private String getInformeParaAsignacionExitosa() {
		return "Se han asignados los artistas con éxito. La información actualizada de la canción es:\n"
				+ cancion.toString();
	}

//	public boolean aux() {
//		List<Artista> lista = listaDeArtistasPosiblesParaEntrenar.stream()
//				.filter(a -> recital.artistaEstaAsignadoAUnaCancion(a)).toList();
//		int cantidad = 0;
//		for (Map.Entry<String, Integer> nodo : rolesXCantidadFaltante.entrySet()) {
//			cantidad += nodo.getValue();
//		}
//
//		return lista.size() < cantidad;
//	}

	public String entrenarArtistasRecomendadosYAsignarLosCandidatos(int opcion) {
		if (opcion == NO) {
			transaccionCommitted = false;
			return null;// Exception?
		}
		for (Map.Entry<String, IntegranteDeUnRol> nodo : rolesXIntegrantesCandidatos.entrySet()) {
			String rol = nodo.getKey();
			List<ArtistaBase> lista = nodo.getValue().getListaDeIntegrantes();
			lista.forEach(artista -> {
				cancion.agregarArtista(rol, artista);
				artista.asignar(this.cancion);
			});
			for (int i = 0; i < nodo.getValue().getCantDeCuposDisponibles(); i++) {
				ArtistaBase artista = listaDeArtistasPosiblesParaEntrenar.get(i);
				artista.entrenarNuevoRol(rol);
				cancion.agregarArtista(rol, artista);
				artista.asignar(this.cancion);

			}
		}
//		for (Map.Entry<String, List<Artista>> nodo : rolXArtistaCandidato.entrySet()) {
//			String rol = nodo.getKey();
//			List<Artista> lista = nodo.getValue();
//			lista.forEach(artista -> {
//				cancion.agregarArtista(rol, artista);
//				artista.asignar(this.cancion);
//			});
//		}

//		for (Map.Entry<String, Integer> nodo : rolesXCantidadFaltante.entrySet()) {
//			String rol = nodo.getKey();
//			int cantidad = nodo.getValue();
//			for (int i = 0; i < cantidad; i++) {
//				Artista artista = listaDeArtistasPosiblesParaEntrenar.get(i);
//				artista.entrenarNuevoRol(rol);
//				cancion.agregarArtista(rol, artista);
//				artista.asignar(this.cancion);
//			}
//		}
		transaccionCommitted = true;
		return cancion.toString();
	}

//	protected void cancelarTransaccion(Map<String, Integer> rolesXCantidadFaltante,
//			List<Artista> listaDeArtistasCandidatos, Map<String, List<Artista>> rolXArtistaCandidato) {
//		transaccionCommitted = false;
//		this.rolesXCantidadFaltante = rolesXCantidadFaltante;
//		this.rolXArtistaCandidato = rolXArtistaCandidato;
//		this.listaDeArtistasPosiblesParaEntrenar = listaDeArtistasCandidatos;
//		this.listaDeArtistasPosiblesParaEntrenar = listaDeArtistasCandidatos.stream()
//				.filter(a -> !a.perteneceADiscografica() && !a.estaAsignadoAUnaCancion()).toList();
//	}

	public void cancelarTransaccion(Map<String, IntegranteDeUnRol> rolesXIntegrantesCandidatos,
			List<ArtistaBase> listaDeArtistasCandidatos) {
		transaccionCommitted = false;
		this.rolesXIntegrantesCandidatos = rolesXIntegrantesCandidatos;
		this.listaDeArtistasPosiblesParaEntrenar = listaDeArtistasCandidatos.stream()
				.filter(a -> !a.perteneceADiscografica() && !a.estaAsignadoAlmenosAUnaCancion()).toList();
	}
}
