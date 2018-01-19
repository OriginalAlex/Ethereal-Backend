package io.github.originalalex.ethereal.database;

import io.github.originalalex.ethereal.utils.QueryUtils;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import java.sql.*;

public class DatabaseCommunicator {

    private Connection connection;
    private String dbName;

    private PreparedStatement fetchBet;
    private PreparedStatement fetchAccount;
    private PreparedStatement createAccount;
    private PreparedStatement updateBalance;

    private DatabaseCommunicator(String host, String port, String user, String pass) {
        this.dbName = "ETHEReal";
        String url = "jdbc:mysql://" + host + ":" + port;
        connect(dbName, url, user, pass);
        System.out.println("Database server started on 127.0.0.1:" + port);
    }

    public static DatabaseCommunicator access(String host, String port, String user, String pass) {
        return new DatabaseCommunicator(host, port, user, pass);
    }

    public boolean connect(String database, String url, String user, String pass) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, pass);
            addDefaults("ETHEReal");
            prepareStatements();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void addDefaults(String database) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + database);
        statement.executeUpdate("USE " + database);
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS bets( " +
                "username VARCHAR(20), " + // name of the user who made the bet
                "betid VARCHAR(10) PRIMARY KEY, " + // a unique bet id
                "betsize DECIMAL(7, 5), " +  // values ranging from 0.00000 to 99.99999
                "time VARCHAR(20), " + // the time at which the roll was determined
                "payout DOUBLE, " + // the percent gained if a victory
                "ip VARCHAR(16), " +
                "wincondition VARCHAR(10), " + // the number required to rull under
                "profit DOUBLE, " + // result of the game
                "dievalue TINYINT, " +
                "serverhash VARCHAR(70), " +
                "serversecret VARCHAR(30), " +
                "clienthash VARCHAR (70), " +
                "nonce VARCHAR(20)," +
                "hashused VARCHAR(70))");
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS users(" +
                "username VARCHAR(20) PRIMARY KEY," +
                "passwordhash VARCHAR(70), " + // hash of password
                "balance DECIMAL(11, 6))"); // ETH balance of account
        statement.close();
    }

    private void prepareStatements() throws SQLException {
        fetchBet = connection.prepareStatement("SELECT * FROM bets " +
                "WHERE betid=?");
        createAccount = connection.prepareStatement("INSERT INTO users VALUES(?, ?, 5)");
        fetchAccount = connection.prepareStatement("SELECT * FROM users " +
                "WHERE username=? LIMIT 1");
        updateBalance = connection.prepareStatement("UPDATE users" +
                "SET BALANCE = ? " +
                "WHERE username = ?");
    }

    private ResultSet getBetInformation(String id) {
        try {
            fetchBet.setString(1, id);
            return fetchBet.executeQuery(); // return the row with all this specific bet data
        } catch (SQLException e) {
            return null;
        }
    }

    public void createAccount(String username, String passwordHash) {
        try {
            createAccount.setString(1, username);
            createAccount.setString(2, passwordHash);
            createAccount.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean accountExists(String name) {
        try {
            fetchAccount.setString(1, name);
            return fetchAccount.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet fetch(String name) {
        try {
            fetchAccount.setString(1, name);
            return fetchAccount.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public double fetchBalance(String name) {
        ResultSet rs = fetch(name);
        try {
            if (rs.next()) return rs.getDouble("balance");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void update(String name, double balanceChange) {
        try {
            ResultSet rs = fetch(name);
            double newBalance = rs.getDouble("balance") + balanceChange;
            updateBalance.setDouble(1, newBalance);
            updateBalance.setString(2, name);
            updateBalance.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean disconnect() {
        try {
            connection.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean isValidUser() {
        return true;
    }

    public boolean validCredentials(String username, String passHash) {
        try {
            ResultSet rs = fetch(username);
            if (!rs.next()) return false;
            return rs.getString("passwordhash").equals(passHash);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void wipedb(HttpServerExchange e) {
        QueryUtils.enableCORS(e);
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DROP TABLE bets");
            statement.executeUpdate("DROP TABLE users");
            statement.executeUpdate("DROP DATABASE ETHEReal");
            statement.close();
            addDefaults("ETHEReal");
            e.setStatusCode(StatusCodes.OK);
            e.getResponseSender().send("wiped");
        } catch (SQLException ex) {
            ex.printStackTrace();
            e.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
            e.getResponseSender().send("error");
        }
    }

}
