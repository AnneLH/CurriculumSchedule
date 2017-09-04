package android.practice.curriculumschedule;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * Created by 630267 on 2017/9/3.
 */

public class WorkRest extends DataSupport {
    private int id;
    @Column(nullable = false,unique = true)
    private int myId;
    @Column(nullable = false,defaultValue = "00:00")
    private String timeBegin;
    @Column(nullable = false,defaultValue = "00:00")
    private String timeEnd;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMyId() {
        return myId;
    }

    public void setMyId(int myId) {
        this.myId = myId;
    }

    public String getTimeBegin() {
        return timeBegin;
    }

    public void setTimeBegin(String timeBegin) {
        this.timeBegin = timeBegin;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
