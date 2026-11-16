package org.iesalandalus.programacion.tallermecanico.modelo.negocio.ficheros.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.iesalandalus.programacion.tallermecanico.modelo.TallerMecanicoExcepcion;
import org.iesalandalus.programacion.tallermecanico.modelo.dominio.Cliente;
import org.iesalandalus.programacion.tallermecanico.modelo.negocio.IClientes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Clientes implements IClientes {

    private static final String FICHERO_CLIENTES = String.format("%s%s%s%s%s%s%s", "datos", File.separator, "ficheros", File.separator, "json", File.separator, "clientes.json");
    private static final TypeReference<List<Cliente>> TYPE_LIST_CLIENTE = new TypeReference<>() {
    };

    private final ObjectMapper mapper;
    private static Clientes instancia;

    private Clientes() {
        mapper = new ObjectMapper();
    }

    static Clientes getInstancia() {
        if (instancia == null) {
            instancia = new Clientes();
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

    private List<Cliente> leer() throws TallerMecanicoExcepcion {
        File fichero = new File(FICHERO_CLIENTES);
        List<Cliente> clientes = new ArrayList<>();
        if (fichero.exists() && fichero.length() > 0) {
            try {
                clientes = mapper.readValue(fichero, TYPE_LIST_CLIENTE);
            } catch (IOException e) {
                throw new TallerMecanicoExcepcion(String.format("No se ha podido leer el fichero %s. Compruebe que su contenido es correcto.", FICHERO_CLIENTES));
            }
        }
        return clientes;
    }

    private void escribir(List<Cliente> clientes) throws TallerMecanicoExcepcion {
        File fichero = new File(FICHERO_CLIENTES);
        try {
            // Si la carpeta del fichero no existe, se crea para poder escribir en él.
            fichero.getParentFile().mkdirs();
            mapper.writerWithDefaultPrettyPrinter().writeValue(fichero, clientes);
        } catch (IOException e) {
            throw new TallerMecanicoExcepcion(String.format("No se ha podido escribir en el fichero %s.", FICHERO_CLIENTES));
        }
    }

    @Override
    public List<Cliente> get() {
        List<Cliente> clientes = new ArrayList<>();
        try {
            clientes = leer();
        } catch (TallerMecanicoExcepcion e) {
            System.out.println(e.getMessage());
        }
        return clientes;
    }

    @Override
    public void insertar(Cliente cliente) throws TallerMecanicoExcepcion {
        Objects.requireNonNull(cliente, "No se puede insertar un cliente nulo.");
        List<Cliente> clientes = leer();
        if (clientes.contains(cliente)) {
            throw new TallerMecanicoExcepcion("Ya existe un cliente con ese DNI.");
        }
        clientes.add(cliente);
        escribir(clientes);
    }

    @Override
    public Cliente modificar(Cliente cliente, String nombre, String telefono) throws TallerMecanicoExcepcion {
        Objects.requireNonNull(cliente, "No se puede modificar un cliente nulo.");
        List<Cliente> clientes = leer();
        int indice = clientes.indexOf(cliente);
        if (indice == -1) {
            throw new TallerMecanicoExcepcion("No existe ningún cliente con ese DNI.");
        }
        Cliente clienteEncontrado = clientes.get(indice);
        if (nombre != null && !nombre.isBlank()) {
            clienteEncontrado.setNombre(nombre);
        }
        if (telefono != null && !telefono.isBlank()) {
            clienteEncontrado.setTelefono(telefono);
        }
        escribir(clientes);
        return clienteEncontrado;
    }

    @Override
    public Cliente buscar(Cliente cliente) {
        Objects.requireNonNull(cliente, "No se puede buscar un cliente nulo.");
        List<Cliente> clientes = get();
        int indice = clientes.indexOf(cliente);
        return (indice == -1) ? null : clientes.get(indice);
    }

    @Override
    public void borrar(Cliente cliente) throws TallerMecanicoExcepcion {
        Objects.requireNonNull(cliente, "No se puede borrar un cliente nulo.");
        List<Cliente> clientes = leer();
        if (!clientes.contains(cliente)) {
            throw new TallerMecanicoExcepcion("No existe ningún cliente con ese DNI.");
        }
        clientes.remove(cliente);
        escribir(clientes);
    }

}
