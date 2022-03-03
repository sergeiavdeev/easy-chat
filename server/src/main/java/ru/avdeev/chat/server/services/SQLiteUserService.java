package ru.avdeev.chat.server.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.avdeev.chat.commons.User;
import ru.avdeev.chat.commons.Utils;

import java.sql.*;

public class SQLiteUserService implements UserService{

    private static SQLiteUserService instance;
    private static final String DB_CONNECTION_STRING = "jdbc:sqlite:server/db/chat.db";
    private final Connection connection;
    private PreparedStatement preparedStatement;
    private final Logger logger = LogManager.getLogger();

    private SQLiteUserService() throws SQLException {

        connection = DriverManager.getConnection(DB_CONNECTION_STRING);

        Statement statement = connection.createStatement();
        connection.setAutoCommit(false);
        statement.execute(DB_CREATE_TABLES);
        statement.execute(DB_CREATE_LOGIN_INDEX);
        statement.execute(DB_USER_INIT);
        connection.setAutoCommit(true);
    }

    public static SQLiteUserService getInstance() throws SQLException {

        if (instance == null) {
            instance = new SQLiteUserService();
        }
        return instance;
    }

    @Override
    public User getUser(int id) {
        try {
            preparedStatement = connection.prepareStatement(DB_SELECT_USER_BY_ID);
            preparedStatement.setInt(1, id);
            logger.trace("Prepare select {}", DB_SELECT_USER_BY_ID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new User(resultSet.getInt(1), resultSet.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e);
        }
        return null;
    }

    @Override
    public User getUserByLogin(String login) {
        try {
            preparedStatement = connection.prepareStatement(DB_SELECT_USER_BY_LOGIN);
            preparedStatement.setString(1, Utils.hash(login));
            logger.trace("Prepare select {}", DB_SELECT_USER_BY_LOGIN);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new User(resultSet.getInt(1), resultSet.getString(2));
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        return null;
    }

    @Override
    public User addUser(User user, String login, String password) {
        try {
            preparedStatement = connection.prepareStatement(DB_ADD_USER, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, Utils.hash(login));
            preparedStatement.setString(2, Utils.hash(password));
            preparedStatement.setString(3, user.getName());
            logger.trace("Prepare insert {}", DB_ADD_USER);
            preparedStatement.execute();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                return new User(rs.getInt(1), user.getName());
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        return null;
    }

    @Override
    public void deleteUser(int id) {

    }

    @Override
    public User auth(String login, String password) {

        try {
            preparedStatement = connection.prepareStatement(DB_SELECT_AUTH_USER);
            preparedStatement.setString(1, Utils.hash(login));
            preparedStatement.setString(2, Utils.hash(password));
            logger.trace("Prepare select {}", DB_SELECT_AUTH_USER);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new User(resultSet.getInt(1), resultSet.getString(2));
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        return null;
    }

    @Override
    public void changeName(int id, String name) {
        try {
            preparedStatement = connection.prepareStatement(DB_UPDATE_USER_NAME);
            preparedStatement.setInt(2, id);
            preparedStatement.setString(1, name);
            logger.trace("Prepare update {}", DB_UPDATE_USER_NAME);
            preparedStatement.execute();
        } catch (SQLException e) {
            logger.error(e);
        }
    }

    @Override
    public void setPassword(int id, String password) {
        try {
            preparedStatement = connection.prepareStatement(DB_UPDATE_USER_PASSWORD);
            preparedStatement.setInt(2, id);
            preparedStatement.setString(1, Utils.hash(password));
            logger.trace("Prepare update {}", DB_UPDATE_USER_PASSWORD);
            preparedStatement.execute();
        } catch (SQLException e) {
            logger.error(e);
        }
    }

    private static final String DB_CREATE_TABLES =
            "CREATE TABLE IF NOT EXISTS user " +
                    "(" +
                    "id integer primary key autoincrement," +
                    "login text," +
                    "password text," +
                    "name text);";

    private static final String DB_CREATE_LOGIN_INDEX =
            "CREATE UNIQUE INDEX IF NOT EXISTS user_login_index on user (login);";

    private static final String DB_USER_INIT =
            "INSERT OR IGNORE INTO user " +
            "VALUES (" +
                "1, " +
                "'8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', " +
                "'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', " +
                "'Administrator');";

    private static final String DB_SELECT_AUTH_USER=
            "SELECT id, name " +
            "FROM user " +
            "WHERE login = ? AND password = ?;";

    private static final String DB_SELECT_USER_BY_LOGIN =
            "SELECT id, name " +
            "FROM user " +
            "WHERE login = ?;";

    private static final String DB_SELECT_USER_BY_ID =
            "SELECT id, name " +
            "FROM user " +
            "WHERE id = ?;";

    private static final String DB_ADD_USER =
            "INSERT INTO user (login, password, name) " +
            "VALUES (?, ?, ?);";

    private static final String DB_UPDATE_USER_NAME =
            "UPDATE user SET name = ? " +
            "WHERE id = ?;";

    private static final String DB_UPDATE_USER_PASSWORD =
            "UPDATE user SET password = ? " +
            "WHERE id = ?;";
}
