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

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Производит сравнение между данными из объектов RowData.
 * На основе этих данных формирует объект DataDiff.
 */
class ComparatorForOnePairRowData {

    private static final Logger logger = LoggerFactory.getLogger(ComparatorForOnePairRowData.class);

    private DataDiff dataDiff = new DataDiff();
    private List<String> columnNames;
    private RowData firstRowData;
    private RowData secondRowData;
    private Connection connFirstDb;
    private Connection connSecondDb;
    private ArgsForTable argsForTable;

    ComparatorForOnePairRowData(Connection connFirstDb, Connection connFSecondDb, ArgsForTable argsForTable) {
        this.connFirstDb = connFirstDb;
        this.connSecondDb = connFSecondDb;
        this.firstRowData = new RowData(argsForTable, argsForTable.getInitialValue(), argsForTable.getInitialValue());
        this.argsForTable = argsForTable;
    }

    DataDiff getDataDiff() {
        return dataDiff;
    }

    /**
     * Сравнивает строку из первой и второй таблиц. При нахождении различных данных
     * записывает данные в соответствующие поля объекта класса DataDiff.
     *
     * @throws IncorrectDataException если возникает ошибка доступа к базе данных или
     * этот метод вызывается при закрытом соединении
     */
    void startCompare() throws IncorrectDataException {

        do {
            compareRowDataObj(argsForTable, connFirstDb, connSecondDb);

            if (!firstRowData.isSecondTable()) {
                firstRowData = new RowData(argsForTable, firstRowData.getIdValue(),
                        firstRowData.getPreviousValue());
            } else {
                secondRowData = new RowData(argsForTable, secondRowData.getIdValue(),
                        secondRowData.getPreviousValue());
            }

        } while (!(firstRowData.getPreviousValue().equals(firstRowData.getIdValue()) &&
                secondRowData.getPreviousValue().equals(secondRowData.getIdValue())));

        logger.info("Таблицы сравнены.");

    }

    private void compareRowDataObj(ArgsForTable argsForTable, Connection connFirstDb, Connection connSecondDb)
            throws IncorrectDataException {


        if (firstRowData.performQuery(connFirstDb) && !firstRowData.isSecondTable()) {

            columnNames = firstRowData.getColumnNames();
            Map<Object, List<Object>> firstRow = firstRowData.getData();
            for (Map.Entry<Object, List<Object>> entry : firstRow.entrySet()) {

                Object id = entry.getKey();
                List<Object> firstListColumnValues = entry.getValue();

                secondRowData = new RowData(argsForTable, firstRowData.getIdValue(), true);
                if (!secondRowData.performQuery(connSecondDb)) {
                    addDelRow(id, firstListColumnValues);
                } else {
                    Map<Object, List<Object>> secondRow = secondRowData.getData();
                    addChangesBetweenExistRow(id, firstListColumnValues, secondRow);
                }

            }

        } else {
            compareSecondTableWithFirst(argsForTable, connFirstDb, connSecondDb);
        }

    }

    private void compareSecondTableWithFirst(ArgsForTable argsForTable, Connection connFirstDb, Connection connSecondDb)
            throws IncorrectDataException {

        if (secondRowData.isSecondTable()) {
            secondRowData = new RowData(argsForTable, argsForTable.getInitialValue(), argsForTable.getInitialValue());
        }
        if (secondRowData.performQuery(connSecondDb)) {
            firstRowData = new RowData(argsForTable, secondRowData.getIdValue(), true);
            if (!firstRowData.performQuery(connFirstDb)) {
                Map<Object, List<Object>> secondRow = secondRowData.getData();
                addNewRow(secondRow);
            }
        }

    }

    private void addDelRow(Object id, List<Object> firstListColumnValues) {

        Map<String, Object> delColumns = new HashMap<>();

        for (int i = 0; i < firstListColumnValues.size(); i++) {
            Object firstDataColumnValue = firstListColumnValues.get(i);
            delColumns.put(columnNames.get(i), firstDataColumnValue);
        }

        dataDiff.addDelData(id, delColumns);

    }

    private void addChangesBetweenExistRow(Object id, List<Object> firstListColumnValues,
                                           Map<Object, List<Object>> secondRow) {

        List<Object> secondListColumnValues = secondRow.get(id);

        Map<String, PairChangedValues> diffColumns = setDiffColumns(firstListColumnValues, secondListColumnValues);
        if (diffColumns.size() != 0) {
            dataDiff.addExistRowData(id, diffColumns);
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

    private void addNewRow(Map<Object, List<Object>> secondRow) {

        secondRow.forEach((id, secondListColumnValues) -> {

            Map<String, Object> newColumns = new HashMap<>();
            for (int i = 0; i < secondListColumnValues.size(); i++) {
                Object secondDataColumnValue = secondListColumnValues.get(i);
                newColumns.put(columnNames.get(i), secondDataColumnValue);
            }
            dataDiff.addNewData(id, newColumns);

        });

    }


}
