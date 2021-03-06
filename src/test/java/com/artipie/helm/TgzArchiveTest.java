/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 artipie.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.artipie.helm;

import com.artipie.asto.Storage;
import com.artipie.asto.fs.FileStorage;
import io.vertx.reactivex.core.Vertx;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * A test for {@link TgzArchive}.
 *
 * @since 0.2
 * @checkstyle MethodBodyCommentsCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class TgzArchiveTest {

    @Test
    public void nameIdentifiedCorrectly() throws IOException {
        MatcherAssert.assertThat(
            new TgzArchive(
                Files.readAllBytes(
                    Paths.get("./src/test/resources/tomcat-0.4.1.tgz")
                )
            ).name(),
            new IsEqual<>("tomcat-0.4.1.tgz")
        );
    }

    @Test
    public void sizeHasCorrectValue() throws IOException {
        final Path file = Paths.get("./src/test/resources/tomcat-0.4.1.tgz");
        MatcherAssert.assertThat(
            new TgzArchive(
                Files.readAllBytes(file)
            ).size().get(),
            new IsEqual<>(Files.size(file))
        );
    }

    @Test
    public void savedCorrectly(@TempDir final Path tmp) throws IOException {
        final Vertx vertx = Vertx.vertx();
        // @todo #19:30min Replace FileStorage with InMemory one
        //  Currently FileStorage is used in this test, but we need to refactor it to use InMemory
        //  storage.
        final Storage storage = new FileStorage(tmp, vertx.fileSystem());
        new TgzArchive(
            Files.readAllBytes(
                Paths.get("./src/test/resources/tomcat-0.4.1.tgz")
            )
        ).save(storage).blockingGet();
        MatcherAssert.assertThat(
            Files.readAllBytes(
                Paths.get(tmp.toAbsolutePath().toString(), "tomcat-0.4.1.tgz")
            ),
            new IsEqual<>(
                Files.readAllBytes(
                    Paths.get("./src/test/resources/tomcat-0.4.1.tgz")
                )
            )
        );
        vertx.close();
    }
}
