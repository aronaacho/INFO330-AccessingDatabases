import java.sql.*;
import java.util.*;

public class TeamAnalyzer {
    // All the "against" column suffixes:
    static String[] types = {
        "bug","dark","dragon","electric","fairy","fight",
        "fire","flying","ghost","grass","ground","ice","normal",
        "poison","psychic","rock","steel","water"
    };

    public static void main(String... args) throws Exception {
        // Take six command-line parameters
        if (args.length < 6) {
            print("You must give me six Pokemon to analyze");
            System.exit(-1);
        }

        // This bit of JDBC magic I provide as a free gift :-)
        // The rest is up to you.
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:../pokemon.sqlite")) {

            for (String arg : args) {
                print("Analyzing " + arg);
                String type1 = "";
                String type2 = "";
                try (Statement statement = connection.createStatement()) {
                    // getting the name of the pokemon with pokedex_number
                    String nameSql = "SELECT name FROM pokemon WHERE id = " + arg ;
                    try (ResultSet results = statement.executeQuery(nameSql)) {
                        while (results.next()) {
                            String name = results.getString("name");
                            System.out.print(name);
                        }
                    }
                    // getting the type of the pokemon
                    System.out.print(" (");
                    String typeSql = "SELECT name FROM type LEFT OUTER JOIN pokemon_type ON " 
                                    + "type.id = pokemon_type.type_id WHERE pokemon_id = " + arg;
                    try (ResultSet results = statement.executeQuery(typeSql)) {
                        int count = 0;
                        while (results.next()) {
                            count++;
                            if (count % 2 == 1) {
                                type1 = results.getString("name");
                                System.out.print(type1 + " ");
                            } else {
                                type2 = results.getString("name");
                                System.out.print(type2);
                            }
                        }
                    }

                    // getting against_XXX of types determining strong against / weak against
                    System.out.print(") is strong against ");
                    List<String> strong = new ArrayList<>();
                    List<String> weak = new ArrayList<>();
                    String againstSql = "SELECT * FROM pokemon_types_battle_view WHERE type1name = '" 
                                        + type1 + "' AND type2name = '" + type2 + "'";

                    try (ResultSet results = statement.executeQuery(againstSql)) {
                        while (results.next()) {
                            for (int i = 0; i < types.length; i++) {
                                Double strength = results.getDouble("against_" + types[i]);
                                if (strength > 1) {
                                    strong.add("'" + types[i] + "'");
                                } else if (strength < 1) {
                                    weak.add("'" + types[i] + "'");
                                }
                            }
                            System.out.print(strong);
                            System.out.println(" but weak against " + weak);
                        }
                    }
                }


                // Analyze the pokemon whose pokedex_number is in "arg"

                // You will need to write the SQL, extract the results, and compare
                // Remember to look at those "against_NNN" column values; greater than 1
                // means the Pokemon is strong against that type, and less than 1 means
                // the Pokemon is weak against that type
            }

            String answer = input("Would you like to save this team? (Y)es or (N)o: ");
            if (answer.equalsIgnoreCase("Y") || answer.equalsIgnoreCase("YES")) {
                String teamName = input("Enter the team name: ");

                // Write the pokemon team to the "teams" table
                print("Saving " + teamName + " ...");
            }
            else {
                print("Bye for now!");
            }
        }        
    }

    /*
     * These are here just to have some symmetry with the Python code
     * and to make console I/O a little easier. In general in Java you
     * would use the System.console() Console class more directly.
     */
    public static void print(String msg) {
        System.console().writer().println(msg);
    }

    /*
     * These are here just to have some symmetry with the Python code
     * and to make console I/O a little easier. In general in Java you
     * would use the System.console() Console class more directly.
     */
    public static String input(String msg) {
        return System.console().readLine(msg);
    }
}
