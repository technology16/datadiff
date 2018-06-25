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
 * Проверяет идентификатор на допустимые символы и, при необходимости, экранирует их.
 */
final class Identifier {

    private Identifier() { }

    /**
     * Получить строку с экранированными кавычками, если они присутствуют в идентификаторе.
     *
     * @param diffValue объект, который следует проверить
     * @return объект в виде строки с экранированными кавычками
     */
    static String getEscapeIdentifier(Object diffValue) {

        String valueString = diffValue.toString();
        final String escapedSymbol = "\"";
        if (valueString.contains(escapedSymbol)) {
            valueString = valueString.replace(escapedSymbol, "\"\"");
        }
        return checkStringForNotAllowedCharacters(valueString);

    }

    /**
     * Проверяет строку на допустимые символы.
     * Если в строке присутствуют символы кроме строчных букв, цифр и символа подчеркивания,
     * то заключает строку в двойные кавычки.
     *
     * @param valueString строка, которую нужно проверить
     * @return строка в двойных кавычках, если в ней присутсвуют недопустимые символы
     */
    private static String checkStringForNotAllowedCharacters(String valueString) {

        String allowedCharacters = "abcdefghijklmnopqrstuvwxyz0123456789_";
        StringBuilder valueStringBuilder = new StringBuilder();
        for (int i = 0; i < valueString.length(); i++) {
            char charIdentifier = valueString.charAt(i);
            int charIndex = allowedCharacters.indexOf(charIdentifier);
            if (charIndex == -1) {
                valueStringBuilder.append('"').append(valueString).append('"');
                return valueStringBuilder.toString();
            }
        }
        return valueString;

    }

}
