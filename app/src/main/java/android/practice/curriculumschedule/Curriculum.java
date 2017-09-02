package android.practice.curriculumschedule;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * Created by 630267 on 2017/8/31.
 * 课程名称表
 */

public class Curriculum extends DataSupport {
    private int id;

    @Column(unique = true, defaultValue = "unknown",nullable = true)
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
