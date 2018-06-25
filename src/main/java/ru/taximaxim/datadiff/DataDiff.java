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

import java.util.HashMap;
import java.util.Map;

class DataDiff {

    private final Map<Object, Map<String, PairChangedValues>> existRowData = new HashMap<>();

    private final Map<Object, Map<String, Object>> newData = new HashMap<>();

    private final Map<Object, Map<String, Object>> delData = new HashMap<>();

    Map<Object, Map<String, PairChangedValues>> getExistRowData() {
        return existRowData;
    }

    Map<Object, Map<String, Object>> getNewData() {
        return newData;
    }

    Map<Object, Map<String, Object>> getDelData() {
        return delData;
    }

    void addExistRowData(Object id, Map<String, PairChangedValues> columns) {
        existRowData.put(id, columns);
    }

    void addNewData(Object id, Map<String, Object> columns) {
        newData.put(id, columns);
    }

    void addDelData(Object id, Map<String, Object> columns) {
        delData.put(id, columns);
    }

}
