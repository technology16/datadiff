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

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class ComparatorTest {

    private DataDiff dataDiff = new DataDiff();
    private static final String columnName = "column_name";

    /**
     * Задает значения для проверки и передает их в ComparatorForTableData.
     */
    @Before
    public void setValuesForComparison() {

        Map<Object, List<Object>> firstValues = new HashMap<>();
        Map<Object, List<Object>> secondValues = new HashMap<>();
        firstValues.put(1, Collections.singletonList("1"));
        firstValues.put(3, Collections.singletonList(null));
        firstValues.put(4, Collections.singletonList("NULL"));
        firstValues.put(5, Collections.singletonList(5));
        firstValues.put(6, Collections.singletonList(null));
        firstValues.put(7, Collections.singletonList(null));
        secondValues.put(1, Collections.singletonList("10"));
        secondValues.put(2, Collections.singletonList("20"));
        secondValues.put(4, Collections.singletonList(null));
        secondValues.put(5, Collections.singletonList(5.0));
        secondValues.put(6, Collections.singletonList("NULL"));
        secondValues.put(7, Collections.singletonList(null));

        ComparatorForTableData comparator = new ComparatorForTableData(firstValues, secondValues, Collections.singletonList(columnName));
        comparator.startCompare();
        dataDiff = comparator.getDataDiff();

    }

    /**
     * Проверяет пару строк с заданными значениями c парой строк с измененными значениями из объекта dataDiff.
     */
    @Test
    public void checkDataDiffValuesExistingRow() {

        Map<Object, Map<String, PairChangedValues>> testRows = new HashMap<>();

        Map<String, PairChangedValues> testChangedValues = new HashMap<>();
        testChangedValues.put(columnName, new PairChangedValues("1", "10"));
        testRows.put(1, testChangedValues);

        Map<String, PairChangedValues> testChangedValues2 = new HashMap<>();
        testChangedValues2.put(columnName, new PairChangedValues(5, 5.0));
        testRows.put(5, testChangedValues2);

        Map<String, PairChangedValues> testChangedValues3 = new HashMap<>();
        testChangedValues3.put(columnName, new PairChangedValues("NULL", null));
        testRows.put(4, testChangedValues3);

        Map<String, PairChangedValues> testChangedValues4 = new HashMap<>();
        testChangedValues4.put(columnName, new PairChangedValues(null, "NULL"));
        testRows.put(6, testChangedValues4);

        Map<Object, Map<String, PairChangedValues>> existRowData = dataDiff.getExistRowData();

        assertEquals("Данные не одинаковы.", testRows, existRowData);

    }

    /**
     * Проверяет заданную строку с новой строкой из объекта dataDiff.
     */
    @Test
    public void checkDataDiffValuesNewRow() {

        Map<Object, Map<String, Object>> testRows = new HashMap<>();
        Map<String, Object> testNewValues = new HashMap<>();
        testNewValues.put(columnName, "20");
        testRows.put(2, testNewValues);
        Map<Object, Map<String, Object>> newRowData = dataDiff.getNewData();

        assertEquals("Данные не одинаковы", testRows, newRowData);

    }

    /**
     * Проверяет заданную строку с удаленной строкой из объекта dataDiff.
     */
    @Test
    public void checkDataDiffValuesDelRow() {

        Map<Object, Map<String, Object>> testRows = new HashMap<>();
        Map<String, Object> testNewValues = new HashMap<>();
        testNewValues.put(columnName, null);
        testRows.put(3, testNewValues);
        Map<Object, Map<String, Object>> delRowData = dataDiff.getDelData();

        assertEquals("Данные не одинаковы.", testRows, delRowData);

    }

}
