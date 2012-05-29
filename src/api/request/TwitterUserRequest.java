package api.request;

import com.android.dataframework.Entity;

public class TwitterUserRequest implements BaseRequest {

    private int column;
    private Entity user;

    public TwitterUserRequest(int column, Entity user) {
        this.column = column;
        this.user = user;
    }

    public int getColumn() {
        return column;
    }
    public void setColumn(int column) {
        this.column = column;
    }

    public Entity getUser() {
        return user;
    }
    public void setUser(Entity user) {
        this.user = user;
    }

}
