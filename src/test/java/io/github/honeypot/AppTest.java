package io.github.honeypot;

import io.github.honeypot.listener.PersistenceListener;
import io.github.honeypot.listener.SSHListener;
import io.github.honeypot.logger.HistoryLogConsumer;
import io.github.honeypot.logger.Log;
import io.github.honeypot.logger.LogType;
import io.github.honeypot.logger.RankedAttributeConsumer;
import io.github.honeypot.service.HTTPService;
import io.github.honeypot.service.IRCService;
import io.github.honeypot.service.SMTPService;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    public void testHttpService() {
        HTTPService service = new HTTPService();
        service.attachLog(new Log(LogType.HTTP_EVENT));
        assert (service.getPreamble() == null);
        assert (service.feed("") != null);
    }

    public void testIrcService() {
        IRCService service = new IRCService();
        service.attachLog(new Log(LogType.HTTP_EVENT));
        assert(service.getPreamble() != null);
        assert(service.feed("") != null);
    }

    public void testSmtpService() {
        SMTPService service = new SMTPService();
        service.attachLog(new Log(LogType.HTTP_EVENT));
        assert(service.getPreamble() != null);
        assert(service.feed("HELO google.com") != null);
    }

    public void testObservations() {
        HistoryLogConsumer testConsumer = new HistoryLogConsumer(10);
        testConsumer.addAcceptableType(LogType.HTTP_EVENT);

        SSHListener fakeListener = new SSHListener(0, ()->new Log(LogType.HTTP_EVENT));

        fakeListener.addObserver(testConsumer);

        Log mockLog = Mockito.mock(Log.class);
        when(mockLog.toJson()).thenReturn(null);

        mockLog.type = LogType.HTTP_EVENT;
        fakeListener.triggerObserver(mockLog);
        assert(testConsumer.recentEvents.length() == 1);

        mockLog.type = LogType.IRC_EVENT;
        fakeListener.triggerObserver(mockLog);
        assert(testConsumer.recentEvents.length() == 1);

        testConsumer.setAcceptAll();
        fakeListener.triggerObserver(mockLog);
        assert(testConsumer.recentEvents.length() == 2);
    }

    public void testPersistenceListener() throws Exception {
        RankedAttributeConsumer testConsumer = new RankedAttributeConsumer("rank");
        testConsumer.setAcceptAll();

        PersistenceListener logLoader = new PersistenceListener();

        logLoader.addObserver(testConsumer);
        logLoader.reloadLogs("src/test/files/");

        assert(testConsumer.getLogCount() == 1);
    }
}
