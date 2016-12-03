package io.github.honeypot;

import io.github.honeypot.connection.Connection;
import io.github.honeypot.connection.TCPConnection;
import io.github.honeypot.exception.HoneypotException;
import io.github.honeypot.listener.PersistenceListener;
import io.github.honeypot.listener.SSHListener;
import io.github.honeypot.logger.HistoryLogConsumer;
import io.github.honeypot.logger.Log;
import io.github.honeypot.logger.LogType;
import io.github.honeypot.logger.RankedAttributeConsumer;
import io.github.honeypot.service.HTTPService;
import io.github.honeypot.service.IRCService;
import io.github.honeypot.service.SMTPService;
import io.github.honeypot.service.Service;
import org.mockito.Mockito;
import org.junit.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

public class AppTest {
    @BeforeClass
    public static void beforeClass() {
        System.out.println("before class");
    }

    @Test
    public void testHttpService() {
        HTTPService service = new HTTPService();
        service.attachLog(new Log(LogType.HTTP_EVENT));
        assert (service.getPreamble() == null);
        assert (service.feed("") != null);
    }

    @Test
    public void testIrcService() {
        IRCService service = new IRCService();
        service.attachLog(new Log(LogType.HTTP_EVENT));
        assert(service.getPreamble() != null);
        assert(service.feed("") != null);
    }

    @Test
    public void testSmtpService() {
        SMTPService service = new SMTPService();
        service.attachLog(new Log(LogType.HTTP_EVENT));
        assert(service.getPreamble() != null);
        assert(service.feed("HELO google.com") != null);
    }

    @Test
    public void testObservations() {
        HistoryLogConsumer testConsumer = new HistoryLogConsumer(10);
        testConsumer.addAcceptableType(LogType.HTTP_EVENT);

        SSHListener fakeListener = new SSHListener(0, ()->new Log(LogType.HTTP_EVENT));

        fakeListener.addObserver(testConsumer);

        Log mockLog = Mockito.mock(Log.class);
        when(mockLog.toJson()).thenReturn(null);

        when(mockLog.getType()).thenReturn(LogType.HTTP_EVENT);
        fakeListener.triggerObserver(mockLog);
        assert(testConsumer.getRecentEvents().length() == 1);

        when(mockLog.getType()).thenReturn(LogType.IRC_EVENT);
        fakeListener.triggerObserver(mockLog);
        assert(testConsumer.getRecentEvents().length() == 1);

        testConsumer.setAcceptAll();
        fakeListener.triggerObserver(mockLog);
        assert(testConsumer.getRecentEvents().length() == 2);
    }

    @Test
    public void testPersistenceListener() throws Exception {
        RankedAttributeConsumer testConsumer = new RankedAttributeConsumer("rank");
        testConsumer.setAcceptAll();

        PersistenceListener logLoader = new PersistenceListener();

        logLoader.addObserver(testConsumer);
        logLoader.reloadLogs("src/test/files/good-logs");

        assert(testConsumer.getLogCount() == 1);
    }

    @Test(expected = IOException.class)
    public void testPersistenceListenerFailure() throws Exception {
        RankedAttributeConsumer testConsumer = new RankedAttributeConsumer("rank");
        testConsumer.setAcceptAll();

        PersistenceListener logLoader = new PersistenceListener();

        logLoader.addObserver(testConsumer);
        logLoader.reloadLogs("src/test/files/bad-logs");
    }

    @Test
    public void testConnection() throws Exception {
        Service mockService = spy(new HTTPService());
        mockService.attachLog(new Log(LogType.HTTP_EVENT));

        Socket mockSocket = mock(Socket.class);
        BufferedReader mockReader = mock(BufferedReader.class);
        PrintWriter mockWriter = mock(PrintWriter.class);
        Log log = mock(Log.class);
        Consumer<Object> mockCallable = mock(Consumer.class);

        when(mockReader.readLine()).thenReturn("<line>", "<second-line>", null);
        when(mockService.getPreamble()).thenReturn("<preamble>");
        when(mockService.feed(anyString())).thenReturn("<response>");

        Connection conn = new TCPConnection(mockService, mockSocket, mockReader, mockWriter, log, mockCallable);
        conn.run();

        verify(mockWriter).println(eq("<preamble>"));

        verify(mockWriter).close();
        verify(mockReader).close();
        verify(mockSocket).shutdownInput();
        verify(mockSocket).shutdownOutput();
        verify(mockSocket).close();

    }
}
