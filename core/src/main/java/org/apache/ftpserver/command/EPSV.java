/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */  

package org.apache.ftpserver.command;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.ftpserver.DataConnectionException;
import org.apache.ftpserver.ServerDataConnectionFactory;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.interfaces.FtpIoSession;
import org.apache.ftpserver.interfaces.FtpServerContext;
import org.apache.ftpserver.util.FtpReplyUtil;

/**
 * The EPSV command requests that a server listen on a data port and
 * wait for a connection.  The EPSV command takes an optional argument.
 * The response to this command includes only the TCP port number of the
 * listening connection.  The format of the response, however, is
 * similar to the argument of the EPRT command.  This allows the same
 * parsing routines to be used for both commands.  In addition, the
 * format leaves a place holder for the network protocol and/or network
 * address, which may be needed in the EPSV response in the future.  The
 * response code for entering passive mode using an extended address
 * MUST be 229.
 */
public 
class EPSV extends AbstractCommand {

    /**
     * Execute command.
     */
    public void execute(FtpIoSession session,
                        FtpServerContext context,
                        FtpRequest request) throws IOException {
        
        // reset state variables
        session.resetState();
        
        // set data connection
        ServerDataConnectionFactory dataCon = session.getDataConnection();
        
        try {
            InetSocketAddress dataConAddress = dataCon.initPassiveDataConnection();
            // get connection info
            int servPort = dataConAddress.getPort();
            
            // send connection info to client
            String portStr = "|||" + servPort + '|';
            session.write(FtpReplyUtil.translate(session, request, context, 229, "EPSV", portStr));
        
        } catch(DataConnectionException e) {
            session.write(FtpReplyUtil.translate(session, request, context, FtpReply.REPLY_425_CANT_OPEN_DATA_CONNECTION, "EPSV", null));
            return;   
        }
    }
}