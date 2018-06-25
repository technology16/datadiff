/*
 * ========================LICENSE_START=================================
 * datadiff
 * *
 * Copyright (C) 2018 "Technology" LLC
 * *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package ru.taximaxim.datadiff;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * На основе введенных аргументов из класса ArgsForTable и переданных id
 * производит запросы к БД и сохраняет полученные данные.
 */
class RowData {

    private Map<Object, List<Object>> data = new HashMap<>();

    private final String table;
    private final String tableKey;
    private String idValue;
    private String previousValue;
    private List<String> columnNames;

    private boolean secondTable = false;

    RowData(ArgsForTable argsForTable, String idValue, String previousValue) {
        this.table = argsForTable.getTable();
        this.tableKey = argsForTable.getIdColumn();
        this.idValue = idValue;
        this.previousValue = previousValue;
    }

    RowData (ArgsForTable argsForTable, String idValue, boolean secondTable) {
        this.table = argsForTable.getTable();
        this.tableKey = argsForTable.getIdColumn();
        this.idValue = idValue;
        this.secondTable = secondTable;
    }

    Map<Object, List<Object>> getData() {
        return data;
    }

    String getPreviousValue() {
        return previousValue;
    }

    List<String> getColumnNames() {
        return columnNames;
    }

    String getIdValue() {
        return idValue;
    }

    boolean isSecondTable() {
        return secondTable;
    }

    /**
     * Выполняет запрос к таблице.
     *
     * @param connection строка подключения к БД
     * @return true - в случае удачного запроса, иначе false
     * @throws IncorrectDataException если возникает ошибка доступа к базе данных или
     * этот метод вызывается при закрытом соединении
     */
    boolean performQuery(Connection connection) throws IncorrectDataException {

        data.clear();

        if (!secondTable) {

            try (PreparedStatement preparedStatement = selectRowFromCurrentDb(connection);
                 ResultSet result = preparedStatement.executeQuery()) {

                previousValue = idValue;
                fillDataFromTable(result);

                if (data.size() == 0) {
                    return false;
                }
            } catch (SQLException e) {
                throw new IncorrectDataException(e);
            }
        } else {
            try (PreparedStatement preparedStatement = selectRowWithIdFromFirstTable(connection);
                 ResultSet result = preparedStatement.executeQuery()) {

                fillDataFromTable(result);
                previousValue = idValue;

                if (data.size() == 0) {
                    return false;
                }
            } catch (SQLException e) {
                throw new IncorrectDataException(e);
            }
        }
        return true;

    }

    private PreparedStatement selectRowFromCurrentDb(Connection connection)
            throws IncorrectDataException, SQLException {

        if (connection.getMetaData().getDatabaseProductName().equals("Microsoft SQL Server")) {
            return selectRowMs(connection);
        } else {
            return selectRowPostgres(connection);
        }

    }

    private PreparedStatement selectRowWithIdFromFirstTable(Connection connection) throws IncorrectDataException {

        PreparedStatement preparedStatement;
        try {

            preparedStatement = connection.prepareStatement("SELECT * FROM " + table +
                    " WHERE " + tableKey + " = ?");
            preparedStatement.setInt(1, Integer.parseInt(idValue));

        } catch (SQLException e) {
            throw new IncorrectDataException(e);
        }
        return preparedStatement;

    }

    /**
     * Создает объект класса PreparedStatement для SQL запроса к PostgresSQL.
     *
     * @param connection соединение с конкретной БД.
     * @return объект класса PreparedStatement с выполненным запросом
     * @throws IncorrectDataException если возникает ошибка доступа к базе данных или
     * этот метод вызывается при закрытом соединении
     */
    private PreparedStatement selectRowPostgres(Connection connection) throws IncorrectDataException {

        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM " + table +
                    " WHERE " + tableKey + " > ? ORDER BY " + tableKey + " ASC LIMIT 1");
            preparedStatement.setInt(1, Integer.parseInt(idValue));
        } catch (SQLException e) {
            throw new IncorrectDataException(e);
        }
        return preparedStatement;
    }

    /**
     * Создает объект класса PreparedStatement для SQL запроса к MS SQL.
     *
     * @param connection соединение с конкретной БД.
     * @return объект класса PreparedStatement с выполненным запросом
     * @throws IncorrectDataException если возникает ошибка доступа к базе данных или
     * этот метод вызывается при закрытом соединении
     */
    private PreparedStatement selectRowMs(Connection connection) throws IncorrectDataException {

        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement("SELECT TOP (1) * FROM " + table +
                    " WHERE " + tableKey + " > ? ORDER BY " + tableKey + " ASC");
            preparedStatement.setInt(1, Integer.parseInt(idValue));
        } catch (SQLException e) {
            throw new IncorrectDataException(e);
        }
        return preparedStatement;
    }

    /**
     * Формирует мапу, где ключ - поле с id, значение - лист значений всех колонок.
     *
     * @param result результат запроса
     */
    private void fillDataFromTable(ResultSet result) throws IncorrectDataException {

        createListOfColumnNames(result);

        try {

            while (result.next()) {

                List<Object> oneRow = new ArrayList<>();

                for (String columnName : columnNames) {
                    oneRow.add(result.getObject(columnName));
                }
                data.put(result.getObject(tableKey), oneRow);
                idValue = result.getString(tableKey);

            }

        } catch (SQLException e) {
            throw new IncorrectDataException(e);
        }
    }

    /**
     * Формирует лист состоящий из названий колонок в таблице.
     *
     * @param result результат запроса
     */
    private void createListOfColumnNames(ResultSet result) throws IncorrectDataException {

        List<String> listOfColumnFromTable = new ArrayList<>();

        try {

            ResultSetMetaData rsmd = result.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            for (int i = 1; i <= columnsNumber; i++) {
                String columnName = rsmd.getColumnName(i);

                if (!columnName.equals(tableKey) && !listOfColumnFromTable.contains(columnName)) {
                    listOfColumnFromTable.add(columnName);
                }
            }

        }  catch (SQLException e) {
            throw new IncorrectDataException(e);
        }
        this.columnNames = listOfColumnFromTable;
    }

}
