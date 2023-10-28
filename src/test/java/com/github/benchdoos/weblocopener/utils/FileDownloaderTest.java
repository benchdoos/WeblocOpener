package com.github.benchdoos.weblocopener.utils;

import com.github.benchdoos.weblocopener.base.WiremockBaseUnitTest;
import com.github.benchdoos.weblocopenercore.service.actions.ActionListener;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.status;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class FileDownloaderTest extends WiremockBaseUnitTest {

    @AfterEach
    void fileCleanUp() {
        //noinspection ResultOfMethodCallIgnored
        Arrays.stream(Objects.requireNonNull(tempDir.toFile().listFiles())).forEach(File::delete);
    }

    @Test
    void downloadMustSucceed() throws IOException {

        final String url = "1.txt";

        final String testBody = "Test body of downloading file";


        final File file = new File(tempDir.toFile(), url);
        assertThat(file).doesNotExist();


        wireMockServer.stubFor(
                get(urlEqualTo("/" + url))
                        .willReturn(aResponse()
                                .withBodyFile("1.txt")
                                .withHeader("Content-Type", "text/plain")
                        )
        );

        final FileDownloader fileDownloader = new FileDownloader(new URL(wiremockAddress + "/" + url), file);

        assertThatCode(fileDownloader::download).doesNotThrowAnyException();
        assertThat(file).exists();
        assertThat(FileUtils.readFileToString(file, StandardCharsets.UTF_8)).isEqualTo(testBody);

    }

    @Test
    void downloadMustSucceedAndListenersWorkFine() throws IOException, URISyntaxException {

        final String url = "1.txt";

        final File file = new File(tempDir.toFile(), url);
        assertThat(file).doesNotExist();


        wireMockServer.stubFor(
                get(urlEqualTo("/" + url))
                        .willReturn(aResponse()
                                .withBodyFile("1.txt")
                                .withHeader("Content-Type", "text/plain")
                        )
        );

        final FileDownloader fileDownloader = new FileDownloader(new URL(wiremockAddress + "/" + url), file);
        final URL resourceURI = new com.github.benchdoos.weblocopener.base.utils.FileUtils().getResourceURI("__files/1.txt");
        fileDownloader.setTotalFileSize(new File(resourceURI.toURI()).length()); // important

        final ActionListener<?> actionListener1 = Mockito.mock(ActionListener.class);
        final ActionListener<?> actionListener2 = Mockito.mock(ActionListener.class);
        fileDownloader.addListener(actionListener1);
        fileDownloader.addListener(actionListener2);


        assertThatCode(fileDownloader::download).doesNotThrowAnyException();
        assertThat(file).exists();

        Mockito.verify(actionListener1, Mockito.atLeastOnce()).actionPerformed(Mockito.any());
        Mockito.verify(actionListener2, Mockito.atLeastOnce()).actionPerformed(Mockito.any());
    }


    @Test
    void downloadMustFail() throws IOException {

        final String url = "1.txt";


        final File file = new File(tempDir.toFile(), url);
        assertThat(file).doesNotExist();


        wireMockServer.stubFor(
                get(urlEqualTo("/" + url))
                        .willReturn(status(404))
        );

        final FileDownloader fileDownloader = new FileDownloader(new URL(wiremockAddress + "/" + url), file);

        assertThatCode(fileDownloader::download).isExactlyInstanceOf(FileNotFoundException.class);
        assertThat(file).doesNotExist();

    }

    @Test
    void addListener() throws MalformedURLException {
        final FileDownloader fileDownloader = new FileDownloader(new URL("http://localhost/1.zip"), new File("1.zip"));
        final ActionListener<?> actionListener = Mockito.mock(ActionListener.class);

        fileDownloader.addListener(actionListener);
        //checking if the same listener added twice
        fileDownloader.addListener(actionListener);

        assertThat(fileDownloader.getListeners()).size().isOne();

//        Mockito.verify(actionListener, Mockito.atLeastOnce()).actionPerformed(Mockito.any());

    }

    @Test
    void removeListener() throws MalformedURLException {
        final FileDownloader fileDownloader = new FileDownloader(new URL("http://localhost/1.zip"), new File("1.zip"));
        final ActionListener<?> actionListener1 = Mockito.mock(ActionListener.class);
        final ActionListener<?> actionListener2 = Mockito.mock(ActionListener.class);

        fileDownloader.addListener(actionListener1);
        fileDownloader.addListener(actionListener2);

        assertThat(fileDownloader.getListeners()).size().isEqualTo(2);

        fileDownloader.removeListener(actionListener1);
        assertThat(fileDownloader.getListeners()).size().isOne();

        assertThat(fileDownloader.getListeners().stream().toList().get(0)).isEqualTo(actionListener2);
    }

    @Test
    void removeAllListeners() throws MalformedURLException {
        final FileDownloader fileDownloader = new FileDownloader(new URL("http://localhost/1.zip"), new File("1.zip"));
        final ActionListener<?> actionListener1 = Mockito.mock(ActionListener.class);
        final ActionListener<?> actionListener2 = Mockito.mock(ActionListener.class);

        fileDownloader.addListener(actionListener1);
        fileDownloader.addListener(actionListener2);

        assertThat(fileDownloader.getListeners()).size().isEqualTo(2);

        fileDownloader.removeAllListeners();

        assertThat(fileDownloader.getListeners()).size().isZero();
    }

    @Test
    void getListeners() throws MalformedURLException {
        final FileDownloader fileDownloader = new FileDownloader(new URL("http://localhost/1.zip"), new File("1.zip"));
        final ActionListener<Integer> actionListener1 = Mockito.mock(ActionListener.class);
        final ActionListener<Integer> actionListener2 = Mockito.mock(ActionListener.class);

        fileDownloader.addListener(actionListener1);
        fileDownloader.addListener(actionListener2);

        assertThat(fileDownloader.getListeners()).size().isEqualTo(2);

        assertThat(fileDownloader.getListeners()).contains(actionListener1, actionListener2);
    }

    @Test
    void getLink() throws MalformedURLException {
        final URL expectedLink = new URL("http://localhost/1.zip");
        final FileDownloader fileDownloader = new FileDownloader(expectedLink, new File("1.zip"));
        assertThat(fileDownloader.getLink()).isEqualTo(expectedLink);
    }

    @Test
    void getFile() throws MalformedURLException {
        final File expectedFile = new File("1.zip");
        final FileDownloader fileDownloader = new FileDownloader(new URL("http://localhost/1.zip"), expectedFile);
        assertThat(fileDownloader.getFile()).isEqualTo(expectedFile);
    }

    @Test
    void getSetTotalFileSize() throws MalformedURLException {
        final FileDownloader fileDownloader = new FileDownloader(new URL("http://localhost/1.zip"), new File("1.zip"));
        final long expectedSize = 1024L;
        fileDownloader.setTotalFileSize(expectedSize);

        assertThat(fileDownloader.getTotalFileSize()).isEqualTo(expectedSize);
    }
}