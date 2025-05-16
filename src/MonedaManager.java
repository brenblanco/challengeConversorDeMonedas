import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class MonedaManager {
    private final Map<String, Moneda> monedasDisponibles = new LinkedHashMap<>();
    private static final String API_KEY = "bafd9babac62ff49eacc189c";
    private static final String ARCHIVO_MONEDAS = "monedas.json";

    public MonedaManager() {
        monedasDisponibles.put("ARS", new Moneda("ARS", "Peso argentino", "Argentina"));
        monedasDisponibles.put("BOB", new Moneda("BOB", "Boliviano boliviano", "Bolivia"));
        monedasDisponibles.put("BRL", new Moneda("BRL", "Real brasileño", "Brasil"));
        monedasDisponibles.put("CLP", new Moneda("CLP", "Peso chileno", "Chile"));
        monedasDisponibles.put("COP", new Moneda("COP", "Peso colombiano", "Colombia"));
        monedasDisponibles.put("USD", new Moneda("USD", "Dólar estadounidense", "Estados Unidos"));
    }

    public void mostrarMonedas() {
        System.out.println("\nMonedas disponibles:");
        monedasDisponibles.forEach((codigo, moneda) ->
                System.out.println(codigo + ": " + moneda.currencyName() + " - " + moneda.Country()));
    }

    public boolean existeMoneda(String codigo) {
        return monedasDisponibles.containsKey(codigo);
    }

    public void agregarMoneda(Scanner scanner) {
        String codigo;
        while (true) {
            System.out.print("Ingrese el código de la nueva moneda (ej. EUR): ");
            codigo = scanner.nextLine().toUpperCase();
            if (!codigo.matches("^[A-Z]{3}$")) {
                System.out.println("Código inválido. Debe tener 3 letras (ej. EUR).");
                continue;
            }
            if (!verificarCodigoMonedaValido(codigo)) {
                System.out.println("El código de moneda ingresado no es válido.");
                continue;
            }
            if (existeMoneda(codigo)) {
                System.out.println("La moneda ya existe en el listado.");
                continue;
            }
            break;
        }

        String nombre;
        while (true) {
            System.out.print("Ingrese el nombre de la moneda: ");
            nombre = scanner.nextLine();
            if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+$")) {
                System.out.println("Nombre inválido. Solo se permiten letras y espacios.");
                continue;
            }
            break;
        }

        String pais;
        while (true) {
            System.out.print("Ingrese el país de la moneda: ");
            pais = scanner.nextLine();
            if (!pais.matches("^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+$")) {
                System.out.println("País inválido. Solo se permiten letras y espacios.");
                continue;
            }
            break;
        }

        monedasDisponibles.put(codigo, new Moneda(codigo, nombre, pais));
        System.out.println("Moneda agregada correctamente.");
        guardarEnArchivo();
    }

    private boolean verificarCodigoMonedaValido(String codigo) {
        try {
            URL url = new URL("https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/USD");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.out.println("Error en la conexión con la API.");
                return false;
            }

            JsonObject json = JsonParser.parseReader(new InputStreamReader(conn.getInputStream())).getAsJsonObject();

            if (!json.has("conversion_rates") || json.get("conversion_rates").isJsonNull()) {
                System.out.println("Error: la respuesta no contiene tasas de conversión válidas.");
                return false;
            }

            JsonObject rates = json.getAsJsonObject("conversion_rates");
            return rates.has(codigo.toUpperCase());

        } catch (Exception e) {
            System.out.println("Error al verificar el código de moneda: " + e.getMessage());
            return false;
        }
    }

    public void eliminarMoneda(Scanner scanner) {
        System.out.print("Ingrese el código de la moneda a eliminar: ");
        String codigo = scanner.nextLine().toUpperCase();
        if (monedasDisponibles.remove(codigo) != null) {
            System.out.println("Moneda eliminada correctamente.");
            guardarEnArchivo();
        } else {
            System.out.println("No existe la moneda ingresada.");
        }
    }

    public void cargarDesdeArchivo() {
        File archivo = new File(ARCHIVO_MONEDAS);
        try {
            if (!archivo.exists()) {
                try (FileWriter writer = new FileWriter(archivo)) {
                    writer.write("{}");
                    System.out.println("Archivo monedas.json creado vacío.");
                }
            }

            try (FileReader reader = new FileReader(archivo)) {
                Type type = new TypeToken<Map<String, Moneda>>() {}.getType();
                Map<String, Moneda> monedasDesdeArchivo = new com.google.gson.Gson().fromJson(reader, type);
                if (monedasDesdeArchivo != null) {
                    monedasDisponibles.putAll(monedasDesdeArchivo);
                }
                System.out.println("Monedas cargadas desde archivo.");
            }
        } catch (Exception e) {
            System.out.println("No se pudo cargar monedas desde archivo: " + e.getMessage());
        }
    }

    private void guardarEnArchivo() {
        try (FileWriter writer = new FileWriter(ARCHIVO_MONEDAS)) {
            Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
            String json = gsonPretty.toJson(monedasDisponibles);
            writer.write(json);
            //System.out.println("Archivo monedas.json guardado correctamente.");
        } catch (Exception e) {
            System.out.println("Error al guardar monedas en archivo: " + e.getMessage());
        }
    }
}
