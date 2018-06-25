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

/**
 * Хранит пару измененных значений.
 */
class PairChangedValues {

    private final Object valueFirstTable;
    private final Object valueSecondTable;

    PairChangedValues(Object valueFirstTable, Object valueSecondTable) {
        this.valueFirstTable = valueFirstTable;
        this.valueSecondTable = valueSecondTable;
    }

    Object getValueFirstTable() {
        return valueFirstTable;
    }

    Object getValueSecondTable() {
        return valueSecondTable;
    }

    @Override
    public String toString() {
        return "PairChangedValues{" +
                "valueFirstTable=" + valueFirstTable +
                ", valueSecondTable=" + valueSecondTable +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PairChangedValues that = (PairChangedValues) o;

        if (valueFirstTable != null ? !valueFirstTable.equals(that.valueFirstTable) : that.valueFirstTable != null)
            return false;
        return valueSecondTable != null ? valueSecondTable.equals(that.valueSecondTable) : that.valueSecondTable == null;
    }

    @Override
    public int hashCode() {
        int result = valueFirstTable != null ? valueFirstTable.hashCode() : 0;
        result = 31 * result + (valueSecondTable != null ? valueSecondTable.hashCode() : 0);
        return result;
    }
}
