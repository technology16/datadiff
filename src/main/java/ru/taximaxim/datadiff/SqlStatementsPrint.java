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

import java.io.PrintWriter;
import java.util.Map;

/**
 * Формирует полученные изменения из объекта DataDiff в sql-запросы.
 * Запись производится объектом класса PrintWriter.
 */
class SqlStatementsPrint implements Diff {

    private static final Logger logger = LoggerFactory.getLogger(SqlStatementsPrint.class);

    private final DataDiff dataDiff;
    private final String tableName;
    private final String idColumn;

    SqlStatementsPrint(DataDiff dataDiff, String tableName, String idColumn) {
        this.dataDiff = dataDiff;
        this.tableName = tableName;
        this.idColumn = idColumn;
    }

    /**
     * Получить на вывод SQL-выражения с измененными данными.
     */
    public void getDiff(PrintWriter printWriter) {

        printWriter.print("\nDELETE FROM " + Identifier.getEscapeIdentifier(tableName) + " WHERE " +
                                        Identifier.getEscapeIdentifier(idColumn) + " IN (");
        final String[] comma = {""};
        dataDiff.getDelData().forEach((id, value) -> {
            printWriter.print(comma[0]);
            comma[0] = ", ";
            printWriter.print(getEscapeString(id));
        });
        printWriter.print(");\n\n");

        dataDiff.getExistRowData().forEach((id, listDiff) -> updExistRows(id, listDiff, printWriter));
        printWriter.println("");

        printWriter.print("INSERT INTO " + Identifier.getEscapeIdentifier(tableName) + " VALUES ");
        comma[0] = "";
        dataDiff.getNewData().forEach((id, value) -> {
            printWriter.print(comma[0]);
            comma[0] = ", ";
            addNewRows(value, printWriter);
        });
        printWriter.print(";\n");

        logger.info("SQL-запросы сформированны.");

    }

    /**
     * Получить строку с экранированными кавычками, если они присутствуют в названии текущего объекта.
     *
     * @param diffValue объект, который следует проверить
     * @return объект в виде строки с экранированными кавычками
     */
    private String getEscapeString(Object diffValue) {

        String valueString = diffValue.toString();
        final String escapedSymbol = "'";
        if (valueString.contains(escapedSymbol)) {
            valueString = valueString.replace(escapedSymbol, "''");
        }
        valueString = "'" + valueString + "'";
        return valueString;

    }

    /**
     * Формирует SQL-выражение для обновления данных в колонках текущей строки.
     *
     * @param id значение id-колонки текущей строки
     * @param listDiff лист с различными данными в колонках текущей строки из двух таблиц
     */
    private void updExistRows(Object id, Map<String, PairChangedValues> listDiff, PrintWriter printWriter) {

        printWriter.print("UPDATE " + Identifier.getEscapeIdentifier(tableName) + " SET ");
        final String[] comma = {""};
        listDiff.forEach((columnName, diffValue) -> {
            printWriter.print(comma[0]);
            comma[0] = ", ";
            printWriter.print(Identifier.getEscapeIdentifier(columnName) + " = " +
                    getEscapeString(diffValue.getValueSecondTable()));
        });
        printWriter.print(" WHERE " + Identifier.getEscapeIdentifier(idColumn) + " = " + getEscapeString(id) + ";\n");

    }

    /**
     * Формирует строку с новыми данными.
     *
     * @param listDiff лист со значениями в колонках текущей строки
     */
    private void addNewRows(Map<String, Object> listDiff, PrintWriter printWriter) {

        printWriter.print("(");
        final String[] comma = {""};
        listDiff.forEach((columnName, diffValue) -> {
            printWriter.print(comma[0]);
            comma[0] = ", ";
            printWriter.print(getEscapeString(diffValue));
        });
        printWriter.print(")");

    }


}
