package artista;

import java.util.List;
import java.util.Objects;

import com.google.gson.JsonObject;

import cancion.Cancion;

public class ArtistaContratado extends ArtistaBase {
	protected int maxCanciones;

	public ArtistaContratado(String nombre, List<String> rol, List<BandaHistorico> banda, double costo,
			int maxCanciones) {
		super(nombre, rol, banda);
		this.costo = costo;
		this.maxCanciones = maxCanciones;
	}

//	probado
	@Override
	public boolean entrenarNuevoRol(String nuevoRol) {
		if (nuevoRol == null)
			throw new IllegalArgumentException("El rol no puede ser null.");
		if (super.roles.contains(nuevoRol))
			return false;
		super.roles.addLast(nuevoRol);
		this.costo *= 1.5;
		return true;
	}

//	probado
	@Override
	public boolean perteneceADiscografica() {
		return false;
	}

//	probado
	@Override
	public double getCosto() {
		return tieneDescuento() ? super.getCosto() * 0.5 : super.getCosto();
	}

//	probado
	@Override
	public boolean puedeSerAsignadoACancion() {
		return this.maxCanciones > 0;
	}

//	probado
	@Override
	public boolean asignar(Cancion cancion) {
		if (cancion == null)
			throw new IllegalArgumentException("No se puede asignar una cancion en null.");
		if (!this.puedeSerAsignadoACancion() || cancionesEnLasQueEstaAsignado.contains(cancion))
			return false;
		cancionesEnLasQueEstaAsignado.addLast(cancion);
		this.maxCanciones--;
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArtistaContratado other = (ArtistaContratado) obj;
		return Objects.equals(nombre, other.nombre);
	}

	// probado
	@Override
	public boolean designar(Cancion cancion) {
		if (cancion == null)
			throw new IllegalArgumentException("No se puede designar una cancion en null.");
		if (!cancionesEnLasQueEstaAsignado.contains(cancion))
			return false;
		cancionesEnLasQueEstaAsignado.remove(cancion);
		this.maxCanciones++;
		return true;
	}

	@Override
	public String toString() {
		String str = super.toString();
		str += "\tTiene descuento: " + (this.tieneDescuento() ? "Si" : "No") + "\n";
		str += "\t" + (this.tieneDescuento()
				? "El costo con descuento es: " + super.costo * 0.5 + "\n\tEl costo sin descuento es: " + super.costo
				: "El costo es: " + super.costo) + "\n";
		str += String.format("\tCantidad máxima de canciones en las que puede estar: %d \n",
				this.maxCanciones + super.cancionesEnLasQueEstaAsignado.size());
		return str;
	}

//	probado
	@Override
	public boolean tieneDescuento() {
		for (int i = 0; i < getListaDeBandas().size(); i++)
			if (getListaDeBandas().get(i).tieneArtistaDeDiscografica())
				return true;
		return false;
	}

//	probado
	@Override
	public JsonObject toJson() {
		JsonObject artistaJSON = super.toJson();
		artistaJSON.addProperty("costo", this.costo);
		artistaJSON.addProperty("maxCanciones", maxCanciones + this.cancionesEnLasQueEstaAsignado.size());
		return artistaJSON;
	}
}
