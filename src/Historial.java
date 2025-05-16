import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Historial {
    private final List<Map<String, Object>> registros = new ArrayList<>();
    private static final String ARCHIVO = "historial_conversiones.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void agregar(String origen, String destino, double cantidad, double resultado) {
        Map<String, Object> conversion = Map.of(
                "origen", origen,
                "destino", destino,
                "cantidad", cantidad,
                "resultado", resultado
        );
        registros.add(conversion);
        guardarEnArchivo();
    }

    public void mostrar() {
        if (registros.isEmpty()) {
            System.out.println("No hay conversiones registradas.");
        } else {
            System.out.println("Historial de conversiones:");
            for (Map<String, Object> reg : registros) {
                System.out.printf("%.2f %s = %.2f %s%n",
                        reg.get("cantidad"),
                        reg.get("origen"),
                        reg.get("resultado"),
                        reg.get("destino"));
            }
        }
    }

    private void guardarEnArchivo() {
        try (Writer writer = new FileWriter(ARCHIVO)) {
            gson.toJson(registros, writer);
        } catch (IOException e) {
            System.out.println("No se pudo guardar en el archivo: " + e.getMessage());
        }
    }

    public void cargarDesdeArchivo() {
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) return;

        try (Reader reader = new FileReader(archivo)) {
            Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
            List<Map<String, Object>> cargados = gson.fromJson(reader, listType);
            if (cargados != null) registros.addAll(cargados);
        } catch (IOException e) {
            System.out.println("No se pudo leer el historial: " + e.getMessage());
        }
    }
}
