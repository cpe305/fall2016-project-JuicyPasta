package io.github.honeypot;

import io.github.honeypot.connection.Connection;
import io.github.honeypot.connection.TCPConnection;
import io.github.honeypot.listener.SSHListener;
import io.github.honeypot.LogConsumer.HistoryLogConsumer;
import io.github.honeypot.logger.Log;
import io.github.honeypot.logger.LogFactory;
import io.github.honeypot.logger.LogTap;
import io.github.honeypot.logger.LogType;
import io.github.honeypot.LogConsumer.RankedAttributeConsumer;
import io.github.honeypot.service.*;
import io.github.honeypot.servlet.HistoryServlet;
import org.apache.sshd.server.session.ServerSession;
import org.json.JSONObject;
import org.mockito.Mockito;
import org.junit.*;
import sun.security.ssl.HandshakeInStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.security.PublicKey;
import java.util.function.Consumer;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

public class AppTest {
    @BeforeClass
    public static void beforeClass() {
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
        assert(service.feed("EHLO google.com") != null);
    }

    @Test
    public void testStart() throws Exception {
        App app = new App();
    }

    @Test
    public void testHistoryServletInit() {
        HistoryServlet.loadStaticContext();
        assert(LogTap.getInstance().countObservers() > 0);
    }
    @Test
    public void testHistoryGet() throws Exception {
        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        HttpServletResponse mockRes = mock(HttpServletResponse.class);

        when(mockReq.getQueryString()).thenReturn(null);
        when(mockReq.getPathInfo()).thenReturn("/ALL");

        HistoryServlet testServlet = new HistoryServlet();
        testServlet.doGet(mockReq, mockRes);

        verify(mockReq).getQueryString();
        verify(mockReq).getPathInfo();
        verify(mockRes).getWriter();
    }

    @Test
    public void testHistoryBadGetNoError() throws Exception {
        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        HttpServletResponse mockRes = mock(HttpServletResponse.class);

        when(mockReq.getQueryString()).thenReturn(null);
        when(mockReq.getPathInfo()).thenReturn("/DNE");

        HistoryServlet testServlet = new HistoryServlet();
        testServlet.doGet(mockReq, mockRes);
    }

    @Test
    public void testRankServletInit() {
        HistoryServlet.loadStaticContext();
        assert(LogTap.getInstance().countObservers() > 0);
    }

    @Test
    public void testRankGet() throws Exception {
        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        HttpServletResponse mockRes = mock(HttpServletResponse.class);

        when(mockReq.getQueryString()).thenReturn(null);
        when(mockReq.getPathInfo()).thenReturn("/ALL");

        HistoryServlet testServlet = new HistoryServlet();
        testServlet.doGet(mockReq, mockRes);

        verify(mockRes).getWriter();
        verify(mockRes).setContentType(eq("application/json"));
    }

    @Test
    public void testObservations() {
        HistoryLogConsumer testConsumer = new HistoryLogConsumer();
        testConsumer.addAcceptableType(LogType.HTTP_EVENT);

        SSHListener fakeListener = new SSHListener(0, ()->new Log(LogType.HTTP_EVENT));

        fakeListener.addObserver(testConsumer);

        Log mockLog = Mockito.mock(Log.class);
        when(mockLog.toSmallJson()).thenReturn(new JSONObject());

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
    public void testPersistenceSuccess() throws Exception {
        RankedAttributeConsumer testConsumer = new RankedAttributeConsumer("rank");
        testConsumer.setAcceptAll();

        LogTap.getInstance().addObserver(testConsumer);
        LogTap.getInstance().reloadLogs("src/test/files/good-logs");

        assert(testConsumer.getLogCount() == 1);
    }

    @Test(expected = IOException.class)
    public void testPersistenceFailure() throws Exception {
        RankedAttributeConsumer testConsumer = new RankedAttributeConsumer("rank");
        testConsumer.setAcceptAll();

        LogTap.getInstance().addObserver(testConsumer);
        LogTap.getInstance().reloadLogs("src/test/files/bad-logs");
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

    @Test
    public void testFactories() {
        // verify no error occurs
        ServiceFactory<Service> servFact = IRCService::new;
        LogFactory<Log> LogFact = ()->new Log(LogType.IRC_EVENT);
    }

    @Test
    public void testPasswordAuth() throws Exception {
        Log mockLog = mock(Log.class);
        SSHListener sshListener = new SSHListener(10022, ()-> mockLog);

        ServerSession mockSession = mock(ServerSession.class);
        InetSocketAddress mockAddr = new InetSocketAddress("0.0.0.0", 1234);

        when(mockSession.getClientAddress()).thenReturn(mockAddr);

        boolean in = sshListener.passwordAuthenticate("username", "password", mockSession);

        assertFalse(in);

    }

    @Test
    public void testPubKeyAuth() {
        Log mockLog = mock(Log.class);
        SSHListener sshListener = new SSHListener(10022, ()-> mockLog);

        ServerSession mockSession = mock(ServerSession.class);
        InetSocketAddress mockAddr = new InetSocketAddress("0.0.0.0", 1234);
        PublicKey mockPubKey = mock(PublicKey.class);

        when(mockSession.getClientAddress()).thenReturn(mockAddr);


        boolean in = sshListener.publicKeyAuthenticate("username", mockPubKey, mockSession);

        assertFalse(in);
    }

}
