import java.util.Scanner;

public class Menu {
    private final Scanner scanner = new Scanner(System.in);
    private final ConversorMoneda conversor = new ConversorMoneda();
    private final MonedaManager monedaManager = new MonedaManager();
    private final Historial historial = new Historial();

    public void iniciar() {
        monedaManager.cargarDesdeArchivo();
        historial.cargarDesdeArchivo();

        while (true) {
            System.out.println("\n===Bienvenido al Conversor de Monedas ===");
            System.out.println("1. Hacer conversión de moneda");
            System.out.println("2. Ver listado actual de monedas");
            System.out.println("3. Agregar una moneda al listado");
            System.out.println("4. Eliminar una moneda al listado");
            System.out.println("5. Ver historial de conversiones");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1" -> conversor.realizarConversion(scanner, historial, monedaManager);
                case "2" -> monedaManager.mostrarMonedas();
                case "3" -> monedaManager.agregarMoneda(scanner);
                case "4" -> monedaManager.eliminarMoneda(scanner);
                case "5" -> historial.mostrar();
                case "6" -> {
                    System.out.println("Gracias por usar el conversor de monedas.");
                    return;
                }
                default -> System.out.println("Opción no válida. Intente nuevamente.");
            }
        }
    }
}
