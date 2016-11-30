package io.github.honeypot.listener;

import io.github.honeypot.exception.HoneypotRuntimeException;
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
import java.security.PublicKey;
import java.util.Collections;

import io.github.honeypot.logger.Log;
import io.github.honeypot.logger.LogType;

/**
 * Created by jackson on 11/2/16.
 */
public class SSHListener extends Listener {
    private LogType logType = LogType.SSH_EVENT;

    private boolean isClosed;
    private SshServer sshd;
    private int port;

    public SSHListener(int port) {
        this.port = port;
        isClosed = false;

        sshd = SshServer.setUpDefaultServer();
        sshd.setPort(port);

        AbstractGeneratorHostKeyProvider keyProvider = new SimpleGeneratorHostKeyProvider(new File("hostkey.ser"));
        keyProvider.setAlgorithm("RSA");
        sshd.setKeyPairProvider(keyProvider);
        sshd.setShellFactory(null);

        sshd.setPublickeyAuthenticator(this::publicKeyAuthenticate);
        sshd.setPasswordAuthenticator(this::passwordAuthenticate);

        sshd.setTcpipForwardingFilter(AcceptAllForwardingFilter.INSTANCE);
        sshd.setCommandFactory(new ScpCommandFactory.Builder().withDelegate(
                command -> new ProcessShellFactory(GenericUtils.split(command, ' ')).create()
        ).build());
        sshd.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory()));
    }

    private boolean passwordAuthenticate(String username, String password, ServerSession session) {
        int remotePort = ((InetSocketAddress) session.getClientAddress()).getPort();
        InetAddress addr = ((InetSocketAddress) session.getClientAddress()).getAddress();

        Log passwordAttemptLog = new Log(logType, addr);
        passwordAttemptLog.setLocalPort(this.port);
        passwordAttemptLog.setRemotePort(remotePort);

        passwordAttemptLog.addProperty("username", username);
        passwordAttemptLog.addProperty("password", password);
        passwordAttemptLog.addProperty("credentials", username+"::"+password);

        passwordAttemptLog.end();

        setChanged();
        notifyObservers(passwordAttemptLog);

        return true;
    }

    private boolean publicKeyAuthenticate(String username, PublicKey key, ServerSession session) {
        int remotePort = ((InetSocketAddress) session.getClientAddress()).getPort();
        InetAddress addr = ((InetSocketAddress) session.getClientAddress()).getAddress();

        Log pubkeyAttemptLog = new Log(logType, addr);
        pubkeyAttemptLog.setLocalPort(this.port);
        pubkeyAttemptLog.setRemotePort(remotePort);

        pubkeyAttemptLog.addProperty("username", username);
        pubkeyAttemptLog.addProperty("key", key.toString());
        pubkeyAttemptLog.addProperty("credentials", username+"::"+key.toString());

        pubkeyAttemptLog.end();

        setChanged();
        notifyObservers(pubkeyAttemptLog);

        return true;
    }

    private static class ShellFactory extends InteractiveProcessShellFactory {
        private static final InteractiveProcessShellFactory INSTANCE = new InteractiveProcessShellFactory();
    }

    @Override
    public void run() {
        try {
            sshd.start();
        } catch (IOException e) {
            throw new HoneypotRuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        sshd.close();
    }
}
