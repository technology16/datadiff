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
 * Формирует полученные изменения из объекта DataDiff в diff-формат.
 * Запись производится объектом класса PrintWriter.
 */
class DiffPrint implements Diff {

    private static final Logger logger = LoggerFactory.getLogger(DiffPrint.class);

    private final DataDiff dataDiff;
    private int startLineDiff = 0;

    DiffPrint(DataDiff dataDiff) {
        this.dataDiff = dataDiff;
    }

    /**
     * Получить на вывод сформированные diff-строки.
     */
     public void getDiff(PrintWriter printWriter) {

        startLineDiff = 0;
        printWriter.println("\n--- First table\n+++ Second Table");
        dataDiff.getDelData().forEach((id, listDiff) -> delRows(id, listDiff, printWriter));
        printWriter.println("");

        startLineDiff = 0;
        printWriter.println("--- First table\n+++ Second Table");
        dataDiff.getExistRowData().forEach((id, listDiff) -> printDiffExistRow(id, listDiff, printWriter));
        printWriter.println("");

        startLineDiff = 0;
        printWriter.println("--- First table\n+++ Second Table");
        dataDiff.getNewData().forEach((id, listDiff) -> newRows(id, listDiff, printWriter));
        printWriter.println("");

        logger.info("Diff-строки сформированны.");

    }

    /**
     * Печатает полученные строки с изменными данными в diff-формате.
     *
     * @param rowsFirstTable сформированная строка изменных данных с тещущим id из первой таблицы
     * @param rowsSecondTable сформированная строка изменных данных с тещущим id из второй таблицы
     */
    private void printDiff(String rowsFirstTable, String rowsSecondTable, PrintWriter printWriter) {

        startLineDiff++;

        if (!rowsFirstTable.isEmpty() && rowsSecondTable.isEmpty()) {
            printWriter.println("@@ -" + startLineDiff + ",1 +0,0 @@");
            printWriter.println("-" + rowsFirstTable);
        }

        if (!rowsFirstTable.isEmpty() && !rowsSecondTable.isEmpty()) {
            printWriter.println("@@ -" + startLineDiff + ",1 +" + startLineDiff + ",1 @@");
            printWriter.println("-" + rowsFirstTable);
            printWriter.println("+" + rowsSecondTable);
        }

        if (rowsFirstTable.isEmpty() && !rowsSecondTable.isEmpty()) {
            printWriter.println("@@ -0,0 +" + startLineDiff + ",1 @@");
            printWriter.println("+" + rowsSecondTable);
        }

    }

    /**
     * Формирует две строки с измененными данными из первой и второй таблиц.
     *
     * @param id значение id-колонки текущей строки
     * @param listDiff лист с различными данными в колонках текущей строки из двух таблиц
     */
    private void printDiffExistRow(Object id, Map<String, PairChangedValues> listDiff, PrintWriter printWriter) {

        String rowsFirstTable;
        String rowsSecondTable;

        StringBuilder columnsFirstTable = new StringBuilder();
        StringBuilder columnsSecondTable = new StringBuilder();
        columnsFirstTable.append("id: ").append(id.toString()).append(" | ");
        columnsSecondTable.append("id: ").append(id.toString()).append(" | ");

        listDiff.forEach((columnName, diffValue) -> {
            Object valueFirstTable = diffValue.getValueFirstTable();
            Object valueSecondTable = diffValue.getValueSecondTable();
            columnsFirstTable.append(columnName).append(": ").append(valueFirstTable.toString()).append(" | ");
            columnsSecondTable.append(columnName).append(": ").append(valueSecondTable.toString()).append(" | ");
        });

        rowsFirstTable = columnsFirstTable.toString();
        rowsSecondTable = columnsSecondTable.toString();

        printDiff(rowsFirstTable, rowsSecondTable, printWriter);

    }

    /**
     * Формирует строку с новыми данными.
     *
     * @param id значение id-колонки текущей строки
     * @param listDiff лист со значениями в колонках текущей строки
     */
    private void newRows(Object id, Map<String, Object> listDiff, PrintWriter printWriter) {

        String rowsFirstTable = "";
        String rowsSecondTable;

        StringBuilder columnsSecondTable = new StringBuilder();
        columnsSecondTable.append("id: ").append(id.toString()).append(" | ");

        listDiff.forEach((columnName, diffValue) ->
                columnsSecondTable.append(columnName).append(": ").append(diffValue.toString()).append(" | "));

        rowsSecondTable = columnsSecondTable.toString();

        printDiff(rowsFirstTable, rowsSecondTable, printWriter);

    }

    /**
     * Формирует строку с удаленными данными.
     *
     * @param id значение id-колонки текущей строки
     * @param listDiff лист со значениями в колонках текущей строки
     */
    private void delRows(Object id, Map<String, Object> listDiff, PrintWriter printWriter) {

        String rowsFirstTable;
        String rowsSecondTable = "";

        StringBuilder columnsFirstTable = new StringBuilder();
        columnsFirstTable.append("id: ").append(id.toString()).append(" | ");

        listDiff.forEach((columnName, diffValue) ->
                columnsFirstTable.append(columnName).append(": ").append(diffValue.toString()).append(" | "));

        rowsFirstTable = columnsFirstTable.toString();

        printDiff(rowsFirstTable, rowsSecondTable, printWriter);

    }

}
