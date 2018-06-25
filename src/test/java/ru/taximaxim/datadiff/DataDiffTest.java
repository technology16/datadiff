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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotEquals;

public class DataDiffTest {

    /**
     * Проверяет запись заданной строки в поле с удаленными строками обеъкта dataDiff.
     */
    @Test
    public void checkSaveDelRowValues() {

        Map<Object, List<Object>> firstValues = new HashMap<>();
        Map<Object, List<Object>> secondValues = new HashMap<>();

        firstValues.put("10QWE", Arrays.asList("", "", ""));

        ComparatorForTableData comparator = new ComparatorForTableData(firstValues, secondValues, Arrays.asList("first", "second", "third"));
        comparator.startCompare();

        Map<Object, Map<String, Object>> diffDelRows = comparator.getDataDiff().getDelData();

        assertNotEquals("Нет удаленных строк в объекте dataDiff.", diffDelRows.size(), 0);

    }

    /**
     * Проверяет запись заданных строк в поле с измененными строками обеъкта dataDiff.
     */
    @Test
    public void checkSaveExistingRowValues() {

        Map<Object, List<Object>> firstValues = new HashMap<>();
        Map<Object, List<Object>> secondValues = new HashMap<>();

        firstValues.put(10, Arrays.asList("1", "2", "3"));
        secondValues.put(10, Arrays.asList("10", "20", "30"));

        ComparatorForTableData comparator = new ComparatorForTableData(firstValues, secondValues,
                Arrays.asList("first", "second", "third"));
        comparator.startCompare();

        Map<Object, Map<String, PairChangedValues>> diffExistRows = comparator.getDataDiff().getExistRowData();

        assertNotEquals("Нет измененных строк в объекте dataDiff.", diffExistRows.size(), 0);

        diffExistRows.forEach((id, changedColumns) ->
                changedColumns.forEach((columnName, pairChangedValues) ->
                        assertNotEquals("Строки идентичны.", pairChangedValues.getValueFirstTable(),
                                pairChangedValues.getValueSecondTable())));
    }

    /**
     * Проверяет запись заданной строки в поле с новыми строками обеъкта dataDiff.
     */
    @Test
    public void checkSaveNewRowValues() {

        Map<Object, List<Object>> firstValues = new HashMap<>();
        Map<Object, List<Object>> secondValues = new HashMap<>();

        secondValues.put("10QWE", Arrays.asList("1", "2", "3"));

        ComparatorForTableData comparator = new ComparatorForTableData(firstValues, secondValues,
                Arrays.asList("first", "second", "third"));
        comparator.startCompare();

        Map<Object, Map<String, Object>> diffNewRows = comparator.getDataDiff().getNewData();

        assertNotEquals("Нет новых строк в объекте dataDiff.", diffNewRows.size(), 0);

    }

}
