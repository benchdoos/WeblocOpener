/*
 * (C) Copyright 2019.  Eugene Zrazhevsky and others.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Contributors:
 * Eugene Zrazhevsky <eugene.zrazhevsky@gmail.com>
 */

package com.github.benchdoos.weblocopener.utils.version;

import java.util.StringJoiner;

public class Beta {
    private final boolean beta;
    private final int version;


    public Beta(final int version) {
        beta = (version != 0);
        this.version = version;
    }

    public boolean isBeta() {
        return beta;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public String toString() {
        final StringJoiner stringJoiner =
                new StringJoiner(", ", Beta.class.getSimpleName() + "[", "]")
                        .add("beta=" + beta);

        if (version != 0) {
            stringJoiner.add("version=" + version);
        }

        return stringJoiner.toString();
    }
}
