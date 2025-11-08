package artista;

import java.util.List;

import com.google.gson.JsonObject;

import banda.BandaHistorico;

public class ArtistaBase extends Artista {
	public ArtistaBase(String nombre, List<String> rol, List<BandaHistorico> banda) {
		super(nombre, rol, banda);
	}

	@Override
	public boolean perteneceADiscografica() {
		return true;
	}

	@Override
	public boolean puedeSerAsignadoACancion() {
		return true;
	}

	@Override
	public boolean tieneDescuento() {
		return false;
	}

	@Override
	public JsonObject toJson() {
		System.out.println("XDDDD");
		JsonObject artistaJSON = super.toJson();
		artistaJSON.addProperty("costo", 0);
		artistaJSON.addProperty("maxCanciones", 100);
		return artistaJSON;
	}

}
