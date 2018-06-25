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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class DiffTest {

    private final String fileName;
    private final Diff diffPrint;

    public DiffTest(Diff diffPrint, String fileName) {
        this.diffPrint = diffPrint;
        this.fileName = fileName;
    }

    @Test
    public void checkCorrectResultPrint() throws IOException {

        byte[] bytesFromFile = Files.readAllBytes(Paths.get(fileName));
        String dataFromFile = new String(bytesFromFile);

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (PrintWriter printWriter = new PrintWriter(byteStream, true)) {
            diffPrint.getDiff(printWriter);
        }

        byte[] byteArray = byteStream.toByteArray();
        String resultData = new String(byteArray);
        assertEquals("Данные различны.",  dataFromFile, resultData);

    }

    /**
     * Проверяет корректное формирование sql и diff выражений.
     */
    @Parameterized.Parameters
    public static Iterable<Object[]> isCorrectSqlStatementsPrint()  {

        Map<Object, List<Object>> firstValues = new HashMap<>();
        Map<Object, List<Object>> secondValues = new HashMap<>();
        firstValues.put(1, Collections.singletonList("1"));
        firstValues.put(2, Collections.singletonList("2"));
        secondValues.put(1, Collections.singletonList("10"));
        secondValues.put(3, Collections.singletonList("30"));

        ComparatorForTableData comparator = new ComparatorForTableData(firstValues, secondValues, Collections.singletonList("column_name"));
        comparator.startCompare();

        DataDiff dataDiff = comparator.getDataDiff();
        String table = "table";
        String idColumn = "id_column";

        return Arrays.asList(new Object[][] {
                {new SqlStatementsPrint(dataDiff, table, idColumn), "src/test/resources/SqlStatements"},
                {new DiffPrint(dataDiff), "src/test/resources/Diff"}
        });
    }

}
