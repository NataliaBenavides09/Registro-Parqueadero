import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

class Parqueadero {

    // 1. CONSTANTES DEL SISTEMA
    static final int CAPACIDAD_TOTAL = 10;
    static final int TARIFA_HORA_CARRO = 4000;
    static final int TARIFA_DIA_CARRO = 30000;
    static final int TARIFA_HORA_MOTO = 2000;
    static final int TARIFA_DIA_MOTO = 15000;
    static final int UMBRAL_HORAS_DIA = 8;
    static final int HORA_APERTURA = 8;
    static final int HORA_CIERRE = 22;

    static final Scanner SCANNER = new Scanner(System.in);

    static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm");

    // 2. ARREGLOS Y VARIABLES
    static String[] tipoVehiculo = new String[CAPACIDAD_TOTAL];
    static LocalDateTime[] fechaIngreso =
            new LocalDateTime[CAPACIDAD_TOTAL];

    static int recaudoCarros = 0;
    static int recaudoMotos = 0;

    public static void main(String[] args) {

        int opcion;

        do {
            System.out.println("\n===== PARQUEADERO =====");
            System.out.println("1. Registrar entrada");
            System.out.println("2. Registrar salida");
            System.out.println("3. Reporte de ocupacion");
            System.out.println("4. Reporte financiero");
            System.out.println("5. Salir");

            opcion = leerEntero("Seleccione una opcion: ", 1, 5);

            switch (opcion) {
                case 1:
                    registrarEntrada();
                    break;

                case 2:
                    registrarSalida();
                    break;

                case 3:
                    reporteOcupacion();
                    break;

                case 4:
                    reporteFinanciero();
                    break;

                case 5:
                    System.out.println("Programa finalizado.");
                    break;
            }

        } while (opcion != 5);

        SCANNER.close();
    }

    // 3. VALIDAR DATOS NUMERICOS
    static int leerEntero(String mensaje, int minimo, int maximo) {
        int numero;

        while (true) {
            System.out.print(mensaje);
            String texto = SCANNER.nextLine().trim();

            try {
                numero = Integer.parseInt(texto);

                if (numero >= minimo && numero <= maximo) {
                    return numero;
                }

            } catch (NumberFormatException e) {
                // El dato ingresado no es un numero entero.
            }

            System.out.println("Dato invalido. Intente nuevamente.");
        }
    }
    // 4. BUSCAR UNA CELDA LIBRE
    static int celdaDisponible() {
        for (int i = 0; i < CAPACIDAD_TOTAL; i++) {
            if (tipoVehiculo[i] == null) {
                return i;
            }
        }

        return -1;
    }

    // 5. REGISTRAR LA ENTRADA
    static void registrarEntrada() {

        int celda = celdaDisponible();

        if (celda == -1) {
            System.out.println("El parqueadero esta lleno.");
            return;
        }

        System.out.println("\n1. Carro");
        System.out.println("2. Moto");

        int tipo = leerEntero("Tipo de vehiculo: ", 1, 2);

        if (tipo == 1) {
            tipoVehiculo[celda] = "Carro";
        } else {
            tipoVehiculo[celda] = "Moto";
        }

        LocalDateTime ingreso = leerFechaHora("Fecha y hora de ingreso (yyyy-MM-dd HH:mm): ");

        fechaIngreso[celda] = ingreso;

        System.out.println("Entrada registrada correctamente.");
        System.out.println("Celda asignada: " + (celda + 1));
        System.out.println("Vehiculo: " + tipoVehiculo[celda]);
        System.out.println("Ingreso: " + ingreso.format(FORMATO_FECHA_HORA));
    }

    // 6. LEER Y VALIDAR FECHA Y HORA
    static LocalDateTime leerFechaHora(String mensaje) {

        while (true) {
            System.out.print(mensaje);
            String texto = SCANNER.nextLine().trim();

            try {
                LocalDateTime fecha =
                        LocalDateTime.parse(texto, FORMATO_FECHA_HORA);

                if (!fecha.format(FORMATO_FECHA_HORA).equals(texto)) {
                    System.out.println("La fecha no existe en el calendario.");
                    continue;
                }

                int hora = fecha.getHour();
                int minuto = fecha.getMinute();

                if (hora < HORA_APERTURA || hora > HORA_CIERRE
                        || (hora == HORA_CIERRE && minuto > 0)) {
                    System.out.println("Horario permitido: 08:00 a 22:00.");
                    continue;
                }

                return fecha;

            } catch (DateTimeParseException e) {
                System.out.println("Fecha invalida. Use yyyy-MM-dd HH:mm.");
            }
        }
    }

    // 7. REGISTRAR LA SALIDA
    static void registrarSalida() {

        int celda = leerEntero(
                "Numero de celda (1-10): ", 1, CAPACIDAD_TOTAL
        ) - 1;

        if (tipoVehiculo[celda] == null) {
            System.out.println("La celda esta vacia.");
            return;
        }

        LocalDateTime ingreso = fechaIngreso[celda];
        LocalDateTime salida;

        while (true) {
            salida = leerFechaHora(
                    "Fecha y hora de salida (yyyy-MM-dd HH:mm): "
            );

            if (!salida.isBefore(ingreso)) {
                break;
            }

            System.out.println("La salida no puede ser anterior al ingreso.");
        }

        liquidacion(celda, salida);
    }
    // 8. LIQUIDAR LA ESTADIA Y MOSTRAR LA FACTURA
    static void liquidacion(int celda, LocalDateTime salida) {

        String tipo = tipoVehiculo[celda];
        LocalDateTime ingreso = fechaIngreso[celda];

        long dias = ChronoUnit.DAYS.between(ingreso, salida);

        LocalDateTime cursor = ingreso.plusDays(dias);

        long minutosRestantes = Duration.between(cursor, salida).toMinutes();

        long horas = (minutosRestantes + 59) / 60;

        if (horas >= UMBRAL_HORAS_DIA) {
            dias++;
            horas = 0;
        }

        int tarifaHora;
        int tarifaDia;

        if (tipo.equals("Carro")) {
            tarifaHora = TARIFA_HORA_CARRO;
            tarifaDia = TARIFA_DIA_CARRO;
        } else {
            tarifaHora = TARIFA_HORA_MOTO;
            tarifaDia = TARIFA_DIA_MOTO;
        }

        long valorDias = dias * tarifaDia;
        long valorHoras = horas * tarifaHora;
        long total = valorDias + valorHoras;

        System.out.println("\n===== FACTURA =====");
        System.out.println("Celda: " + (celda + 1));
        System.out.println("Vehiculo: " + tipo);
        System.out.println("Ingreso: " + ingreso.format(FORMATO_FECHA_HORA));
        System.out.println("Salida: " + salida.format(FORMATO_FECHA_HORA));
        System.out.println("Dias cobrados: " + dias);
        System.out.println("Horas cobradas: " + horas);
        System.out.println("Valor por dias: $" + valorDias);
        System.out.println("Valor por horas: $" + valorHoras);
        System.out.println("TOTAL A PAGAR: $" + total);

        if (tipo.equals("Carro")) {
            recaudoCarros += total;
        } else {
            recaudoMotos += total;
        }

        // Liberar la celda despues del cobro.
        tipoVehiculo[celda] = null;
        fechaIngreso[celda] = null;

        System.out.println("Celda liberada correctamente.");
    }

    // 9. REPORTE DE OCUPACION
    static void reporteOcupacion() {

        int ocupadas = 0;

        for (int i = 0; i < CAPACIDAD_TOTAL; i++) {
            if (tipoVehiculo[i] != null) {
                ocupadas++;
            }
        }

        int libres = CAPACIDAD_TOTAL - ocupadas;

        System.out.println("\n===== REPORTE DE OCUPACION =====");
        System.out.println("Celdas ocupadas: " + ocupadas);
        System.out.println("Celdas libres: " + libres);
    }

    // 10. REPORTE FINANCIERO
    static void reporteFinanciero() {

        System.out.println("\n===== REPORTE FINANCIERO =====");
        System.out.println("Recaudo de carros: $" + recaudoCarros);
        System.out.println("Recaudo de motos: $" + recaudoMotos);
        System.out.println("Recaudo total: $" + (recaudoCarros + recaudoMotos));
    }
}




