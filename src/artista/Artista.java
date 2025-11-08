package artista;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import banda.BandaHistorico;
import cancion.Cancion;

public abstract class Artista {
	private String nombre;
	protected List<String> roles;
	private List<BandaHistorico> bandaHistorico;
	protected List<Cancion> cancionesEnLasQueEstaAsignado;
	protected double costo;

	public Artista(String nombre, List<String> rol, List<BandaHistorico> banda) {
		this.nombre = nombre;
		this.roles = rol;
		this.bandaHistorico = banda;
		this.costo = 0;
		cancionesEnLasQueEstaAsignado = new ArrayList<>();
	}

	public boolean estaAsignadoAUnaCancion() {
		return cancionesEnLasQueEstaAsignado.size() != 0;
	}

	public List<Cancion> getListaDeCancionesEnLasQueEstaAsignado() {
		return cancionesEnLasQueEstaAsignado;
	}

	public List<BandaHistorico> getListaDeBandas() {
		return bandaHistorico;
	}

	public double getCosto() {
		return costo;
	}

	public String getNombre() {
		return nombre;
	}

	public void entrenarNuevoRol(String nuevoRol) {
	}

	public abstract boolean tieneDescuento();

	public abstract boolean perteneceADiscografica();

	public abstract boolean puedeSerAsignadoACancion();

	public void asignar(Cancion cancion) {
		cancionesEnLasQueEstaAsignado.addLast(cancion);
	}

	public void designar(Cancion cancion) {
		cancionesEnLasQueEstaAsignado.remove(cancion);
	}

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

	public JsonObject toJson() {
		JsonObject artistaJSON = new JsonObject();
		JsonArray rolesJSON = new JsonArray(roles.size());
		JsonArray bandasJSON = new JsonArray(bandaHistorico.size());
		roles.forEach(rol -> rolesJSON.add(rol));
		bandaHistorico.forEach(banda -> bandasJSON.add(banda.getNombre()));
		;
		artistaJSON.addProperty("nombre", this.nombre);
		artistaJSON.add("roles", rolesJSON);
		artistaJSON.add("bandas", bandasJSON);
		return artistaJSON;
	}
}
