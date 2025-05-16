import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ConversorMoneda {
    private static final String API_KEY = "6982d8a391df6933b4ce8387";
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    public void realizarConversion(Scanner scanner, Historial historial, MonedaManager monedaManager) {
        String origen;
        while (true) {
            System.out.print("Ingrese la moneda de origen: ");
            origen = scanner.nextLine().toUpperCase();
            if (!monedaManager.existeMoneda(origen)) {
                System.out.println("Moneda de origen inválida, intente nuevamente.");
            } else {
                break;
            }
        }

        String destino;
        while (true) {
            System.out.print("Ingrese la moneda de destino: ");
            destino = scanner.nextLine().toUpperCase();
            if (!monedaManager.existeMoneda(destino)) {
                System.out.println("Moneda de destino inválida, intente nuevamente.");
            } else if (destino.equals(origen)) {
                System.out.println("La moneda de destino no puede ser igual a la de origen, intente otra.");
            } else {
                break;
            }
        }

        double cantidad;
        while (true) {
            System.out.print("Ingrese la cantidad a convertir: ");
            String entrada = scanner.nextLine();
            try {
                cantidad = Double.parseDouble(entrada);
                if (cantidad <= 0) {
                    System.out.println("Ingrese una cantidad positiva.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Cantidad inválida, intente nuevamente.");
            }
        }

        try {
            URL url = new URL(API_URL + origen);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            JsonObject json = JsonParser.parseReader(new InputStreamReader(conn.getInputStream())).getAsJsonObject();
            double tasa = json.getAsJsonObject("conversion_rates").get(destino).getAsDouble();
            double resultado = cantidad * tasa;

            System.out.printf("\nUsted ha convertido %.2f %s en %.2f %s\n", cantidad, origen, resultado, destino);
            historial.agregar(origen, destino, cantidad, resultado);
        } catch (Exception e) {
            System.out.println("Error al conectar con la API: " + e.getMessage());
        }
    }
}