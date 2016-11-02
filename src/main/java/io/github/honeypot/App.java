package io.github.honeypot;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import io.github.honeypot.exception.HoneypotException;
import io.github.honeypot.listener.TCPListener;
import io.github.honeypot.service.IRCService;
import io.github.honeypot.service.SMTPService;
import io.github.honeypot.service.SSHService;

/**
 * Hello world!
 */
public class App implements ServletContextListener {
    private TCPListener tcpListener;
    private Thread tcpListenerThread;

    public App() throws HoneypotException {

        try {
            tcpListener = new TCPListener();
            tcpListener.addService(6667, IRCService::new);
            tcpListener.addService(6668, SMTPService::new);
            tcpListener.addService(6677, SSHService::new);
        } catch (IOException e) {
            throw new HoneypotException(e);
        }

        tcpListenerThread = new Thread(tcpListener);

        /*
        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setPort(6666);

        AbstractGeneratorHostKeyProvider keyProvider = new SimpleGeneratorHostKeyProvider(new File("hostkey.ser"));
        keyProvider.setAlgorithm("RSA");
        sshd.setKeyPairProvider(keyProvider);

        sshd.setShellFactory(InteractiveProcessShellFactory.INSTANCE);
        sshd.setPasswordAuthenticator((username, password, session) -> {
            System.out.println(username);
            System.out.println(password);
            System.out.println(session);
            return true;
        });
        sshd.setTcpipForwardingFilter(AcceptAllForwardingFilter.INSTANCE);
        sshd.setCommandFactory(new ScpCommandFactory.Builder().withDelegate(
                command -> new ProcessShellFactory(GenericUtils.split(command, ' ')).create()
        ).build());
        sshd.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory()));
        sshd.start();
        */
    }

    @Override
    public final void contextInitialized(ServletContextEvent context) {
        tcpListenerThread.start();
    }

    @Override
    public final void contextDestroyed(ServletContextEvent context) {

    }
}
