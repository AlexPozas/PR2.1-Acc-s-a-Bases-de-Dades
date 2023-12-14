package com.project;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws SQLException {
        String basePath = System.getProperty("user.dir") + "/data/";
        String filePath = basePath + "forHonor.db";

        // Si no hay un archivo creado, créalo y agrega datos
        File fDatabase = new File(filePath);
        if (!fDatabase.exists()) {
            initDatabase(filePath);
        }

        // Conectar (crea la base de datos si no existe)
        Connection conn = UtilsSQLite.connect(filePath);

        // Menú principal
        while (true) {
            mostrarMenuPrincipal();
            Scanner scanner = new Scanner(System.in);
            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    mostrarTaula(conn);
                    break;
                case 2:
                    mostrarPersonatgesPerFaccio(conn);
                    break;
                case 3:
                    mostrarMillorAtacantPerFaccio(conn);
                    break;
                case 4:
                    mostrarMillorDefensorPerFaccio(conn);
                    break;
                case 5:
                    // Salir
                    UtilsSQLite.disconnect(conn);
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opción no válida");
            }
        }
    }

    static void mostrarMenuPrincipal() {
        System.out.println("----- Menú Principal -----");
        System.out.println("1. Mostrar una taula");
        System.out.println("2. Mostrar personatges per facció");
        System.out.println("3. Mostrar el millor atacant per facció");
        System.out.println("4. Mostrar el millor defensor per facció");
        System.out.println("5. Sortir");
        System.out.print("Selecciona una opció: ");
    }

    static void mostrarTaula(Connection conn) throws SQLException {
        System.out.println("Selecciona una taula a mostrar:");
        System.out.println("1. Faccio");
        System.out.println("2. Personatge");

        Scanner scanner = new Scanner(System.in);
        int opcion = scanner.nextInt();

        switch (opcion) {
            case 1:
                mostrarTaula(conn, "Faccio");
                break;
            case 2:
                mostrarTaula(conn, "Personatge");
                break;
            default:
                System.out.println("Opció no vàlida");
        }
    }

    static void mostrarTaula(Connection conn, String tableName) {
        ResultSet rs = null;

        try {
            rs = UtilsSQLite.querySelect(conn, "SELECT * FROM " + tableName + ";");
            ResultSetMetaData rsmd = rs.getMetaData();

            // Imprimir información de la tabla
            System.out.println("Informació de la taula " + tableName + ":");
            for (int cnt = 1; cnt <= rsmd.getColumnCount(); cnt++) {
                String label = rsmd.getColumnLabel(cnt);
                String name = rsmd.getColumnName(cnt);
                int type = rsmd.getColumnType(cnt);
                System.out.println("    " + label + ", " + name + ", " + type);
            }
            System.out.println();

            // Moverse al primer registro antes de acceder a los datos
            if (rs.next()) {
                // Imprimir contenido de la tabla
                System.out.println("Contingut de la taula " + tableName + ":");
                if (tableName.equals("Personatge")) {
                    do {
                    System.out.println("    " + rs.getInt("id") + ", " + rs.getString("nom")+ ", " + rs.getString("atac")
                    + ", " + rs.getString("defensa")+ ", " + rs.getString("idFaccio"));
                } while (rs.next());
                    
                }else{do {
                    System.out.println("    " + rs.getInt("id") + ", " + rs.getString("nom"));
                } while (rs.next());}
                System.out.println();
            } else {
                System.out.println("La taula " + tableName + " està buida.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cerrar el ResultSet después de usarlo
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static void mostrarPersonatgesPerFaccio(Connection conn) throws SQLException {
        // Mostrar la lista de facciones disponibles
    ArrayList<String> faccions = obtenerNombresFaccions(conn);
    System.out.println("Faccions disponibles:");
    for (int i = 0; i < faccions.size(); i++) {
        System.out.println((i + 1) + ". " + faccions.get(i));
    }

    // Solicitar al usuario que elija una facción
    Scanner scanner = new Scanner(System.in);
    System.out.print("Selecciona el número de la facció: ");
    int faccioIndex = scanner.nextInt();

    // Obtener el nombre de la facción seleccionada
    String faccioNom = faccions.get(faccioIndex - 1);

    // Ejecutar la consulta SQL para obtener los personajes de la facción seleccionada
    String query = "SELECT * FROM Personatge WHERE idFaccio IN (SELECT id FROM Faccio WHERE nom = ?);";
    ResultSet rs = null;

    try {
        rs = UtilsSQLite.querySelect(conn, query, faccioNom);

        // Mostrar los personajes de la facción
        System.out.println("Personatges de la facció '" + faccioNom + "':");
        while (rs.next()) {
            System.out.println("    " + rs.getInt("id") + ", " + rs.getString("nom"));
        }
        System.out.println();
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        // Cerrar el ResultSet después de usarlo
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    }

    static void mostrarMillorAtacantPerFaccio(Connection conn) throws SQLException {
         // Mostrar la lista de facciones disponibles
    ArrayList<String> faccions = obtenerNombresFaccions(conn);
    System.out.println("Faccions disponibles:");
    for (int i = 0; i < faccions.size(); i++) {
        System.out.println((i + 1) + ". " + faccions.get(i));
    }

    // Solicitar al usuario que elija una facción
    Scanner scanner = new Scanner(System.in);
    System.out.print("Selecciona el número de la facció: ");
    int faccioIndex = scanner.nextInt();

    // Obtener el nombre de la facción seleccionada
    String faccioNom = faccions.get(faccioIndex - 1);

    // Ejecutar la consulta SQL para obtener el mejor atacante de la facción seleccionada
    String query = "SELECT * FROM Personatge WHERE idFaccio IN (SELECT id FROM Faccio WHERE nom = ?) " +
                   "ORDER BY atac DESC LIMIT 1;";
    ResultSet rs = UtilsSQLite.querySelect(conn, query, faccioNom);

    // Mostrar el mejor atacante de la facción
    System.out.println("Millor atacant de la facció '" + faccioNom + "':");
    if (rs.next()) {
        System.out.println("    " + rs.getInt("id") + ", " + rs.getString("nom") +
                           ", Atac: " + rs.getFloat("atac") + ", Defensa: " + rs.getFloat("defensa"));
    } else {
        System.out.println("No hi ha personatges en la facció '" + faccioNom + "'.");
    }
    }

    static void mostrarMillorDefensorPerFaccio(Connection conn) throws SQLException {
        // Mostrar la lista de facciones disponibles
    ArrayList<String> faccions = obtenerNombresFaccions(conn);
    System.out.println("Faccions disponibles:");
    for (int i = 0; i < faccions.size(); i++) {
        System.out.println((i + 1) + ". " + faccions.get(i));
    }

    // Solicitar al usuario que elija una facción
    Scanner scanner = new Scanner(System.in);
    System.out.print("Selecciona el número de la facció: ");
    int faccioIndex = scanner.nextInt();

    // Obtener el nombre de la facción seleccionada
    String faccioNom = faccions.get(faccioIndex - 1);

    // Ejecutar la consulta SQL para obtener el mejor defensor de la facción seleccionada
    String query = "SELECT * FROM Personatge WHERE idFaccio IN (SELECT id FROM Faccio WHERE nom = ?) " +
                   "ORDER BY defensa DESC LIMIT 1;";
    ResultSet rs = UtilsSQLite.querySelect(conn, query, faccioNom);

    // Mostrar el mejor defensor de la facción
    System.out.println("Millor defensor de la facció '" + faccioNom + "':");
    if (rs.next()) {
        System.out.println("    " + rs.getInt("id") + ", " + rs.getString("nom") +
                           ", Atac: " + rs.getFloat("atac") + ", Defensa: " + rs.getFloat("defensa"));
    } else {
        System.out.println("No hi ha personatges en la facció '" + faccioNom + "'.");
    }
    }

    static void initDatabase(String filePath) {
        // Conectar (crea la base de datos si no existe)
        Connection conn = UtilsSQLite.connect(filePath);

        // Eliminar la tabla (si existe)
        UtilsSQLite.queryUpdate(conn, "DROP TABLE IF EXISTS Faccio;");
        UtilsSQLite.queryUpdate(conn, "DROP TABLE IF EXISTS Personatge;");

        // Crear una nueva tabla
        UtilsSQLite.queryUpdate(conn, "CREATE TABLE IF NOT EXISTS Faccio ("
                + "    id integer PRIMARY KEY AUTOINCREMENT,"
                + "    nom VARCHAR(15) NOT NULL,"
                + "    resum VARCHAR(500) NOT NULL);");

        UtilsSQLite.queryUpdate(conn, "CREATE TABLE IF NOT EXISTS Personatge ("
                + "    id integer PRIMARY KEY AUTOINCREMENT,"
                + "    nom VARCHAR(15) NOT NULL,"
                + "    atac FLOAT NOT NULL,"
                + "    defensa FLOAT NOT NULL,"
                + "    idFaccio int, FOREIGN KEY (idFaccio) REFERENCES Faccio (id));");

        // Agregar elementos a la tabla
        UtilsSQLite.queryUpdate(conn, "INSERT INTO Faccio (nom, resum) VALUES ('Cavallers', 'Facció de cavallers valents');");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO Faccio (nom, resum) VALUES ('Vikings', 'Facció de guerrers vikings intrèpids');");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO Faccio (nom, resum) VALUES ('Samurais', 'Facció de samurais disciplinats');");
        
        UtilsSQLite.queryUpdate(conn, "INSERT INTO Personatge (nom, atac, defensa, idFaccio) VALUES ('Warden', 10.5, 8.0, 1);");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO Personatge (nom, atac, defensa, idFaccio) VALUES ('Raider', 9.0, 7.5, 2);");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO Personatge (nom, atac, defensa, idFaccio) VALUES ('Kensei', 8.5, 9.0, 3);");

        UtilsSQLite.queryUpdate(conn, "INSERT INTO Personatge (nom, atac, defensa, idFaccio) VALUES ('Peacekeeper', 9.0, 7.0, 1);");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO Personatge (nom, atac, defensa, idFaccio) VALUES ('Warlord', 8.5, 8.5, 2);");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO Personatge (nom, atac, defensa, idFaccio) VALUES ('Orochi', 9.5, 8.0, 3);");

        UtilsSQLite.queryUpdate(conn, "INSERT INTO Personatge (nom, atac, defensa, idFaccio) VALUES ('Lawbringer', 9.0, 9.0, 1);");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO Personatge (nom, atac, defensa, idFaccio) VALUES ('Berserker', 10.0, 6.5, 2);");
        UtilsSQLite.queryUpdate(conn, "INSERT INTO Personatge (nom, atac, defensa, idFaccio) VALUES ('Shugoki', 8.0, 9.5, 3);");
        System.out.println("Base de datos inicializada");

        // Desconectar
        UtilsSQLite.disconnect(conn);
    }
   
   
   
    static ArrayList<String> obtenerNombresFaccions(Connection conn) throws SQLException {
        ArrayList<String> nombresFacciones = new ArrayList<>();
    
        // Ejecutar la consulta SQL
        String query = "SELECT nom FROM Faccio;";
        ResultSet rs = UtilsSQLite.querySelect(conn, query);
    
        // Almacenar los nombres de las facciones en el ArrayList
        while (rs.next()) {
            nombresFacciones.add(rs.getString("nom"));
        }
    
        // Cerrar el ResultSet después de usarlo
        rs.close();
    
        return nombresFacciones;
    }
}