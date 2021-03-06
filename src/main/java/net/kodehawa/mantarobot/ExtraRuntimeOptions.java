/*
 * Copyright (C) 2016-2018 David Alejandro Rubio Escares / Kodehawa
 *
 * Mantaro is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Mantaro is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Mantaro.  If not, see http://www.gnu.org/licenses/
 */

package net.kodehawa.mantarobot;

import java.util.OptionalInt;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class ExtraRuntimeOptions {
    public static final boolean DISABLE_NON_ALLOCATING_BUFFER = System.getProperty("mantaro.disable-non-allocating-buffer") != null;
    public static final boolean DEBUG = System.getProperty("mantaro.debug") != null;
    public static final boolean DEBUG_LOGS = System.getProperty("mantaro.debug_logs") != null;
    public static final boolean LOG_DB_ACCESS = System.getProperty("mantaro.log_db_access") != null;
    public static final boolean TRACE_LOGS = System.getProperty("mantaro.trace_logs") != null;
    public static final boolean VERBOSE = System.getProperty("mantaro.verbose") != null;
    public static final OptionalInt FROM_SHARD = maybeInt("mantaro.from-shard");
    public static final OptionalInt TO_SHARD = maybeInt("mantaro.to-shard");

    private static OptionalInt maybeInt(String name) {
        try {
            return OptionalInt.of(Integer.parseInt(System.getProperty(name)));
        } catch(Exception e) {
            return OptionalInt.empty();
        }
    }
}
