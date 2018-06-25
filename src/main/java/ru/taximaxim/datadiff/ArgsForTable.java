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

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

/**
 * Парсит и сохраняет введенные аргументы.
 */
public class ArgsForTable {

    private static final Logger logger = LoggerFactory.getLogger(ArgsForTable.class);

    /**
     * Строка подключения к первой БД.
     */
    @Argument(index = 0, metaVar="DB1", usage="connection string of the first database", required = true)
    private String firstDatabase;

    /**
     * Строка подключения ко второй БД.
     */
    @Argument(index = 1, metaVar="DB2", usage="connection string of the second database", required = true)
    private String secondDatabase;

    /**
     * Название таблицы для сравнения.
     */
    @Argument(index = 2, metaVar = "TABLE", usage="name of the table", required = true)
    private String table;

    /**
     * Название id-колонки.
     */
    @Argument(index = 3, metaVar = "ID_COLUMN", usage="id column of the table", required = true)
    private String idColumn;

    /**
     * Значение id-колонки от которой начнется стравнение.
     */
    @Option(name = "--first-id", metaVar = "FIRST_VALUE",
            usage = "the first value of the id string for starting the comparison")
    private int firstValue;

    /**
     * Значение id-колонки на которой закончится стравнение.
     */
    @Option(name = "--second-id", metaVar = "SECOND_VALUE", depends = "--first-id",
            usage = "the second value of the id string for the end of the comparison")
    private int secondValue;

    @Option(name = "--compare-one-row", metaVar = "COMPARE_ONE_ROW",
            usage = "single line comparison method")
    private boolean compareOneRow;

    @Option(name = "--initial-value", metaVar = "INITIAL_VALUE", depends = "--compare-one-row",
            usage = "initial value for starting the comparison from the next line")
    private String initialValue;

    @Option(name = "--diff", metaVar = "DIFF_FORMAT_OF_RESULT", usage = "diff format of result to print")
    private boolean diffResult;

    @Option(name = "--sql", metaVar = "SQL_FORMAT_OF_RESULT", usage = "sql format of result to print")
    private boolean sqlResult;

    @Option(name="--help", help = true, usage = "show help")
    private boolean help;

    @Option(name="--version", help = true, usage="show version")
    private boolean version;

    String getFirstDatabase() {
        return firstDatabase;
    }

    String getSecondDatabase() {
        return secondDatabase;
    }

    String getTable() {
        return table;
    }

    String getIdColumn() {
        return idColumn;
    }

    int getFirstValue() {
        return firstValue;
    }

    int getSecondValue() {
        return secondValue;
    }

    boolean isCompareOneRow() {
        return compareOneRow;
    }

    String getInitialValue() {
        return initialValue;
    }

    boolean isDiffResult() {
        return diffResult;
    }

    boolean isSqlResult() {
        return sqlResult;
    }

    boolean isHelp() {
        return help;
    }

    @Override
    public String toString() {
        return "Введенные аргументы:" +
                "\nDB1: " + firstDatabase +
                ",\nDB2: " + secondDatabase +
                ",\nTABLE: " + table +
                ", ID_COLUMN = " + idColumn +
                ", FIRST_VALUE = " + firstValue +
                ", SECOND_VALUE = " + secondValue;
    }

    /**
     * Парсит строку с аргументами.
     *
     * @param arrayOfArgs массив строк
     */
    boolean parseArgs(String[] arrayOfArgs, PrintWriter printWriter) throws IncorrectDataException {

        CmdLineParser parser = new CmdLineParser(this);

        if (arrayOfArgs.length != 0) {
            try {
                parser.parseArgument(arrayOfArgs);
                logger.info("Строка с аргументами спарсена.");
            } catch (CmdLineException e) {
                throw new IncorrectDataException(e);
            }
        } else {
            help = true;
        }

        if (help) {
            printWriter.println("\nExample string of start app:\n" +
                    "[option] DB1 DB2 TABLE ID_COLUMN");
            parser.printUsage(printWriter, null);
            return false;
        }

        if (version) {
            printVersion(printWriter);
            return false;
        }
        return true;
    }

    private void printVersion(PrintWriter printWriter) throws IncorrectDataException {

        InputStream inputStream = this.getClass().getResourceAsStream("/META-INF/maven/datadiff/datadiff/pom.properties");
        if (inputStream == null) {
            throw new IncorrectDataException("Вывод версии производится через командную строку после сборки проекта.");
        }
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new IncorrectDataException(e);
        }
        String projectVersion = properties.getProperty("version");
        printWriter.println("Data_Diff version: " + projectVersion);

    }


}