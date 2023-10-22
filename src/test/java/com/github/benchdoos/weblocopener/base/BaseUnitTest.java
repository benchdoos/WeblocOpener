package com.github.benchdoos.weblocopener.base;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Path;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@Tag("weblocopener-unit-test")
public abstract class BaseUnitTest {

    @TempDir
    public static Path tempDir;
    public static final WireMockConfiguration wiremockConfig =
            wireMockConfig().port(findAvailablePort(8080));

    @RegisterExtension
    public static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wiremockConfig)
            .build();

    static {
        WireMock.configureFor(new WireMock(wireMockServer));
    }


    private static int findAvailablePort(int desiredPort) {
        int port = desiredPort;
        while (port < 65536) {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                serverSocket.close();
                return port;
            } catch (final IOException e) {
                // Port is not available, try the next one
                port++;
            }
        }
        throw new RuntimeException("No available port found.");
    }
}
