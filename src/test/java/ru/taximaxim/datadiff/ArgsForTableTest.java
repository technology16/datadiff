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

import org.junit.Test;

import java.io.PrintWriter;

import static org.junit.Assert.*;

public class ArgsForTableTest {

    /**
     * Проверяет парсинг аргументов.
     */
    @Test
    public void checkParseArgs() throws IncorrectDataException {

        String db1 = "DB1";
        String db2 = "DB2";
        String table = "table";
        String idColumn = "id column";
        String valueOfFirstId = "1";
        String valueOfSecondId = "10";

        String[] argsArray = {db1, db2, table, idColumn, "--first-id", valueOfFirstId, "--second-id", valueOfSecondId};
        PrintWriter printWriter = new PrintWriter(System.out, true);
        ArgsForTable argsForTable = new ArgsForTable();
        argsForTable.parseArgs(argsArray, printWriter);

        assertEquals("Аргументы различны", argsForTable.getFirstDatabase(), db1);
        assertEquals("Аргументы различны", argsForTable.getSecondDatabase(), db2);
        assertEquals("Аргументы различны", argsForTable.getTable(), table);
        assertEquals("Аргументы различны", argsForTable.getIdColumn(), idColumn);
        assertEquals("Аргументы различны", argsForTable.getFirstValue(), Integer.parseInt(valueOfFirstId));
        assertEquals("Аргументы различны", argsForTable.getSecondValue(), Integer.parseInt(valueOfSecondId));
        assertFalse("Некорректное значение", argsForTable.isHelp());
        assertFalse("Некорректное значение", argsForTable.isDiffResult());
        assertFalse("Некорректное значение", argsForTable.isSqlResult());


    }
}
