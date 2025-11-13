package artista;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cancion.Cancion;

public class ArtistaBase {
	private String nombre;
	protected List<String> roles;
	private List<BandaHistorico> bandaHistorico;
	protected List<Cancion> cancionesEnLasQueEstaAsignado;
	protected double costo;

	public ArtistaBase(String nombre, List<String> rol, List<BandaHistorico> banda) {
		this.nombre = nombre;
		this.roles = rol;
		this.bandaHistorico = banda;
		this.costo = 0;
		cancionesEnLasQueEstaAsignado = new ArrayList<>();
		banda.forEach(b -> b.agregarIntegrante(this));
	}

	@Override
	public int hashCode() {
		return Objects.hash(bandaHistorico, cancionesEnLasQueEstaAsignado, costo, nombre, roles);
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
		return Objects.equals(bandaHistorico, other.bandaHistorico)
				&& Objects.equals(cancionesEnLasQueEstaAsignado, other.cancionesEnLasQueEstaAsignado)
				&& Double.doubleToLongBits(costo) == Double.doubleToLongBits(other.costo)
				&& Objects.equals(nombre, other.nombre) && Objects.equals(roles, other.roles);
	}

//	probado
	public boolean tieneRol(String rolAConsultar) {
		return roles.contains(rolAConsultar);
	}

	public boolean estaAsignadoAUnaCancion() {
		return cancionesEnLasQueEstaAsignado.size() != 0;
	}

//	probado
	public List<Cancion> getListaDeCancionesEnLasQueEstaAsignado() {
		return cancionesEnLasQueEstaAsignado;
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
		cancionesEnLasQueEstaAsignado.addLast(cancion);
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
