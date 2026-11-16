package org.iesalandalus.programacion.tallermecanico.modelo.negocio.ficheros.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Vehiculo;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.IVehiculos;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Vehiculos implements IVehiculos {

    private static final String FICHERO_VEHICULOS = String.format("%s%s%s%s%s%s%s", "datos", File.separator, "ficheros", File.separator, "json", File.separator, "vehiculos.json");
    private static final TypeReference<List<Vehiculo>> TYPE_LIST_VEHICULO = new TypeReference<>() {
    };

    private final ObjectMapper mapper;
    private static Vehiculos instancia;

    private Vehiculos() {
        mapper = new ObjectMapper();
    }

    static Vehiculos getInstancia() {
        if (instancia == null) {
            instancia = new Vehiculos();
        }
        return instancia;
    }

    @Override
    public void comenzar() {
        // No hace nada: los datos se leen del fichero JSON cada vez que el usuario los solicita.
    }

    @Override
    public void terminar() {
        // No hace nada: los datos se escriben en el fichero JSON cada vez que se modifican.
    }

    private List<Vehiculo> leer() throws TallerMecanicoExcepcion {
        File fichero = new File(FICHERO_VEHICULOS);
        List<Vehiculo> vehiculos = new ArrayList<>();
        if (fichero.exists() && fichero.length() > 0) {
            try {
                vehiculos = mapper.readValue(fichero, TYPE_LIST_VEHICULO);
            } catch (IOException e) {
                throw new TallerMecanicoExcepcion(String.format("No se ha podido leer el fichero %s. Compruebe que su contenido es correcto.", FICHERO_VEHICULOS));
            }
        }
        return vehiculos;
    }

    private void escribir(List<Vehiculo> vehiculos) throws TallerMecanicoExcepcion {
        File fichero = new File(FICHERO_VEHICULOS);
        try {
            // Si la carpeta del fichero no existe, se crea para poder escribir en él.
            fichero.getParentFile().mkdirs();
            mapper.writerWithDefaultPrettyPrinter().writeValue(fichero, vehiculos);
        } catch (IOException e) {
            throw new TallerMecanicoExcepcion(String.format("No se ha podido escribir en el fichero %s.", FICHERO_VEHICULOS));
        }
    }

    @Override
    public List<Vehiculo> get() {
        List<Vehiculo> vehiculos = new ArrayList<>();
        try {
            vehiculos = leer();
        } catch (TallerMecanicoExcepcion e) {
            System.out.println(e.getMessage());
        }
        return vehiculos;
    }

    @Override
    public void insertar(Vehiculo vehiculo) throws TallerMecanicoExcepcion {
        Objects.requireNonNull(vehiculo, "No se puede insertar un vehículo nulo.");
        List<Vehiculo> vehiculos = leer();
        if (vehiculos.contains(vehiculo)) {
            throw new TallerMecanicoExcepcion("Ya existe un vehículo con esa matrícula.");
        }
        vehiculos.add(vehiculo);
        escribir(vehiculos);
    }

    @Override
    public Vehiculo buscar(Vehiculo vehiculo) {
        Objects.requireNonNull(vehiculo, "No se puede buscar un vehículo nulo.");
        List<Vehiculo> vehiculos = get();
        int indice = vehiculos.indexOf(vehiculo);
        return (indice == -1) ? null : vehiculos.get(indice);
    }

    @Override
    public void borrar(Vehiculo vehiculo) throws TallerMecanicoExcepcion {
        Objects.requireNonNull(vehiculo, "No se puede borrar un vehículo nulo.");
        List<Vehiculo> vehiculos = leer();
        if (!vehiculos.contains(vehiculo)) {
            throw new TallerMecanicoExcepcion("No existe ningún vehículo con esa matrícula.");
        }
        vehiculos.remove(vehiculo);
        escribir(vehiculos);
    }
}
