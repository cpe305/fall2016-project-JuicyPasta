package io.github.honeypot.listener;

import org.apache.sshd.common.Closeable;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.forward.AcceptAllForwardingFilter;
import org.apache.sshd.server.keyprovider.AbstractGeneratorHostKeyProvider;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.scp.ScpCommandFactory;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.InteractiveProcessShellFactory;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PublicKey;
import java.util.Collections;

import io.github.honeypot.logger.EventDatabase;
import io.github.honeypot.logger.Log;
import io.github.honeypot.logger.ServiceLogType;

/**
 * Created by jackson on 11/2/16.
 */
public class SSHListener implements AutoCloseable {
    private boolean isClosed;
    private SshServer sshd;

    public SSHListener(int port) {
        isClosed = false;

        sshd = SshServer.setUpDefaultServer();
        sshd.setPort(port);

        AbstractGeneratorHostKeyProvider keyProvider = new SimpleGeneratorHostKeyProvider(new File("hostkey.ser"));
        keyProvider.setAlgorithm("RSA");
        sshd.setKeyPairProvider(keyProvider);
        sshd.setShellFactory(ShellFactory.INSTANCE);

        sshd.setPublickeyAuthenticator(this::publicKeyAuthenticate);
        sshd.setPasswordAuthenticator(this::passwordAuthenticate);

        sshd.setTcpipForwardingFilter(AcceptAllForwardingFilter.INSTANCE);
        sshd.setCommandFactory(new ScpCommandFactory.Builder().withDelegate(
                command -> new ProcessShellFactory(GenericUtils.split(command, ' ')).create()
        ).build());
        sshd.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory()));
    }

    private boolean passwordAuthenticate(String username, String password, ServerSession session) {
        InetAddress addr = ((InetSocketAddress) session.getClientAddress()).getAddress();

        Log passwordAttemptLog = new Log("SshPasswordAttempt", addr);

        passwordAttemptLog.addProperty("username", username);
        passwordAttemptLog.addProperty("password", password);

        passwordAttemptLog.end();

        EventDatabase.logEvent(ServiceLogType.SSH_EVENT, passwordAttemptLog);

        return true;
    }

    private boolean publicKeyAuthenticate(String username, PublicKey key, ServerSession session) {
        InetAddress addr = ((InetSocketAddress) session.getClientAddress()).getAddress();

        Log pubkeyAttemptLog = new Log("SshPubkeyAttemptLog", addr);

        pubkeyAttemptLog.addProperty("username", username);
        pubkeyAttemptLog.addProperty("key", key.toString());

        pubkeyAttemptLog.end();

        EventDatabase.logEvent(ServiceLogType.SSH_EVENT, pubkeyAttemptLog);

        return true;
    }

    private static class ShellFactory extends InteractiveProcessShellFactory {
        private static final InteractiveProcessShellFactory INSTANCE = new InteractiveProcessShellFactory();
    }

    public void start() throws IOException {
        sshd.start();
    }

    @Override
    public void close() throws IOException {
        sshd.close();
    }
}
