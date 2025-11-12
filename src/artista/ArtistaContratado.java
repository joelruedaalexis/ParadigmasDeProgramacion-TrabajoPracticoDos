package artista;

import java.util.List;
import java.util.Objects;

import com.google.gson.JsonObject;

import cancion.Cancion;

public class ArtistaContratado extends ArtistaBase {
	private int maxCanciones;

	public ArtistaContratado(String nombre, List<String> rol, List<BandaHistorico> banda, double costo,
			int maxCanciones) {
		super(nombre, rol, banda);
		this.costo = costo;
		this.maxCanciones = maxCanciones;
	}

	public void entrenarNuevoRol(String nuevoRol) {
		roles.add(nuevoRol);
		this.costo *= 1.5;
	}

	@Override
	public boolean perteneceADiscografica() {
		return false;
	}

	@Override
	public double getCosto() {
		return tieneDescuento() ? super.getCosto() * 0.5 : super.getCosto();
	}

	@Override
	public boolean puedeSerAsignadoACancion() {
		return this.maxCanciones > 0;
	}

//	public boolean asignar(Cancion cancion) {
//		if (!cancionesEnLasQueEstaAsignado.contains(cancion))
//			return false;
//		cancionesEnLasQueEstaAsignado.addLast(cancion);
//		return true;
//	}
//
//	public boolean designar(Cancion cancion) {
//		if (!cancionesEnLasQueEstaAsignado.contains(cancion))
//			return false;
//		cancionesEnLasQueEstaAsignado.remove(cancion);
//		return true;
//	}

	@Override
	public boolean asignar(Cancion cancion) {
		if (!this.puedeSerAsignadoACancion() && !cancionesEnLasQueEstaAsignado.contains(cancion))
			return false;
		super.asignar(cancion);
		this.maxCanciones--;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(maxCanciones);
		return result;
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
		return maxCanciones == other.maxCanciones;
	}

	@Override
	public boolean designar(Cancion cancion) {
		if (!cancionesEnLasQueEstaAsignado.contains(cancion))
			return false;
		super.designar(cancion);
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

	@Override
	public boolean tieneDescuento() {
		for (int i = 0; i < getListaDeBandas().size(); i++)
			if (getListaDeBandas().get(i).tieneArtistaDeDiscografica())
				return true;
		return false;
	}

	@Override
	public JsonObject toJson() {
		JsonObject artistaJSON = super.toJson();
		artistaJSON.addProperty("costo", this.costo);
		artistaJSON.addProperty("maxCanciones", maxCanciones + this.cancionesEnLasQueEstaAsignado.size());
		return artistaJSON;
	}
}
