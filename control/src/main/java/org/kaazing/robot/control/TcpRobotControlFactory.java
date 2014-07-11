package org.kaazing.robot.control;

import java.net.URI;
import java.net.URL;

public class TcpRobotControlFactory extends RobotControlFactorySPI {

    @Override
    public RobotControl newClient(URI controlURI) throws Exception {

        String scheme = controlURI.getScheme();
        if (!"tcp".equals(scheme)) {
            throw new IllegalArgumentException("Unrecognized scheme: " + scheme);
        }

        URL location = new URL(null, controlURI.toASCIIString(), new TcpURLStreamHandler());

        return new TcpRobotControl(location);
    }

    @Override
    public String getSchemeName() {
        return "tcp";
    }
}
