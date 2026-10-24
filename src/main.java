import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

//Declaración de variables//
public class main {
    static int CAPACIDAD_TOTAL = 10;
    static int TARIFA_HORA_CARRO = 4000;
    static int TARIFA_DIA_CARRO = 30000;
    static int TARIFA_HORA_MOTO = 2000;
    static int TARIFA_DIA_MOTO = 15000;
    static int UMBRAL_HORAS_DIA = 8;
    static int HORA_APERTURA = 8;
    static int HORA_CIERRE = 22;

    //Arreglos//
    static Scanner scanner = new Scanner(System.in);
    static DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    static String[] tipoVehiculo=new String[CAPACIDAD_TOTAL];
    static LocalDateTime[] fechaIngreso=new LocalDateTime[CAPACIDAD_TOTAL];

    static int recaudoCarros=0;
    static int recaudoMotos=0;

    public static void main(String[] args) {
        int opcion;
        do {
            System.out.println("Bienvenido al parqueadero");
            System.out.println("1. Registrar entrada");
            System.out.println("2. Registrar salida");
            System.out.println("3. Reporte de ocupación");
            System.out.println("4. Reporte financiero");
            System.out.println("4. Salir");

            opcion = leerEntero("Seleccione una opción", 1, 5);
                    switch(opcion) {
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
                        default:
                            System.out.println("Operación finalizada");
                            break;
                    }
        } while (opcion != 5);
    scanner.close();
    }}

//




