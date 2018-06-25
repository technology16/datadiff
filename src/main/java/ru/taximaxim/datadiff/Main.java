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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            logger.error("SQLServerDriver не найден.", e);
            System.exit(1);
        }

        try (PrintWriter printWriter = new PrintWriter(System.out, true)) {

            ArgsForTable argsForTable = new ArgsForTable();
            try {
                if (!argsForTable.parseArgs(args, printWriter)) {
                    return;
                }
            } catch (IncorrectDataException e) {
                logger.error("Строку с аргументами не удалось спарсить.", e);
                System.exit(1);
            }

            try (Connection connFirstDb = DriverManager.getConnection(argsForTable.getFirstDatabase());
                 Connection connSecDb = DriverManager.getConnection(argsForTable.getSecondDatabase())) {

                logger.info("Соединение с БД установлено.");

                if (!argsForTable.isCompareOneRow()) {

                    TableData firstTableData = new TableData(argsForTable);
                    TableData secondTableData = new TableData(argsForTable);
                    firstTableData.performQuery(connFirstDb);
                    secondTableData.performQuery(connSecDb);

                    ComparatorForTableData comparator = new ComparatorForTableData(
                            firstTableData.getData(), secondTableData.getData(), firstTableData.getColumnNames());
                    comparator.startCompare();

                    printResult(printWriter, argsForTable, comparator.getDataDiff());

                } else {
                    ComparatorForOnePairRowData comparatorForOnePairRowData =
                            new ComparatorForOnePairRowData(connFirstDb, connSecDb, argsForTable);
                    comparatorForOnePairRowData.startCompare();

                    printResult(printWriter, argsForTable, comparatorForOnePairRowData.getDataDiff());
                }

            } catch (SQLException e) {
                logger.error("Соединение не установлено.", e);
                System.exit(1);
            } catch (IncorrectDataException e) {
                logger.error("Запрос не выполнен.", e);
                System.exit(1);
            }

        }

    }

    private static void printResult(PrintWriter printWriter, ArgsForTable argsForTable, DataDiff dataDiff) {
        if (!argsForTable.isDiffResult() && !argsForTable.isSqlResult()) {
            printDiffPrint(printWriter, dataDiff);
            printSqlResult(printWriter, argsForTable, dataDiff);
        } else if (argsForTable.isDiffResult()) {
            printDiffPrint(printWriter, dataDiff);
        } else if (argsForTable.isSqlResult()) {
            printSqlResult(printWriter, argsForTable, dataDiff);
        }
    }

    private static void printSqlResult(PrintWriter printWriter, ArgsForTable argsForTable, DataDiff dataDiff) {
        Diff sqlStatementsPrint = new SqlStatementsPrint(dataDiff,
                argsForTable.getTable(), argsForTable.getIdColumn());
        sqlStatementsPrint.getDiff(printWriter);
    }

    private static void printDiffPrint(PrintWriter printWriter, DataDiff dataDiff) {
        Diff diffPrint = new DiffPrint(dataDiff);
        diffPrint.getDiff(printWriter);
    }

}
