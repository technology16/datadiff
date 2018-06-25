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

import static org.junit.Assert.*;

public class IdentifierTest {

    /**
     * Проверяет корректное экранирование строки.
     */
    @Test
    public void checkEscapeIdentifier() {

        String stringTestWithNotAllowedCharacters = "This_String_In_Class_\"IdentifierTest\"_Is_For_Test";
        String escapeString = Identifier.getEscapeIdentifier(stringTestWithNotAllowedCharacters);
        String expectedCorrectString = "\"This_String_In_Class_\"\"IdentifierTest\"\"_Is_For_Test\"";
        assertEquals("Корректное экранирование строк", expectedCorrectString, escapeString);

    }

    /**
     * Проверяет на экранирование допустимой строки.
     */
    @Test
    public void checkCorrectIdentifier() {

        String stringTestWithNotAllowedCharacters = "user_name";
        String escapeString = Identifier.getEscapeIdentifier(stringTestWithNotAllowedCharacters);
        String expectedCorrectString = "user_name";
        assertEquals("Корректное экранирование строк", escapeString, expectedCorrectString);

    }

}
