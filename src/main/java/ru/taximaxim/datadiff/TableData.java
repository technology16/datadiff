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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * На основе введенных аргументов из класса ArgsForTable производит запросы к БД
 * и сохраняет полученные данные.
 */
public class TableData {

    private static final Logger logger = LoggerFactory.getLogger(TableData.class);

    private Map<Object, List<Object>> data = new HashMap<>();

    private final String table;
    private final String tableKey;
    private int firstValue;
    private int secondValue;
    private List<String> columnNames;

    public TableData(ArgsForTable argsForTable) {

        this.table = argsForTable.getTable();
        this.tableKey = argsForTable.getIdColumn();

        if (argsForTable.getFirstValue() != 0 && argsForTable.getSecondValue() != 0) {
            this.firstValue = argsForTable.getFirstValue();
            this.secondValue = argsForTable.getSecondValue();
        }

    }

    Map<Object, List<Object>> getData() {
        return data;
    }

    List<String> getColumnNames() {
        return columnNames;
    }

    /**
     * Выполняет запрос к таблице.
     *
     * @param connection строка подключения к БД
     * @throws IncorrectDataException если возникает ошибка доступа к базе данных или
     * этот метод вызывается при закрытом соединении
     */
    void performQuery(Connection connection) throws IncorrectDataException {

        try (PreparedStatement preparedStatement =  createPreparedStatement(connection);
             ResultSet result = preparedStatement.executeQuery()) {

            fillDataFromTable(result);
        } catch (SQLException e) {
            throw new IncorrectDataException(e);
        }
    }

    /**
     * Создает объект класса PreparedStatement для SQL запроса к БД.
     *
     * @param connection соединение с конкретной БД.
     * @return объект класса PreparedStatement с выполненным запросом
     * @throws IncorrectDataException если возникает ошибка доступа к базе данных или
     * этот метод вызывается при закрытом соединении
     */
    private PreparedStatement createPreparedStatement(Connection connection) throws IncorrectDataException {

        PreparedStatement preparedStatement;
        try {
            if (firstValue != 0 && secondValue != 0) {
                preparedStatement = connection.prepareStatement("SELECT * FROM " + table +
                        " WHERE " + tableKey + " BETWEEN ? AND ?");
                preparedStatement.setInt(1, firstValue);
                preparedStatement.setInt(2, secondValue);

            } else {
                preparedStatement = connection.prepareStatement("SELECT * FROM " + table);
            }

            logger.info("Запрос к таблице {} успешно выполнен.", table);

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

            }

            logger.info("Данные из таблицы {} сохранены.", table);

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
