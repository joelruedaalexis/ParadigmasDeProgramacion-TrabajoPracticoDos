package artista;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cancion.Cancion;

public class ArtistaBase {
	protected String nombre;
	protected List<String> roles;
	private List<BandaHistorico> bandaHistorico;
	protected Set<Cancion> cancionesEnLasQueEstaAsignado;
	protected double costo;

	public ArtistaBase(String nombre, List<String> rol, List<BandaHistorico> banda) {
		this.nombre = nombre;
		this.roles = rol;
		this.bandaHistorico = banda;
		this.costo = 0;
		cancionesEnLasQueEstaAsignado = new HashSet<>();
		banda.forEach(b -> b.agregarIntegrante(this));
	}

	@Override
	public int hashCode() {
		return Objects.hash(nombre);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArtistaBase other = (ArtistaBase) obj;
		return Objects.equals(nombre, other.nombre);
	}

	// probado
	public boolean tieneRol(String rolAConsultar) {
		return roles.contains(rolAConsultar);
	}

	public boolean estaAsignadoAlmenosAUnaCancion() {
		return !cancionesEnLasQueEstaAsignado.isEmpty();
	}

//	probado
	public List<Cancion> getListaDeCancionesEnLasQueEstaAsignado() {
		return new ArrayList<>(cancionesEnLasQueEstaAsignado);
	}

	public boolean estaAsignadoACancion(Cancion cancion) {
		return this.cancionesEnLasQueEstaAsignado.contains(cancion);
	}

//	probado
	public List<BandaHistorico> getListaDeBandas() {
		return bandaHistorico;
	}

//	probado
	public double getCosto() {
		return costo;
	}

//	probado
	public String getNombre() {
		return nombre;
	}

//	sin probar!!!
	public boolean entrenarNuevoRol(String nuevoRol) {// ¿Exception?
		return false;
	}

//  probado
	public boolean asignar(Cancion cancion) {
		if (cancion == null)
			throw new IllegalArgumentException("La canción no puede ser null.");
		if (cancionesEnLasQueEstaAsignado.contains(cancion))
			return false;
		cancionesEnLasQueEstaAsignado.add(cancion);
		return true;
	}

//	probado
	public boolean designar(Cancion cancion) {
		if (cancion == null)
			throw new IllegalArgumentException("La canción no puede ser null.");
		if (!cancionesEnLasQueEstaAsignado.contains(cancion))
			return false;
		cancionesEnLasQueEstaAsignado.remove(cancion);
		return true;
	}

//	probado
	public List<String> getRoles() {
		return roles;
	}

	@Override
	public String toString() {
		String str = "->Nombre: " + this.nombre + "\n";
		str += "\tRoles: " + this.roles + "\n";
		str += "\tHistórico de bandas: " + bandaHistorico.stream().map(b -> b.getNombre()).toList() + "\n";
		str += "\tPertenece a discografica: " + (perteneceADiscografica() ? "Si" : "No") + "\n";
		return str;
	}

//	probado
	public JsonObject toJson() {
		JsonObject artistaJSON = new JsonObject();
		JsonArray rolesJSON = new JsonArray(roles.size());
		JsonArray bandasJSON = new JsonArray(bandaHistorico.size());
		roles.forEach(rol -> rolesJSON.add(rol));
		bandaHistorico.forEach(banda -> bandasJSON.add(banda.getNombre()));
		artistaJSON.addProperty("nombre", this.nombre);
		artistaJSON.add("roles", rolesJSON);
		artistaJSON.add("bandas", bandasJSON);
		return artistaJSON;
	}

//	probado
	public boolean perteneceADiscografica() {
		return true;
	}

//	probado
	public boolean puedeSerAsignadoACancion() {
		return true;
	}

//	probado
	public boolean tieneDescuento() {
		return false;
	}
}
