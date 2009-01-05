/*$$
 * Copyright (c) 1999, Trustees of the University of Chicago
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with 
 * or without modification, are permitted provided that the following 
 * conditions are met:
 *
 *	 Redistributions of source code must retain the above copyright notice,
 *	 this list of conditions and the following disclaimer.
 *
 *	 Redistributions in binary form must reproduce the above copyright notice,
 *	 this list of conditions and the following disclaimer in the documentation
 *	 and/or other materials provided with the distribution.
 *
 * Neither the name of the University of Chicago nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE TRUSTEES OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *$$*/
package uchicago.src.sim.analysis;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: thowe
 * Date: Jan 3, 2003
 * Time: 9:41:52 AM
 * To change this template use Options | File Templates.
 */
public class DataServer {
  int port = 4001;
  Selector selector = null;
  ServerSocketChannel selectableChannel = null;
  int keysAdded = 0;
  static final int BUFSIZE = 8;


  public static final String WRITE_FILE = "\007";

  public DataServer() {
  }

  public DataServer( int port ) {
    this.port = port;
  }

  public void initialize() throws IOException {
    selector = SelectorProvider.provider().openSelector();
    selectableChannel = ServerSocketChannel.open();
    selectableChannel.configureBlocking(false);
    InetAddress lh = InetAddress.getLocalHost();
    InetSocketAddress isa = new InetSocketAddress(lh, this.port );
    selectableChannel.socket().bind(isa);
    System.out.println("server initialized");
  }

  public void finalize() throws IOException {
    selectableChannel.close();
    selector.close();
  }

  public void acceptConnections() throws IOException, InterruptedException {
    SelectionKey acceptKey = selectableChannel.register(selector, SelectionKey.OP_ACCEPT);
    System.out.println("accepting connections");
    while ((keysAdded = acceptKey.selector().select()) > 0 ) {
      Set readyKeys = selector.selectedKeys();
      Iterator i = readyKeys.iterator();
      while (i.hasNext()) {
        SelectionKey key = (SelectionKey)i.next();
        i.remove();
        if (key.isAcceptable()) {
          ServerSocketChannel nextReady = (ServerSocketChannel)key.channel();
          SocketChannel channel = nextReady.accept();
          channel.configureBlocking( false );
          SelectionKey readKey = channel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
          readKey.attach(new DataCallback(channel, "ModelName"));
        } else if ( key.isReadable()) {
          key.channel();
          this.readMessage((DataCallback) key.attachment());
        }
      }
    }
  }

  public String decode(ByteBuffer byteBuffer) throws CharacterCodingException {
    Charset charset = Charset.forName("us-ascii");
    CharsetDecoder decoder = charset.newDecoder();
    CharBuffer charBuffer = decoder.decode(byteBuffer);
    String result = charBuffer.toString();
    return result;
  }

  public void readMessage(DataCallback callback) throws IOException, InterruptedException {
    ByteBuffer byteBuffer = ByteBuffer.allocate(BUFSIZE);
    callback.getChannel().read(byteBuffer);
    byteBuffer.flip();
    String result = decode(byteBuffer);
    callback.record(result.toString());
    if(result.indexOf(WRITE_FILE) >= 0){
      callback.execute();
      callback.getChannel().close();
      System.out.println("channel closed");
    }
  }
/*
  public static void main( String[] args ) {

    DataServer nbServer = new DataServer();

    try {
      nbServer.initialize();
    } catch ( IOException e ) {
      e.printStackTrace();
      System.exit( -1 );
    }

    try {
      nbServer.acceptConnections();
    }

    catch ( IOException e ) {
      e.printStackTrace();
      System.out.println( e );
    }

    catch ( InterruptedException e ) {
      System.out.println( "Exiting normally..." );
    }
  }
*/
}
