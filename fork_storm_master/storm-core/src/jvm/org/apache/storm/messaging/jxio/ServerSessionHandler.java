package org.apache.storm.messaging.jxio;

import org.accelio.jxio.EventName;
import org.accelio.jxio.EventReason;
import org.accelio.jxio.Msg;
import org.accelio.jxio.ServerSession;
import org.accelio.jxio.exceptions.JxioGeneralException;
import org.accelio.jxio.exceptions.JxioSessionClosedException;
import org.apache.storm.messaging.TaskMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by admin on 17. 6. 9.
 */
public class ServerSessionHandler {
    private final static Logger LOG = LoggerFactory.getLogger(ServerSessionHandler.class.getCanonicalName());

    private ServerSession session;
    private final String srcIp;

    private final short LOAD_METRICS_NO = -900;
    private final short LOAD_METRICS_REQ = -901;

    public ServerSessionHandler(ServerSession.SessionKey sesKey, Server server, String srcIp) {
        this.srcIp = srcIp;
        session = new ServerSession(sesKey, new ServerSessionCallbacks(server));
    }

    public ServerSession getSession() {
        return session;
    }

    public class ServerSessionCallbacks implements ServerSession.Callbacks {
        private Server server;
        //        private List<TaskMessage> messages = new ArrayList<TaskMessage>();
        private AtomicInteger failure_count;
        private char succMsg = 's';

        public ServerSessionCallbacks(Server server) {
            this.server = server;
            failure_count = new AtomicInteger(0);
        }

        @Override
        public void onRequest(Msg msg) {
//            LOG.info("Server-onRequest, msg info: " + msg.toString());

            ByteBuffer bb = msg.getIn();

            short code = bb.getShort();

            if (code == LOAD_METRICS_REQ) {
                LOG.info("[Server] LoadMetrics message = {}", code);
                MessageBatch mb = new MessageBatch(1);
                try {
                    mb.add(new TaskMessage(-1, server._ser.serialize(Arrays.asList((Object) server.taskToLoad))));
//                    tm = new TaskMessage(-1, server._ser.serialize(Arrays.asList((Object) server.taskToLoad)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    msg.getOut().put(mb.buffer().array());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(code == LOAD_METRICS_NO) {
                try {
                    ControlMessage.LOADMETRICS_NO.write(msg.getOut());
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                msg.getOut().putShort(ControlMessage.LOADMETRICS_NO.write());
            }

            //batch TaskMessage
            Object msgs = decoder(bb);
            if (msgs != null) {
                try {
                    server.received(msgs, srcIp);
                } catch (InterruptedException e) {
                    LOG.info("failed to enqueue a request message", e);
                    failure_count.incrementAndGet();
                    e.printStackTrace();
                }

                /*if (msgs instanceof ByteBuffer) {
                    msg.getOut().put(((ByteBuffer) msgs).array());
                }*/
            }

            try {
                session.sendResponse(msg);
            } catch (JxioGeneralException e) {
                e.printStackTrace();
            } catch (JxioSessionClosedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSessionEvent(EventName eventName, EventReason eventReason) {
            String str = "[ServerSession][EVENT] Got event " + eventName + " because of " + eventReason;
            if (eventName == EventName.SESSION_CLOSED) { // normal exit
                LOG.info(str);
            } else {
                LOG.error(str);
            }
            if (session.getIsClosing()) {
                session = null;
                return;
            }
            LOG.error("[OnSessionEvent] srcIp = {}", srcIp);
            session.close();
//            LOG.info("[ServerSession][EVENT] session size: {}", server.allSessions.size());
        }

        @Override
        public boolean onMsgError(Msg msg, EventReason eventReason) {
            if (session.getIsClosing()) {
                LOG.info("On Message Error while closing. Reason is = " + eventReason + "drop Msg = {}, srcIp = {}", msg.toString(), srcIp);
            } else {
                LOG.error("On Message Error. Reason is = " + eventReason + " drop Msg = {}, srcIp = {}", msg.toString(), srcIp);
            }
            return true;
        }

        private Object decoder(ByteBuffer buf) {
            long available = buf.remaining();
            if (available < 2) {
                //need more data
                return null;
            }
            List<Object> ret = new ArrayList<>();

            //Use while loop, try to decode as more messages as possible in single call
            while (available >= 2) {

                // Mark the current buffer position before reading task/len field
                // because the whole frame might not be in the buffer yet.
                // We will reset the buffer position to the marked position if
                // there's not enough bytes in the buffer.
                buf.mark();

                short code = buf.getShort();
                available -= 2;

                //case 1: Control message
                //who send controlmessage?
                ControlMessage ctrl_msg = ControlMessage.mkMessage(code);
                if (ctrl_msg != null) {
                    if (ctrl_msg == ControlMessage.EOB_MESSAGE) {
                        continue;
                    } else {
                        return ctrl_msg;
                    }
                }
                //case 2: SaslTokenMeesageRequest
                //skip

                //case 3: TaskMessage
                //Make sure that we have received at least an integer (length)
                if (available < 4) {
                    //need more data
                    buf.reset();
                    break;
                }
                //Read the length field.
                int length = buf.getInt();
                available -= 4;

                if (length <= 0) {
                    ret.add(new TaskMessage(code, null));
                    break;
                }

                //Make sure if there's enough bytes in the buffer.
                if (available < length) {
                    //The whole bytes were not received yet - return null.
                    buf.reset();
                    break;
                }
                available -= length;

                //There's enough bytes in the buffer. Read it.
                byte[] payload = new byte[length];
                buf.get(payload);

                //Successfully decoded a frame.
                //Return a TaskMessage object
                ret.add(new TaskMessage(code, payload));
            }

            if (ret.size() == 0) {
                return null;
            } else {
                return ret;
            }
        }
    }
}
