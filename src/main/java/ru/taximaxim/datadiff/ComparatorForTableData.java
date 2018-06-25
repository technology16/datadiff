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

import java.util.*;

/**
 * Производит сравнение между данными из объектов TableData.
 * На основе этих данных формирует объект DataDiff.
 */
class ComparatorForTableData {

    private static final Logger logger = LoggerFactory.getLogger(ComparatorForTableData.class);

    private DataDiff dataDiff = new DataDiff();
    private final Map<Object, List<Object>> valueFirstTableData;
    private final Map<Object, List<Object>> valueSecondTableData;
    private List<String> columnNames;

    ComparatorForTableData(Map<Object, List<Object>> valueFirstTableData,
                           Map<Object, List<Object>> valueSecondTableData, List<String> columnNames) {

        this.valueFirstTableData = valueFirstTableData;
        this.valueSecondTableData = valueSecondTableData;
        this.columnNames = columnNames;

    }

    DataDiff getDataDiff() {
        return dataDiff;
    }

    /**
     * Сравнивает данные первой и второй таблиц. При нахождении различных данных
     * записывает данные в соответствующие поля объекта класса DataDiff.
     */
    void startCompare() {

        valueFirstTableData.forEach((id, firstListColumnValues) -> {
            addDelRow(id, firstListColumnValues);
            addChangesBetweenExistRow(id, firstListColumnValues);
        });

        addNewRow();
        logger.info("Таблицы сравнены.");

    }

    /**
     * Если строки с данным id не присутствует во второй таблице,
     * то добавляет удаленную строку в соответствующее поле объекта класса DataDiff.
     *
     * @param id значение id-колонки текущей строки
     * @param firstListColumnValues лист содержащий значения колонок текущей строки из первой таблицы
     */
    private void addDelRow(Object id, List<Object> firstListColumnValues) {

        List<Object> secondListColumnValues = valueSecondTableData.get(id);

        if (secondListColumnValues == null) {

            Map<String, Object> delColumns = new HashMap<>();

            for (int i = 0; i < firstListColumnValues.size(); i++) {
                Object firstDataColumnValue = firstListColumnValues.get(i);
                delColumns.put(columnNames.get(i), firstDataColumnValue);
            }

            dataDiff.addDelData(id, delColumns);
        }
    }

    /**
     * Проверяет существующую в обеих таблицах строку с данным id на наличие изменений в колонках.
     * При нахождении различных данных добавляет их в соответствующее поле объекта класса DataDiff.
     *
     * @param id значение id-колонки текущей строки
     * @param firstListColumnValues лист содержащий значения колонок текущей строки из первой таблицы
     */
    private void addChangesBetweenExistRow(Object id, List<Object> firstListColumnValues) {

        List<Object> secondListColumnValues = valueSecondTableData.get(id);

        if (secondListColumnValues != null) {

            Map<String, PairChangedValues> diffColumns = setDiffColumns(firstListColumnValues, secondListColumnValues);
            if (diffColumns.size() != 0) {
                dataDiff.addExistRowData(id, diffColumns);
            }

        }

    }
    
    private Map<String, PairChangedValues> setDiffColumns(List<Object> firstListColumnValues,
                                                          List<Object> secondListColumnValues) {

        Map<String, PairChangedValues> diffColumns = new HashMap<>();

        for (int i = 0; i < firstListColumnValues.size(); i++) {

            Object firstDataColumnValue = firstListColumnValues.get(i);
            Object secondDataColumnValue = secondListColumnValues.get(i);

            if (secondDataColumnValue != null) {

                if (!secondDataColumnValue.equals(firstDataColumnValue)) {
                    putPairChangedValues(diffColumns, i, firstDataColumnValue, secondDataColumnValue);
                }

            } else if (firstDataColumnValue != null) {
                putPairChangedValues(diffColumns, i, firstDataColumnValue, null);
            }

        }
        return diffColumns;

    }

    private void putPairChangedValues(Map<String, PairChangedValues> diffColumns, int i,
                                      Object firstDataColumnValue, Object secondDataColumnValue) {

        PairChangedValues pairChangedValues =
                new PairChangedValues(firstDataColumnValue, secondDataColumnValue);
        diffColumns.put(columnNames.get(i), pairChangedValues);

    }

    /**
     * Если строки с данным id не присутствует в первой таблице,
     * то добавляет новую строку в соответствующее поле объекта класса DataDiff.
     */
    private void addNewRow() {

        valueSecondTableData.forEach((id, secondListColumnValues) -> {

            if (valueFirstTableData.get(id) == null) {

                Map<String, Object> newColumns = new HashMap<>();

                for (int i = 0; i < secondListColumnValues.size(); i++) {
                    Object secondDataColumnValue = secondListColumnValues.get(i);
                    newColumns.put(columnNames.get(i), secondDataColumnValue);
                }

                dataDiff.addNewData(id, newColumns);
            }
        });

    }

}
