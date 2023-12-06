package Ex;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainApp
{
    public static void adaugarePersoane (Connection connection, String nume, int varsta) {
        String sql="insert into persoane (nume,varsta) values (?,?)";
        try(PreparedStatement ps=connection.prepareStatement(sql)) {
            ps.setString(1, nume);
            ps.setInt(2, varsta);
            ps.executeUpdate();
            System.out.println("Persoana a fost adaugata!");
        } catch (SQLException e) {
            System.out.println(sql);
            e.printStackTrace();
        }
    }

    public static int verificaVarsta (int varsta)
    {
        if (varsta > 0 && varsta <= 100)
            return 1;
        return 0;
    }

    public static int getIdPersoanaByNume(Connection connection, String nume) {
        try {
            String sql = "SELECT id FROM persoane WHERE nume = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, nume);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void adaugareExcursie(Connection connection, String nume, String destinatie, int anul) {
        try {
            int idPersoana = getIdPersoanaByNume(connection, nume);

            if (idPersoana == -1) {
                System.out.println("Persoana cu numele " + nume + " nu exista in tabela persoane.");
                return;
            }

            String adaugareExcursieSQL = "INSERT INTO excursii (id_persoana, destinatia, anul) VALUES (?, ?, ?)";
            try (PreparedStatement adaugareExcursieStmt = connection.prepareStatement(adaugareExcursieSQL)) {
                adaugareExcursieStmt.setInt(1, idPersoana);
                adaugareExcursieStmt.setString(2, destinatie);
                adaugareExcursieStmt.setInt(3, anul);
                adaugareExcursieStmt.executeUpdate();
                System.out.println("Excursia a fost adaugata cu succes!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void afisarePersoaneCuExcursii(Connection connection) {
        try {
            String sql = "SELECT p.nume, e.destinatia, e.anul " +
                    "FROM persoane p " +
                    "LEFT JOIN excursii e ON p.id = e.id_persoana";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    System.out.println("Persoanele si excursiile lor:");

                    while (resultSet.next()) {
                        String nume = resultSet.getString("nume");
                        String destinatie = resultSet.getString("destinatia");
                        int anul = resultSet.getInt("anul");

                        System.out.println("Nume: " + nume + ", Destinatie: " + destinatie + ", Anul: " + anul);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void afisareExcursiiPersoana(Connection connection, String numePersoana) {
        try {
            String sql = "SELECT p.nume, e.destinatia, e.anul " +
                    "FROM persoane p " +
                    "JOIN excursii e ON p.id = e.id_persoana " +
                    "WHERE p.nume = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, numePersoana);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    System.out.println("Excursiile persoanei " + numePersoana + ":");

                    while (resultSet.next()) {
                        String nume = resultSet.getString("nume");
                        String destinatie = resultSet.getString("destinatia");
                        int anul = resultSet.getInt("anul");

                        System.out.println("Nume: " + nume + ", Destinatie: " + destinatie + ", Anul: " + anul);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void afisarePersoaneDupaDestinatie(Connection connection, String destinatie) {
        try {
            String sql = "SELECT p.nume, e.destinatia, e.anul " +
                    "FROM persoane p " +
                    "JOIN excursii e ON p.id = e.id_persoana " +
                    "WHERE e.destinatia = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, destinatie);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    System.out.println("Persoanele care au vizitat destinatia " + destinatie + ":");

                    while (resultSet.next()) {
                        String nume = resultSet.getString("nume");
                        int anul = resultSet.getInt("anul");

                        System.out.println("Nume: " + nume + ", Anul: " + anul);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void afisarePersoaneDupaAn(Connection connection, int an) {
        try {
            String sql = "SELECT p.nume, e.destinatia, e.anul " +
                    "FROM persoane p " +
                    "JOIN excursii e ON p.id = e.id_persoana " +
                    "WHERE e.anul = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, an);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    System.out.println("Persoanele care au facut excursii in anul " + an + ":");

                    while (resultSet.next()) {
                        String nume = resultSet.getString("nume");
                        String destinatie = resultSet.getString("destinatia");

                        System.out.println("Nume: " + nume + ", Destinatie: " + destinatie);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void stergeExcursie(Connection connection, int idExcursie) {
        try {
            String sql = "DELETE FROM excursii WHERE id_excursie = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, idExcursie);
                int rowCount = preparedStatement.executeUpdate();

                if (rowCount > 0) {
                    System.out.println("Excursia cu id-ul " + idExcursie + " a fost stearsa cu succes!");
                } else {
                    System.out.println("Excursia cu id-ul " + idExcursie + " nu a fost gasita.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void stergePersoana(Connection connection, String numePersoana) {
        int idPersoana = getIdPersoanaByNume(connection, numePersoana);

        if (idPersoana == -1) {
            System.out.println("Persoana cu numele " + numePersoana + " nu a fost gasita.");
            return;
        }

        try {
            String sqlExcursii = "DELETE FROM excursii WHERE id_persoana = ?";
            try (PreparedStatement preparedStatementExcursii = connection.prepareStatement(sqlExcursii)) {
                preparedStatementExcursii.setInt(1, idPersoana);
                preparedStatementExcursii.executeUpdate();
            }

            String sqlPersoana = "DELETE FROM persoane WHERE id = ?";
            try (PreparedStatement preparedStatementPersoana = connection.prepareStatement(sqlPersoana)) {
                preparedStatementPersoana.setInt(1, idPersoana);
                int rowCount = preparedStatementPersoana.executeUpdate();

                if (rowCount > 0) {
                    System.out.println("Persoana cu numele " + numePersoana + " a fost stearsa cu succes!");
                } else {
                    System.out.println("Persoana cu numele " + numePersoana + " nu a fost gasita.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException
    {
        String url = "jdbc:mysql://localhost:3306/lab8";
        Connection connection = DriverManager.getConnection (url, "root", "root");

        do
        {
            System.out.println("0. Iesire");
            System.out.println("1. Adaugarea unei persoane in tabela persoane.");
            System.out.println("2. Adaugarea unei excursii in tabela excursii.");
            System.out.println("3. Afisarea tuturor persoanelor şi pentru fiecare persoana a excursiilor in care a fost");
            System.out.println("4. Afisarea excursiilor in care a fost o persoana al carei nume se citeste de la tastatura");
            System.out.println("5. Afisarea tuturor persoanelor care au vizitat o anumita destinatie.");
            System.out.println("6. Afisarea persoanelor care au facut excursii intr-un an introdus");
            System.out.println("7. Stergerea unei excursii");
            System.out.println("8. Stergerea unei persoane (impreuna cu excursiile in care a fost)");

            System.out.println();
            System.out.print("Alegeti optiunea: ");

            Scanner sc = new Scanner(System.in);
            int opt = sc.nextInt();
            System.out.println();

            String nume;
            int varsta;

            switch (opt)
            {
                case 0:
                    connection.close();
                    System.exit(0);
                case 1:
                    System.out.print("Nume: ");
                    nume = sc.next();
                    do
                    {
                        System.out.print("Varsta: ");
                        varsta = sc.nextInt();
                    } while (verificaVarsta(varsta) == 0);
                    adaugarePersoane(connection, nume, varsta);
                    break;
                case 2:
                {
                    System.out.print("Nume Persoana: ");
                    String numePersoana = sc.next();

                    System.out.print("Destinatie: ");
                    String destinatie = sc.next();

                    System.out.print("Anul: ");
                    int anul = sc.nextInt();

                    adaugareExcursie(connection, numePersoana, destinatie, anul);
                    break;
                }
                case 3:
                    afisarePersoaneCuExcursii(connection);
                    break;
                case 4:
                {
                    System.out.print("Nume Persoana: ");
                    String numePersoana = sc.next();
                    afisareExcursiiPersoana(connection, numePersoana);
                    break;
                }
                case 5:
                    System.out.print("Destinatie: ");
                    String destinatie = sc.next();
                    afisarePersoaneDupaDestinatie(connection, destinatie);
                    break;
                case 6:
                    System.out.print("An: ");
                    int an = sc.nextInt();
                    afisarePersoaneDupaAn(connection, an);
                    break;
                case 7:
                    System.out.print("ID Excursie: ");
                    int idExcursie = sc.nextInt();
                    stergeExcursie(connection, idExcursie);
                    break;
                case 8:
                    System.out.print("Nume Persoana: ");
                    String numePersoana = sc.next();
                    stergePersoana(connection, numePersoana);
                    break;
                default:
                    System.out.println("Optiune gresita!");
                    break;
            }
            System.out.println();
        } while(true);
    }
}
