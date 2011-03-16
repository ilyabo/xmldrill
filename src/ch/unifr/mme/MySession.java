package ch.unifr.mme;

import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.protocol.http.request.WebClientInfo;

/**
 * @author Ilya Boyandin
 */
public class MySession extends WebSession {

    private static final long serialVersionUID = 8287853701460849970L;
    private String nickName;

    public MySession(Request request) {
        super(request);
    }

    public static MySession get() {
        return (MySession)Session.get();
    }
 
    public synchronized boolean isAuthenticated() {
        return nickName != null;
    }
    
    public synchronized String getNickName() {
        return nickName;
    }

    public synchronized void setNickName(String userName) {
        this.nickName = userName;
    }

    public int getPoints(String exercise) {
        TMExercisesApplication app = (TMExercisesApplication)getApplication();
        return app.getRankings().getPoints(exercise, getNickname(), getIp());
    }

    public int addPoints(String exercise, int points) {
        TMExercisesApplication app = (TMExercisesApplication)getApplication();
        return app.getRankings().addPoints(exercise, getNickname(), getIp(), points);
    }

    private String getNickname() {
        MySession mySession = MySession.get();
        if (!mySession.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated (no nickname)");
        }
        return mySession.getNickName();
    }

    private String getIp() {
        WebClientInfo ci = (WebClientInfo) Session.get().getClientInfo();
        return ci.getProperties().getRemoteAddress();
    }

}
